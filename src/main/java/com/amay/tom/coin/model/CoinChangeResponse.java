package com.amay.tom.coin.model;

public class CoinChangeResponse extends ModuleResponse {
	public CoinChangeResponse(byte command, byte sequenceNumber, byte[] data) {
		super(command, sequenceNumber, data);
	}

	public boolean isProgress() {
		byte[] d = getData();
		// Spec 4.3.2: LEN=0x05, DATA layout in response: [COM_STATE, HOPPER, 0xAB]
		// In-progress has COM_STATE=0x02 (busy) and Data1=0xAB
		return d.length >= 3 && (d[0] & 0xFF) == 0x02 && (d[2] & 0xFF) == 0xAB;
	}

	public boolean isFinal() {
		byte[] d = getData();
		// Spec 4.3.3: LEN=0x06, DATA: [COM_STATE, HOPPER, DISPENSED_QTY, ERROR_CODE]
		return d.length >= 4 && ((d[0] & 0xFF) == 0x00 || (d[0] & 0xFF) == 0x01 || (d[0] & 0xFF) == 0x04);
	}

	public int getDispensedQuantity() {
		byte[] d = getData();
		// For final response, quantity is at index 2
		return d.length > 2 ? (d[2] & 0xFF) : 0;
	}

	public int getComState() {
		byte[] d = getData();
		return d.length > 0 ? (d[0] & 0xFF) : -1;
	}

	public int getHopper() {
		byte[] d = getData();
		return d.length > 1 ? (d[1] & 0xFF) : -1;
	}

	public int getErrorCode() {
		byte[] d = getData();
		return d.length > 3 ? (d[3] & 0xFF) : 0;
	}
}


