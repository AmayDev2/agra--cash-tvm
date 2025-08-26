package com.amay.tvm.coin.model;

import java.nio.charset.StandardCharsets;

public class VersionResponse extends ModuleResponse {
	public VersionResponse(byte command, byte sequenceNumber, byte[] data) {
		super(command, sequenceNumber, data);
	}

	public String getVersionString() {
		return new String(getData(), StandardCharsets.US_ASCII).trim();
	}
}


