package com.amay.tom.coin.commands;


import com.amay.tom.coin.constants.ProtocolConstants;
import com.amay.tom.coin.protocol.ProtocolFrame;

public final class CommandBuilder {
	private CommandBuilder() {}

	public static ProtocolFrame createPollingStatusCommand(byte sequence) {
		return new ProtocolFrame(ProtocolConstants.CMD_POLLING_STATUS, sequence, new byte[0]);
	}

	public static ProtocolFrame createGetVersionCommand(byte sequence) {
		return new ProtocolFrame(ProtocolConstants.CMD_GET_VERSION, sequence, new byte[0]);
	}

	public static ProtocolFrame createModuleResetCommand(byte sequence) {
		return new ProtocolFrame(ProtocolConstants.CMD_MODULE_RESET, sequence, new byte[0]);
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
		byte[] data = new byte[] { 0x00, (byte)(on ? 0x01 : 0x02) ,0x00,0x00,0x00 };
		return new ProtocolFrame(ProtocolConstants.CMD_CONTROL, sequence, data);
	}

	public static ProtocolFrame controlDivertCommand(boolean on, byte sequence) {
		byte[] data = new byte[] {  (byte)(on ? 0x01 : 0x02) ,0x00 ,0x00,0x00,0x00,0x00};
		return new ProtocolFrame(ProtocolConstants.CMD_CONTROL, sequence, data);
	}

	public static ProtocolFrame controlEscrowCommand(boolean on, byte sequence) {
		byte[] data = new byte[] {  0x00 ,0x00,0x00,0x00,(byte)(on ? 0x01 : 0x02),0x00};
		return new ProtocolFrame(ProtocolConstants.CMD_CONTROL, sequence, data);
	}

	public static ProtocolFrame createStatusBuzzerCommand( byte sequence) {
		byte[] data = new byte[] {  0x00, 0x00, 0x00 ,0x00,0x00,0x00 };
		return new ProtocolFrame(ProtocolConstants.CMD_CONTROL, sequence, data);
	}

	public static ProtocolFrame createControlTrayLightCommand(boolean on, byte sequence) {
		byte[] data = new byte[] {  0x00, (byte)(on ? 0x01 : 0x02), 0x00 ,0x00, 0x00 };
		return new ProtocolFrame(ProtocolConstants.CMD_CONTROL, sequence, data);
	}

	public static ProtocolFrame createCoinChangeEndCommand(byte hopper, byte sequence) {
		// Spec 4.3.4: CMD=0x01, Data0=hopper, LEN=0x03
		byte[] data = new byte[] { hopper };
		return new ProtocolFrame(ProtocolConstants.CMD_COIN_CHANGE_END, sequence, data);
	}

	public static ProtocolFrame createModuleTestCommand(byte sequence,byte testCode) {
		// Test Code - 0x11 (De-Jamming [3 times]), 0x12 (Coin Shutter [1 time]), 0x13 (Diverter [1 time])
		byte[] data = new byte[] { testCode, 0 };
		return new ProtocolFrame(ProtocolConstants.CMD_MODULE_TEST, sequence, data);
	}

	public static ProtocolFrame createDeJamming(byte sequence,byte range) {
		// Range: 1-3
		byte[] data = new byte[] { range };
		return new ProtocolFrame(ProtocolConstants.CMD_DE_JAMMING, sequence, data);
	}

	public static ProtocolFrame createCoinAcceptancePollingStatusCommand(byte sequence, byte status) {
		return new ProtocolFrame(ProtocolConstants.CMD_COIN_ACCEPTANCE_POLLING, sequence, new byte[]{status});
	}

	public static ProtocolFrame createGetCollectionBoxIdCommand(byte seq) {
		byte[] data = new byte[] { ProtocolConstants.COLLECTION_BOX };
		return new ProtocolFrame(ProtocolConstants.CMD_GET_COLLECTION_BOX_ID,seq,data);
	}

	public static ProtocolFrame createSetCollectionBoxIdCommand(byte seq,byte noOfCollectionBox) {
		byte[] data = new byte[] { noOfCollectionBox,0,0,0,0 };
		return new ProtocolFrame(ProtocolConstants.CMD_SET_COLLECTION_BOX_ID,seq,data);
	}

	public static ProtocolFrame createTicketResultCommand(byte seq,byte issue, byte feed) {
		byte[] data = new byte[] { feed,issue };
		return new ProtocolFrame(ProtocolConstants.TICKET_RESULT,seq,data);
	}

	public static ProtocolFrame createChangeTicketBoxCommand(byte seq) {
		byte[] data = new byte[] { 0 };
		return new ProtocolFrame(ProtocolConstants.CHANGE_TICKET_BOX,seq,data);
	}

	public static ProtocolFrame createFeedTicketCommand(byte seq) {
		byte[] data = new byte[] { (byte) 0x01 };
		return new ProtocolFrame(ProtocolConstants.FEED_TICKET,seq,data);
	}

	public static ProtocolFrame createMotorTestCommand(byte seq,byte motorTest) {
		byte[] data = new byte[] { motorTest };
		return new ProtocolFrame(ProtocolConstants.MOTOR_TEST,seq,data);
	}

	public static ProtocolFrame createControlAlarmCommand(boolean on, byte seq) {
		byte[] data = new byte[] { 0x00,0x00,0x00, (byte)(on ? 0x01 : 0x02) ,0x00,0x00 };
		return new ProtocolFrame(ProtocolConstants.CMD_CONTROL, seq, data);
	}

	public static ProtocolFrame createControlLightCommand(boolean on, byte sequence) {
		byte[] data = new byte[] { 0x00,0x00, (byte)(on ? 0x01 : 0x02) ,0x00,0x00,0x00 };
		return new ProtocolFrame(ProtocolConstants.CMD_CONTROL, sequence, data);
	}
//	public static final byte TICKET_RESULT = 0x21;
//	public static final byte CHANGE_TICKET_BOX = 0x22; //DATA BYTE 1
//	public static final byte FEED_TICKET = 0x20; //DATA BYTE 1
//	public static final byte MOTOR_TEST = 0x32; //DATA BYTE 1
}


