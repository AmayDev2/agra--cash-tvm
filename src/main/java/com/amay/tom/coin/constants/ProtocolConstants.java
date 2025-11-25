package com.amay.tom.coin.constants;
//
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
	public static final byte CMD_MODULE_RESET = 0x33;
	//******************
	public static final byte CMD_MODULE_TEST = 0x32;
	public static final byte CMD_DE_JAMMING = 0x09;
	public static final byte CMD_COIN_ACCEPTANCE_POLLING = 0x05;
	public static final byte CMD_GET_COLLECTION_BOX_ID = 0x08;
	public static final byte CMD_SET_COLLECTION_BOX_ID = 0x07;
	//***********************
	public static final byte TICKET_RESULT = 0x21;
	public static final byte CHANGE_TICKET_BOX = 0x22; //DATA BYTE 1
	public static final byte FEED_TICKET = 0x20; //DATA BYTE 1
	public static final byte MOTOR_TEST = 0x32; //DATA BYTE 1




	// Device-specific mode/marker values
	public static final byte MODE_COIN_DISPENSE = (byte)0xAB;
	public static final byte MODE_COIN_DUMP = (byte)0xAC;
	public static final byte COLLECTION_BOX = (byte)0x0A;

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


