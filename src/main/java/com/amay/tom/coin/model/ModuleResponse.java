package com.amay.tom.coin.model;

import lombok.ToString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@ToString
public class ModuleResponse {
	private final byte command;
	private final byte sequenceNumber;
	private final byte[] data;

	public ModuleResponse(byte command, byte sequenceNumber, byte[] data) {
		this.command = command;
		this.sequenceNumber = sequenceNumber;
		this.data = data == null ? new byte[0] : Arrays.copyOf(data, data.length);
	}

	public byte getCommand() { return command; }
	public byte getSequenceNumber() { return sequenceNumber; }
	public byte[] getData() { return Arrays.copyOf(data, data.length); }
}


