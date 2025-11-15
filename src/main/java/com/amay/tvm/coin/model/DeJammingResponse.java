package com.amay.tvm.coin.model;

import com.amay.tvm.coin.enums.CoinError;

public class DeJammingResponse extends ModuleResponse {
	public DeJammingResponse(byte command, byte sequenceNumber, byte[] data) {
		super(command, sequenceNumber, data);
	}

	public boolean isAck() {
		byte[] d = getData();
		return d.length > 0 && d[0] == 0x00; // assume 0x00 means OK
	}

	public CoinError getStatus(){
		return CoinError.fromCode(getData()[1]);
	}
}


