package com.amay.tom.coin.model;

public class PollingStatusResponse extends ModuleResponse {
	public PollingStatusResponse(byte command, byte sequenceNumber, byte[] data) {
		super(command, sequenceNumber, data);
	}

	// --- Protocol core helpers ---
	public int getComState() {
		byte[] d = getData();
		return d.length > 0 ? (d[0] & 0xFF) : 0;
	}
	public boolean isSuccessful() { return getComState() == 0x00; }
	public boolean isFailed()     { return getComState() == 0x01; }
	public boolean isBusy()       { return getComState() == 0x02; }
	public boolean isDataError()  { return getComState() == 0x04; }

	// --- HOPPER STATUS ---
	// Data0 = getData()[1], Data1 = getData()[2], Data2 = getData()[3]
	// Bit0 pos: in position = 0, NOT in pos = 1
	// Bit1 empty: OK = 0, EMPTY/BLOCK = 1
	public boolean isHopper1InPosition() {
		return checkDataBit(1, 0) == false;
	}
	public boolean isHopper1AlmostEmpty() {
		return checkDataBit(1, 1);
	}
	public boolean isHopper2InPosition() {
		return checkDataBit(2, 0) == false;
	}
	public boolean isHopper2AlmostEmpty() {
		return checkDataBit(2, 1);
	}
	public boolean isHopper3InPosition() {
		return checkDataBit(3, 0) == false;
	}
	public boolean isHopper3AlmostEmpty() {
		return checkDataBit(3, 1);
	}

	// --- DOOR & MODULE STATUS (Data6 = getData()[7]) ---
	public boolean isMaintenanceDoorOpen() {
		return checkDataBit(7, 7);
	}
	public boolean isCoinRefillDoorOpen() {
		return checkDataBit(7, 6);
	}
	public boolean isHopperMaintenanceDoorOpen() {
		return checkDataBit(7, 5);
	}
	public boolean isBanknoteModuleInPosition() {
		return checkDataBit(7, 4) == false; // 0 = in position, 1 = NOT in position
	}

	private boolean checkDataBit(int dataIndex, int bitIndex) {
		byte[] d = getData();
		if (d.length <= dataIndex) return false;
		return ((d[dataIndex] >> bitIndex) & 1) == 1;
	}

	// Access to raw status data (excluding COM_STATE!) for debugging
	public int getDataByte(int dataIndex) {
		byte[] d = getData();
		return d.length > (dataIndex) ? (d[dataIndex] & 0xFF) : 0;
	}

	// Human-friendly status summaries
	public String getStatusDescription() {
		if (!isSuccessful()) {
			int comState = getComState();
			switch (comState) {
				case 0x01: return "Command Failed";
				case 0x02: return "Module Busy";
				case 0x04: return "Data Error";
				default:   return "Unknown Error (COM_STATE: 0x" + Integer.toHexString(comState) + ")";
			}
		}

		StringBuilder status = new StringBuilder();
		if (!isHopper1InPosition()) status.append("Hopper1 out of position; ");
		if (!isHopper2InPosition()) status.append("Hopper2 out of position; ");
		if (!isHopper3InPosition()) status.append("Hopper3 out of position; ");

		if (isHopper1AlmostEmpty()) status.append("Hopper1 almost empty; ");
		if (isHopper2AlmostEmpty()) status.append("Hopper2 almost empty; ");
		if (isHopper3AlmostEmpty()) status.append("Hopper3 almost empty; ");

		if (isMaintenanceDoorOpen()) status.append("Maintenance door open; ");
		if (isCoinRefillDoorOpen()) status.append("Coin refill door open; ");
		if (isHopperMaintenanceDoorOpen()) status.append("Hopper maintenance door open; ");

		if (!isBanknoteModuleInPosition()) status.append("Banknote module not in position; ");

		String res = status.toString().trim();
		if (res.endsWith(";")) res = res.substring(0, res.length() - 1);
		return res.isEmpty() ? "All systems operational" : res;
	}

	public String getHopperStatus() {
		return String.format("Hoppers: 1[%s,%s] 2[%s,%s] 3[%s,%s]",
				isHopper1InPosition() ? "POS" : "OUT",
				isHopper1AlmostEmpty() ? "EMPTY" : "OK",
				isHopper2InPosition() ? "POS" : "OUT",
				isHopper2AlmostEmpty() ? "EMPTY" : "OK",
				isHopper3InPosition() ? "POS" : "OUT",
				isHopper3AlmostEmpty() ? "EMPTY" : "OK"
		);
	}

	public String getDoorStatus() {
		return String.format("Doors: Maint[%s] Refill[%s] HopperMaint[%s] | BanknoteMod[%s]",
				isMaintenanceDoorOpen() ? "OPEN" : "CLOSED",
				isCoinRefillDoorOpen() ? "OPEN" : "CLOSED",
				isHopperMaintenanceDoorOpen() ? "OPEN" : "CLOSED",
				isBanknoteModuleInPosition() ? "IN" : "OUT"
		);
	}

	@Override
	public String toString() {
		if (!isSuccessful()) {
			return "PollingStatusResponse{comState=0x" +
					Integer.toHexString(getComState()) + ", status=" + getStatusDescription() + "}";
		}
		return "PollingStatusResponse{" + getHopperStatus() + " | " + getDoorStatus() + "}";
	}
}
