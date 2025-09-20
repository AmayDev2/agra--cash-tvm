package com.amay.tvm.coin.service;


import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.coin.commands.CommandBuilder;
import com.amay.tvm.coin.communication.SerialCommunication;
import com.amay.tvm.coin.communication.SerialCommunicationInterface;
import com.amay.tvm.coin.constants.ProtocolConstants;
import com.amay.tvm.coin.model.CoinChangeResponse;
import com.amay.tvm.coin.model.CoinDumpResponse;
import com.amay.tvm.coin.model.ModuleResponse;
import com.amay.tvm.coin.protocol.DataEscapeUtil;
import com.amay.tvm.coin.protocol.ProtocolFrame;
import com.amay.tvm.coin.protocol.ResponseParser;
import com.amay.tvm.coin.protocol.SequenceNumberManager;
import com.amay.tvm.coin.util.HexUtil;
import org.tinylog.Logger;

public class CoinModuleService {
	private final SerialCommunicationInterface comm;
	private final SequenceNumberManager sequenceNumberManager = new SequenceNumberManager();

	public CoinModuleService() {
		this(new SerialCommunication());
	}

	public CoinModuleService(SerialCommunicationInterface comm) {
		this.comm = comm;
	}

	public void connect(String port) { comm.connect(port); }
	public void disconnect() { comm.disconnect(); }
	public boolean isConnected() { return comm.isConnected(); }

	private ModuleResponse sendAndReceive(ProtocolFrame frame, int timeoutMs) {
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

	public ModuleResponse getVersion() {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createGetVersionCommand(seq);
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
		sendAndReceive(end, 0);
		try { comm.write(end.toByteArray()); } catch (Exception ignored) {}
		return resp;
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
		sendAndReceive(end, 0);
		try { comm.write(end.toByteArray()); } catch (Exception ignored) {}
		return resp;
	}

	public ModuleResponse turnOnBuzzer() { return controlBuzzer(true); }
	public ModuleResponse turnOffBuzzer() { return controlBuzzer(false); }

	public ModuleResponse turnOnTrayLight() { return controlTrayLight(true); }
	public ModuleResponse turnOffTrayLight() { return controlTrayLight(false); }

	private ModuleResponse controlBuzzer(boolean on) {
		byte seq = sequenceNumberManager.next();
		ProtocolFrame frame = CommandBuilder.createControlBuzzerCommand(on, seq);
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
}


