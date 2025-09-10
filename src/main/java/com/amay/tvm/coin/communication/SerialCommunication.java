package com.amay.tvm.coin.communication;


import com.amay.tvm.coin.constants.ProtocolConstants;
import com.amay.tvm.coin.util.HexUtil;
import com.fazecast.jSerialComm.SerialPort;

import java.util.Arrays;

public class SerialCommunication implements SerialCommunicationInterface {
	private SerialPort port;
	private int baudRate = 57600;
	private int dataBits = 8;
	private int stopBits = SerialPort.ONE_STOP_BIT;
	private int parity = SerialPort.NO_PARITY;

	private void applySystemOverrides() {
		try {
			String b = System.getProperty("serial.baud");
			if (b != null) baudRate = Integer.parseInt(b.trim());
			String db = System.getProperty("serial.databits");
			if (db != null) dataBits = Integer.parseInt(db.trim());
			String sb = System.getProperty("serial.stopbits");
			if (sb != null) {
				int sbi = Integer.parseInt(sb.trim());
				if (sbi == 1) stopBits = SerialPort.ONE_STOP_BIT;
				else if (sbi == 2) stopBits = SerialPort.TWO_STOP_BITS;
			}
			String p = System.getProperty("serial.parity");
			if (p != null) {
				String pv = p.trim().toUpperCase();
				if ("NONE".equals(pv) || "0".equals(pv)) parity = SerialPort.NO_PARITY;
				else if ("ODD".equals(pv) || "1".equals(pv)) parity = SerialPort.ODD_PARITY;
				else if ("EVEN".equals(pv) || "2".equals(pv)) parity = SerialPort.EVEN_PARITY;
			}
		} catch (Exception ignored) {}
	}

	@Override
	public void connect(String portName) throws CommunicationException {
		if (isConnected()) disconnect();
		applySystemOverrides();
		port = SerialPort.getCommPort(portName);
		port.setComPortParameters(baudRate, dataBits, stopBits, parity);
		port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 0);
		if (!port.openPort()) {
			throw new CommunicationException("Failed to open port: " + portName);
		}
		try { port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED); } catch (Throwable ignored) {}
		// Toggle DTR/RTS to reset some devices
		try { port.setDTR(); port.clearDTR(); port.setDTR(); } catch (Throwable ignored) {}
		try { port.setRTS(); port.clearRTS(); port.setRTS(); } catch (Throwable ignored) {}
		//System.out.println("PORT OPEN  : " + portName + " baud=" + baudRate + " dataBits=" + dataBits + " stopBits=" + stopBits + " parity=" + parity);
	}

	@Override
	public void disconnect() {
		if (port != null) {
			try { port.closePort(); } finally { port = null; }
		}
	}

	@Override
	public boolean isConnected() {
		return port != null && port.isOpen();
	}

	@Override
	public void write(byte[] data) throws CommunicationException {
		if (!isConnected()) throw new CommunicationException("Port not open");
		// No purge constants available in all jSerialComm versions; skip purge here
		int written = port.writeBytes(data, data.length);
		//System.out.println("PORT WRITE: " + written + " bytes");
		if (written != data.length) throw new CommunicationException("Short write: " + written + "/" + data.length);
	}

	@Override
	public byte[] read(int expectedMinBytes, int readTimeoutMs) throws CommunicationException {
		if (!isConnected()) throw new CommunicationException("Port not open");
		port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, readTimeoutMs, 0);
		byte[] buffer = new byte[Math.max(expectedMinBytes, 256)];
		int total = 0;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < readTimeoutMs) {
			int available = port.bytesAvailable();
			if (available > 0) {
				if (total + available > buffer.length) {
					byte[] nb = new byte[Math.max(buffer.length * 2, total + available)];
					System.arraycopy(buffer, 0, nb, 0, total);
					buffer = nb;
				}
				int read = port.readBytes(buffer, available, total);
				total += read;
				if (total >= expectedMinBytes) break;
			}
			try { Thread.sleep(10); } catch (InterruptedException ignored) {}
		}
		byte[] result = new byte[total];
		System.arraycopy(buffer, 0, result, 0, total);
		return result;
	}

	@Override
	public byte[] readUntilETX(int readTimeoutMs) throws CommunicationException {
		if (!isConnected()) throw new CommunicationException("Port not open");
		port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, readTimeoutMs, 0);
		byte[] buffer = new byte[512];
		int total = 0;
		int startIndex = -1;
		boolean escapeNext = false;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < readTimeoutMs) {
			int available = port.bytesAvailable();
			if (available > 0) {
				if (total + available > buffer.length) {
					byte[] nb = new byte[Math.max(buffer.length * 2, total + available)];
					System.arraycopy(buffer, 0, nb, 0, total);
					buffer = nb;
				}
				int read = port.readBytes(buffer, available, total);
				int prevTotal = total;
				total += read;
				byte[] chunk = Arrays.copyOfRange(buffer, prevTotal, total);
				//System.out.println("PORT READ  : " + read + " bytes -> " + HexUtil.toHex(chunk));
				for (int i = prevTotal; i < total; i++) {
					byte b = buffer[i];
					if (startIndex < 0) {
						if (b == ProtocolConstants.STX) {
							startIndex = i;
							escapeNext = false;
						}
					} else {
						if (escapeNext) {
							// This byte is escaped; treat as data regardless of value
							escapeNext = false;
							continue;
						}
						if (b == ProtocolConstants.DLE) {
							escapeNext = true;
							continue;
						}
						if (b == ProtocolConstants.ETX) {
							int endIndex = i;
							return Arrays.copyOfRange(buffer, startIndex, endIndex + 1);
						}
					}
				}
			}
			try { Thread.sleep(5); } catch (InterruptedException ignored) {}
		}
		byte[] partial = Arrays.copyOfRange(buffer, 0, total);
		//System.out.println("PORT TIMEOUT, partial RX: " + HexUtil.toHex(partial));
		throw new CommunicationException("Timeout waiting for ETX");
	}
}


