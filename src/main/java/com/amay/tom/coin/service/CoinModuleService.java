package com.amay.tom.coin.service;


import com.amay.tom.coin.commands.CommandBuilder;
import com.amay.tom.coin.communication.CommunicationException;
import com.amay.tom.coin.communication.SerialCommunication;
import com.amay.tom.coin.communication.SerialCommunicationInterface;
import com.amay.tom.coin.constants.ProtocolConstants;
import com.amay.tom.coin.enums.ModuleTestCode;
import com.amay.tom.coin.enums.Range;
import com.amay.tom.coin.model.CoinChangeResponse;
import com.amay.tom.coin.model.CoinDumpResponse;
import com.amay.tom.coin.model.ModuleResponse;
import com.amay.tom.coin.protocol.DataEscapeUtil;
import com.amay.tom.coin.protocol.ProtocolFrame;
import com.amay.tom.coin.protocol.ResponseParser;
import com.amay.tom.coin.protocol.SequenceNumberManager;
import com.amay.tom.coin.util.HexUtil;
import com.amay.tom.config.DataTransfer;
import com.amay.tom.config.LoggerTag;
import org.tinylog.Logger;

import java.util.Arrays;

public class CoinModuleService {
	private final SerialCommunicationInterface comm;
	private  DataTransfer listener;
	private final SequenceNumberManager sequenceNumberManager = new SequenceNumberManager();

	public CoinModuleService() {
		this(new SerialCommunication());
	}

	public CoinModuleService(SerialCommunicationInterface comm) {
		this.comm = comm;
	}

	public void connect(String port, String selectedBaud) { comm.connect(port,selectedBaud); }
	public void disconnect() { comm.disconnect(); }
	public boolean isConnected() { return comm.isConnected(); }

	private ModuleResponse sendAndReceive(ProtocolFrame frame, int timeoutMs) {
		byte[] raw = frame.toByteArray();
		boolean escapeEnabled=DataEscapeUtil.isEscapeEnabled(frame);
		byte[] escaped = escapeEnabled ? DataEscapeUtil.escapeFrame(raw) : raw;
		Logger.tag(LoggerTag.BUSS).info("TX (raw)     : " + HexUtil.toHex(raw));
		Logger.tag(LoggerTag.BUSS).info("TX (escaped) : " + HexUtil.toHex(escaped) + (escapeEnabled ? "" : " (disabled)"));
		write("TX : "+(escapeEnabled?HexUtil.toHex(escaped):HexUtil.toHex(raw)));
		comm.write(escaped);
		byte[] in;
		try {
			in = comm.readUntilETXIgnoreDirtyByte(timeoutMs);
		} catch (Exception ex) {
			Logger.tag(LoggerTag.BUSS).info("RX: <no frame> (" + ex.getMessage() + ")");
			throw ex;
		}
		Logger.tag(LoggerTag.BUSS).info("RX (escaped) : " + HexUtil.toHex(in));
		// Always unescape incoming (device may send DLE-prefixed controls even if we don't escape on TX)
		byte[] unescaped = DataEscapeUtil.unescapeFrame(in);
		Logger.tag(LoggerTag.BUSS).info("RX (raw)     : " + HexUtil.toHex(unescaped));
		ProtocolFrame parsed = ProtocolFrame.parse(unescaped);
		Logger.tag(LoggerTag.BUSS).info(
			"RX (parsed)  : CMD=" + HexUtil.toHex(parsed.getCommand()) +
			" SN=" + HexUtil.toHex(parsed.getSequenceNumber()) +
			" DATA=" + HexUtil.toHex(parsed.getData())
		);
		write("RX : "+ HexUtil.toHex(in));
		return ResponseParser.parseResponse(parsed);
	}

	private ModuleResponse sendAndReceiveIgnoreDirtyByte(ProtocolFrame frame, int timeoutMs) {
		byte[] raw = frame.toByteArray();
		boolean escapeEnabled=DataEscapeUtil.isEscapeEnabled(frame);
		byte[] escaped = escapeEnabled ? DataEscapeUtil.escapeFrame(raw) : raw;
		Logger.tag(LoggerTag.BUSS).info("TX (raw)     : " + HexUtil.toHex(raw));
		Logger.tag(LoggerTag.BUSS).info("TX (escaped) : " + HexUtil.toHex(escaped) + (escapeEnabled ? "" : " (disabled)"));
		comm.write(escaped);
		byte[] in;
		try {
			in = comm.readUntilETX(timeoutMs);
		} catch (Exception ex) {
			Logger.tag(LoggerTag.BUSS).info("RX: <no frame> (" + ex.getMessage() + ")");
			throw ex;
		}
		Logger.tag(LoggerTag.BUSS).info("RX (escaped) : " + HexUtil.toHex(in));
		// Always unescape incoming (device may send DLE-prefixed controls even if we don't escape on TX)
		byte[] unescaped = DataEscapeUtil.unescapeFrame(in);
		Logger.tag(LoggerTag.BUSS).info("RX (raw)     : " + HexUtil.toHex(unescaped));
		ProtocolFrame parsed = ProtocolFrame.parse(unescaped);
		Logger.tag(LoggerTag.BUSS).info(
				"RX (parsed)  : CMD=" + HexUtil.toHex(parsed.getCommand()) +
						" SN=" + HexUtil.toHex(parsed.getSequenceNumber()) +
						" DATA=" + HexUtil.toHex(parsed.getData())
		);
		return ResponseParser.parseResponse(parsed);
	}

	public ModuleResponse pollStatus() {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createPollingStatusCommand(seq);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	public ModuleResponse getVersion() throws Exception {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createGetVersionCommand(seq);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}
	public ModuleResponse getModuleReset() throws Exception {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createModuleResetCommand(seq);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	public ModuleResponse dispenseCoin(byte hopper, byte quantity) {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame start = CommandBuilder.createCoinDispenseCommand(hopper, quantity, seq);
		ModuleResponse resp;
		while (true) {
			Logger.tag(LoggerTag.APP).info("Sending Partial Command");
			resp = sendAndReceive(start, ProtocolConstants.LONG_OPERATION_TIMEOUT_MS);
			if (!(resp instanceof CoinChangeResponse)) break;
			CoinChangeResponse ccr = (CoinChangeResponse) resp;
			if (ccr.isProgress()) {
				try { Thread.sleep(ProtocolConstants.LONG_OPERATION_PROGRESS_MS); } catch (InterruptedException ignored) {}
				// continue waiting; device keeps sending progress frames for same SN
				continue;
			}
			if (ccr.isFinal()) {
				break;
			}
			// Unexpected; break to avoid infinite loop
			break;
		}

		// After final response, send End command (no response expected)
		byte endSeq = sequenceNumberManager.next();
		ProtocolFrame end = CommandBuilder.createCoinChangeEndCommand(hopper, endSeq);
		Logger.tag(LoggerTag.BUSS).info("TX (end)     : " + HexUtil.toHex(end.toByteArray()));
//		sendAndReceive(end, 0);
		try { comm.write(end.toByteArray()); } catch (Exception ignored) {}
		return resp;
	}

	public void end(byte data){
		byte endSeq = sequenceNumberManager.next();
		ProtocolFrame end = CommandBuilder.createCoinChangeEndCommand(data, endSeq);
		try { comm.write(end.toByteArray()); } catch (Exception ignored) {}
	}
	public ModuleResponse dumpHopper(byte hopper) {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame start = CommandBuilder.createCoinDumpStartCommand(hopper, seq);
		ModuleResponse resp;
		while (true) {
			resp = sendAndReceive(start, ProtocolConstants.LONG_OPERATION_TIMEOUT_MS);
			if (!(resp instanceof CoinDumpResponse)) break;
			CoinDumpResponse ccr = (CoinDumpResponse) resp;
			if (ccr.isProgress()) {
				try { Thread.sleep(ProtocolConstants.LONG_OPERATION_PROGRESS_MS); } catch (InterruptedException ignored) {}
				// continue waiting; device keeps sending progress frames for same SN
				continue;
			}
			if (ccr.isFinal()) {
				break;
			}
			// Unexpected; break to avoid infinite loop
			break;
		}
		byte endSeq = sequenceNumberManager.next();
		ProtocolFrame end = CommandBuilder.createCoinDumpEndCommand(endSeq);
		Logger.tag(LoggerTag.BUSS).info("TX (end)     : " + HexUtil.toHex(end.toByteArray()));
//		sendAndReceive(end, 0);
		try { comm.write(end.toByteArray()); } catch (Exception ignored) {}
		return resp;
	}

	public ModuleResponse turnOnBuzzer() { return controlBuzzer(true); }
	public ModuleResponse turnOffBuzzer() { return controlBuzzer(false); }

	public ModuleResponse turnOnTrayLight() { return controlTrayLight(true); }
	public ModuleResponse turnOffTrayLight() { return controlTrayLight(false); }



	public ModuleResponse controlDivertCommand(boolean on) throws Exception{
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.controlDivertCommand(on, seq);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	public byte[] sendCommand(byte[] bytes ) throws Exception{
		Logger.tag(LoggerTag.BUSS).info(
				"TX :"+ HexUtil.toHex(bytes)
		);
		write(("TX : "+ HexUtil.toHex(bytes)));
		comm.write(bytes);
		byte[] in;
		try {
			in = comm.readUntilETXIgnoreDirtyByte(ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);

		} catch (Exception ex) {
			Logger.tag(LoggerTag.BUSS).info("RX: <no frame> (" + ex.getMessage() + ")");
			throw ex;
		}
		Logger.tag(LoggerTag.BUSS).info(
				"RX :"+ HexUtil.toHex(in)
		);

		write(("RX : "+ HexUtil.toHex(in)));
		return in;
	}

    public byte[] sendCommandWithoutWait(byte[] bytes ) throws Exception{
        Logger.tag(LoggerTag.BUSS).info(
                "TX :"+ HexUtil.toHex(bytes)
        );
        write(("TX : "+ HexUtil.toHex(bytes)));
        comm.write(bytes);
        byte[] in;
        try {
            in = comm.readUntilETXIgnoreDirtyByte(ProtocolConstants.READ_TIMEOUT_100_MS);

        } catch (Exception ex) {
            Logger.tag(LoggerTag.BUSS).info("RX: <no frame> (" + ex.getMessage() + ")");
            throw ex;
        }
        Logger.tag(LoggerTag.BUSS).info(
                "RX :"+ HexUtil.toHex(in)
        );

        write(("RX : "+ HexUtil.toHex(in)));
        return in;
    }

	public ModuleResponse controlEscrowCommand(byte escrow, byte diverter) throws Exception{
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.controlEscrowCommand(escrow,diverter, seq);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}



	private ModuleResponse controlBuzzer(boolean on) {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createControlBuzzerCommand(on, seq);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	public ModuleResponse controlLight(boolean on) {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createControlLightCommand(on, seq);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}


	private ModuleResponse statusBuzzer() {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createStatusBuzzerCommand( seq);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	private ModuleResponse controlTrayLight(boolean on) {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createControlTrayLightCommand(on, seq);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	public ModuleResponse buzzerStatus() {

		return statusBuzzer();

	}

	public void end() {

	}

	public ModuleResponse testModule(ModuleTestCode moduleTestCode) throws CommunicationException {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createModuleTestCommand(seq, moduleTestCode.getCode());
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	public ModuleResponse getDeJamming(Range range) {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createDeJamming(seq, range.getCode());
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	public ModuleResponse acceptancePollStatus(byte status) throws Exception {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createCoinAcceptancePollingStatusCommand(seq,status);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	public ModuleResponse getCollectionBoxId() {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createGetCollectionBoxIdCommand(seq);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}
	public ModuleResponse setCollectionBoxId(byte id) {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createSetCollectionBoxIdCommand(seq,id);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	public ModuleResponse createTicketResultCommand(byte issue, byte feed) {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createTicketResultCommand(seq,issue,feed);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	public ModuleResponse createChangeTicketBoxCommand() {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createChangeTicketBoxCommand(seq);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	public ModuleResponse createFeedTicketCommand() {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createChangeTicketBoxCommand(seq);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	public ModuleResponse createMotorTestCommand(byte motorTest) {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createMotorTestCommand(seq,motorTest);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	public ModuleResponse controlAlarm(boolean on) {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createControlAlarmCommand(on, seq);
		return sendAndReceive(frame, ProtocolConstants.DEFAULT_READ_TIMEOUT_MS);
	}

	public void setBaudRate(String value) {
		comm.setBaudRate(value);
	}

	private void write(String data){
		if(null!=listener){
			listener.set(data);
		}
	}

	public void setListener(DataTransfer listener) {
		this.listener=listener;
	}
}


