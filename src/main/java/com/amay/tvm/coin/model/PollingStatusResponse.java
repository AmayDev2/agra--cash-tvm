package com.amay.tvm.coin.model;

public class PollingStatusResponse extends ModuleResponse {
	public PollingStatusResponse(byte command, byte sequenceNumber, byte[] data) {
		super(command, sequenceNumber, data);
	}

	// Placeholder decoding helpers; actual bit mapping depends on protocol spec
	public boolean isHopper1InPosition() { return checkBit(0, 0); }
	public boolean isHopper2InPosition() { return checkBit(0, 1); }
	public boolean isHopper3InPosition() { return checkBit(0, 2); }
	public boolean isHopper1AlmostEmpty() { return checkBit(1, 0); }
	public boolean isHopper2AlmostEmpty() { return checkBit(1, 1); }
	public boolean isHopper3AlmostEmpty() { return checkBit(1, 2); }
	public boolean isMaintenanceDoorOpen() { return checkBit(2, 0); }
	public boolean isCoinRefillDoorOpen() { return checkBit(2, 1); }
	public boolean isHopperMaintenanceDoorOpen() { return checkBit(2, 2); }
	public boolean isBanknoteModuleInPosition() { return checkBit(3, 0); }

	private boolean checkBit(int byteIndex, int bitIndex) {
		byte[] d = getData();
		if (d.length <= byteIndex) return false;
		int v = d[byteIndex] & 0xFF;
		return ((v >> bitIndex) & 1) == 1;
	}
}


