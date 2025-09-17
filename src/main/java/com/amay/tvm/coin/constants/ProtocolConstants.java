package com.amay.tvm.coin.constants;

public final class ProtocolConstants {
	private ProtocolConstants() {}

	// Control characters
	public static final byte STX = 0x02;
	public static final byte ETX = 0x03;
	public static final byte DLE = 0x10;

	// Command codes
	public static final byte CMD_COIN_CHANGE = 0x00; // Start + responses
	public static final byte CMD_COIN_CHANGE_END = 0x01; // End command (no response)
	public static final byte CMD_COIN_DUMP = 0x02;
	public static final byte CMD_CONTROL = 0x04;
	public static final byte CMD_POLLING_STATUS = 0x31;
	public static final byte CMD_GET_VERSION = 0x34;

	// Device-specific mode/marker values
	public static final byte MODE_COIN_DISPENSE = (byte)0xAB;
	public static final byte MODE_COIN_DUMP = (byte)0xAC;

	// Response markers or flags
	public static final byte RESPONSE_PROGRESS = (byte)0xAA; // placeholder per spec: progress notification

	// Timeouts (milliseconds)
	public static final int DEFAULT_READ_TIMEOUT_MS = 5000;
	public static final int LONG_OPERATION_PROGRESS_MS = 350;
	public static final int LONG_OPERATION_TIMEOUT_MS = 60_000;

	// Coin change COM State values
	public static final byte COM_STATE_SUCCESS = 0x00;
	public static final byte COM_STATE_FAILED = 0x01;
	public static final byte COM_STATE_BUSY = 0x02;
	public static final byte COM_STATE_DATA_ERROR = 0x04;
}


