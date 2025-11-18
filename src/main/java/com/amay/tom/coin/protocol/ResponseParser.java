package com.amay.tom.coin.protocol;


import com.amay.tom.coin.constants.ProtocolConstants;
import com.amay.tom.coin.model.*;

public final class ResponseParser {
	private ResponseParser() {}

//    public static final byte CMD_MODULE_TEST = 0x32;
//    public static final byte CMD_DE_JAMMING = 0x09;
//    public static final byte CMD_COIN_ACCEPTANCE_POLLING = 0x05;
//    public static final byte CMD_GET_COLLECTION_BOX_ID = 0x08;
//    public static final byte CMD_SET_COLLECTION_BOX_ID = 0x07;
	public static ModuleResponse parseResponse(ProtocolFrame frame) {
		byte cmd = frame.getCommand();
		byte sn = frame.getSequenceNumber();
		byte[] d = frame.getData();
        return switch (cmd) {
            case ProtocolConstants.CMD_POLLING_STATUS -> new PollingStatusResponse(cmd, sn, d);
            case ProtocolConstants.CMD_GET_VERSION -> new VersionResponse(cmd, sn, d);
            case ProtocolConstants.CMD_COIN_CHANGE -> new CoinChangeResponse(cmd, sn, d);
            case ProtocolConstants.CMD_COIN_DUMP -> new CoinDumpResponse(cmd, sn, d);
            case ProtocolConstants.CMD_CONTROL -> new ControlResponse(cmd, sn, d);
            case ProtocolConstants.CMD_MODULE_TEST -> new ModuleTest(cmd, sn, d);
            case ProtocolConstants.CMD_DE_JAMMING -> new DeJammingResponse(cmd, sn, d);
            case ProtocolConstants.CMD_COIN_ACCEPTANCE_POLLING -> new AcceptancePollingStatusResponse(cmd, sn, d);
            case ProtocolConstants.CMD_GET_COLLECTION_BOX_ID -> new CollectionBoxIdSetResponse(cmd, sn, d);
            case ProtocolConstants.CMD_SET_COLLECTION_BOX_ID -> new CollectionBoxIdResponse(cmd, sn, d);
            default -> new ModuleResponse(cmd, sn, d);
        };
	}
}


