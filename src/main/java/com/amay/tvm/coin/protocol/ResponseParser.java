package com.amay.tvm.coin.protocol;


import com.amay.tvm.coin.constants.ProtocolConstants;
import com.amay.tvm.coin.model.*;

public final class ResponseParser {
	private ResponseParser() {}

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
            default -> new ModuleResponse(cmd, sn, d);
        };
	}
}


