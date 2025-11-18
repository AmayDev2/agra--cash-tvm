package com.amay.tom.coin.communication;

public interface SerialCommunicationInterface {
	void connect(String portName) throws CommunicationException;
	void disconnect();
	boolean isConnected();
	void write(byte[] data) throws CommunicationException;
	byte[] read(int expectedMinBytes, int readTimeoutMs) throws CommunicationException;
	byte[] readUntilETX(int readTimeoutMs) throws CommunicationException;
}


