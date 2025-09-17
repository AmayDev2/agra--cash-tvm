package com.amay.tvm.coin.model;

public class CoinDumpResponse extends ModuleResponse {
	public CoinDumpResponse(byte command, byte sequenceNumber, byte[] data) {
		super(command, sequenceNumber, data);
	}

	/**
	 * Check if this is an In Progress response
	 * Spec 4.4.2: Length=0x06, COM_STATE=0x02 (busy), DATA0=hopper, DATA1=0xAC, DATA2=reserved
	 */
	public boolean isProgress() {
		byte[] d = getData();
		// In Progress: length 4 (for 0x06 total), COM_STATE = 0x02, DATA1 = 0xAC
		return d.length == 4 &&
				(d[0] & 0xFF) == 0x02 &&
				(d[2] & 0xFF) == 0xAC;
	}

	/**
	 * Check if this is a Final response
	 * Spec 4.4.3: Length=0x08, COM_STATE in {0x00, 0x01, 0x02, 0x04}
	 */
	public boolean isFinal() {
		byte[] d = getData();
		// Final response: length 6 (for 0x08 total), valid COM_STATE, DATA1 = 0xAC
		if (d.length == 6 && (d[2] & 0xFF) == 0xAC) {
			int comState = d[0] & 0xFF;
			return comState == 0x00 || comState == 0x01 || comState == 0x02 || comState == 0x04;
		}
		return false;
	}

	/**
	 * Get the hopper number from the response (DATA0)
	 */
	public int getHopper() {
		byte[] d = getData();
		return d.length > 1 ? (d[1] & 0xFF) : 0;
	}

	/**
	 * Get the COM_STATE value
	 */
	public int getComState() {
		byte[] d = getData();
		return d.length > 0 ? (d[0] & 0xFF) : 0;
	}

	/**
	 * Get the quantity of dumped coins from Final response
	 * Spec 4.4.3: DATA2=Lower Byte, DATA3=Higher Byte
	 */
	public int getDumpedQuantity() {
		byte[] d = getData();
		if (isFinal() && d.length >= 5) {
			int lowerByte = d[3] & 0xFF;  // DATA2 - Lower Byte
			int higherByte = d[4] & 0xFF; // DATA3 - Higher Byte
			return (higherByte << 8) | lowerByte;
		}
		return 0;
	}

	/**
	 * Get the error code from Final response (DATA4)
	 */
	public int getErrorCode() {
		byte[] d = getData();
		if (isFinal() && d.length >= 6) {
			return d[5] & 0xFF; // DATA4 - Error Code
		}
		return 0;
	}

	/**
	 * Check if the operation was successful (COM_STATE = 0x00)
	 */
	public boolean isSuccessful() {
		return isFinal() && getComState() == 0x00;
	}

	/**
	 * Check if the operation failed (COM_STATE = 0x01)
	 */
	public boolean isFailed() {
		return isFinal() && getComState() == 0x01;
	}

	/**
	 * Check if the module is busy (COM_STATE = 0x02)
	 */
	public boolean isBusy() {
		int comState = getComState();
		return comState == 0x02;
	}

	/**
	 * Check if there was a data error (COM_STATE = 0x04)
	 */
	public boolean isDataError() {
		return isFinal() && getComState() == 0x04;
	}

	/**
	 * Verify DATA1 field contains expected 0xAC value
	 */
	public boolean hasValidMarker() {
		byte[] d = getData();
		return d.length > 2 && (d[2] & 0xFF) == 0xAC;
	}

	/**
	 * Get a human-readable status description
	 */
	public String getStatusDescription() {
		if (!hasValidMarker()) {
			return "Invalid Response - Missing 0xAC marker";
		}

		if (isProgress()) {
			return "In Progress - Hopper " + getHopper() + " dumping coins...";
		}

		if (isFinal()) {
			String hopperInfo = "Hopper " + getHopper();
			int comState = getComState();

            return switch (comState) {
                case 0x00 -> "Success - " + hopperInfo + " dumped " + getDumpedQuantity() + " coins";
                case 0x01 -> "Failed - " + hopperInfo + " (Error Code: 0x" +
                        String.format("%02X", getErrorCode()) + ")";
                case 0x02 -> "Final Busy - " + hopperInfo + " (Error Code: 0x" +
                        String.format("%02X", getErrorCode()) + ")";
                case 0x04 -> "Data Error - " + hopperInfo + " (Error Code: 0x" +
                        String.format("%02X", getErrorCode()) + ")";
                default -> "Unknown State - " + hopperInfo + " (COM_STATE: 0x" +
                        String.format("%02X", comState) + ")";
            };
		}

		return "Invalid Response - Unknown format";
	}

	@Override
	public String toString() {
		return String.format("CoinDumpResponse{cmd=0x%02X, seq=%d, %s}",
				getCommand(), getSequenceNumber(), getStatusDescription());
	}
}
