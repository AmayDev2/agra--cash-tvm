package com.amay.tom.coin.model;

public class ControlResponse extends ModuleResponse {
	public ControlResponse(byte command, byte sequenceNumber, byte[] data) {
		super(command, sequenceNumber, data);
	}

	public boolean isAck() {
		byte[] d = getData();
		return d.length > 0 && d[0] == 0x00; // assume 0x00 means OK
	}
}


