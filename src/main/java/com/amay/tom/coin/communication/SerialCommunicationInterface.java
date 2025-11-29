package com.amay.tom.coin.communication;

public interface SerialCommunicationInterface {
	void connect(String portName, String selectedBaud) throws CommunicationException;
	void disconnect();
	boolean isConnected();
	void write(byte[] data) throws CommunicationException;
	byte[] read(int expectedMinBytes, int readTimeoutMs) throws CommunicationException;
	byte[] readUntilETX(int readTimeoutMs) throws CommunicationException;

	byte[] readUntilETXIgnoreDirtyByte(int readTimeoutMs) throws CommunicationException;

	void setBaudRate(String value);
}


