package com.amay.tvm.coin.commands;


import com.amay.tvm.coin.constants.ProtocolConstants;
import com.amay.tvm.coin.protocol.ProtocolFrame;

public final class CommandBuilder {
	private CommandBuilder() {}

	public static ProtocolFrame createPollingStatusCommand(byte sequence) {
		return new ProtocolFrame(ProtocolConstants.CMD_POLLING_STATUS, sequence, new byte[0]);
	}

	public static ProtocolFrame createGetVersionCommand(byte sequence) {
		return new ProtocolFrame(ProtocolConstants.CMD_GET_VERSION, sequence, new byte[0]);
	}

	public static ProtocolFrame createCoinChangeStartCommand(byte hopper, byte mode, byte quantity, byte sequence) {
		byte[] data = new byte[] { hopper, mode, quantity };
		return new ProtocolFrame(ProtocolConstants.CMD_COIN_CHANGE, sequence, data);
	}

	public static ProtocolFrame createCoinDispenseCommand(byte hopper, byte quantity, byte sequence) {
		// Per device spec: hopper, MODE_COIN_DISPENSE (0xAB), quantity
		byte[] data = new byte[] { hopper, ProtocolConstants.MODE_COIN_DISPENSE, quantity };
		return new ProtocolFrame(ProtocolConstants.CMD_COIN_CHANGE, sequence, data);
	}

	public static ProtocolFrame createCoinChangeEndCommand(byte sequence) {
		byte[] data = new byte[] { 0x00 };
		return new ProtocolFrame(ProtocolConstants.CMD_COIN_CHANGE, sequence, data);
	}

	public static ProtocolFrame createCoinDumpStartCommand(byte hopper, byte sequence) {
		byte[] data = new byte[] { hopper, ProtocolConstants.MODE_COIN_DUMP };
		return new ProtocolFrame(ProtocolConstants.CMD_COIN_DUMP, sequence, data);
	}

	public static ProtocolFrame createCoinDumpEndCommand(byte sequence) {
		byte[] data = new byte[] { 0x00 };
		return new ProtocolFrame(ProtocolConstants.CMD_COIN_DUMP, sequence, data);
	}

	public static ProtocolFrame createControlBuzzerCommand(boolean on, byte sequence) {
		byte[] data = new byte[] { 0x01, (byte)(on ? 0x01 : 0x00) };
		return new ProtocolFrame(ProtocolConstants.CMD_CONTROL, sequence, data);
	}

	public static ProtocolFrame createControlTrayLightCommand(boolean on, byte sequence) {
		byte[] data = new byte[] { 0x02, (byte)(on ? 0x01 : 0x00) };
		return new ProtocolFrame(ProtocolConstants.CMD_CONTROL, sequence, data);
	}

	public static ProtocolFrame createCoinChangeEndCommand(byte hopper, byte sequence) {
		// Spec 4.3.4: CMD=0x01, Data0=hopper, LEN=0x03
		byte[] data = new byte[] { hopper };
		return new ProtocolFrame(ProtocolConstants.CMD_COIN_CHANGE_END, sequence, data);
	}
}


