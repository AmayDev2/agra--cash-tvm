package com.amay.tvm.coin.model;

public class CoinDumpResponse extends ModuleResponse {
	public CoinDumpResponse(byte command, byte sequenceNumber, byte[] data) {
		super(command, sequenceNumber, data);
	}

	public boolean isProgress() {
		byte[] d = getData();
		return d.length > 0 && (d[0] & 0xFF) == 0xAA; // placeholder progress marker
	}

	public int getHopper() {
		byte[] d = getData();
		return d.length > 1 ? (d[1] & 0xFF) : 0;
	}

	public int getDumpedQuantity() {
		byte[] d = getData();
		if (d.length > 3) {
			int hi = d[2] & 0xFF;
			int lo = d[3] & 0xFF;
			return (hi << 8) | lo;
		}
		return 0;
	}
}


