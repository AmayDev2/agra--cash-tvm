package com.amay.tvm.ups.communication;

import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.ups.command.UPSCommand;
import com.amay.tvm.ups.exception.UPSCommunicationException;
import com.amay.tvm.ups.model.UPSResponse;
import com.amay.tvm.ups.model.UPSStatus;
import com.fazecast.jSerialComm.SerialPort;
import org.tinylog.Logger;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * UPS Serial Communication Implementation
 * Based on AGRA METRO PROJECT Protocol Specification
 * Settings: 2400 bps, 8 data bits, No parity, 1 stop bit
 */
public class UPSSerialCommunication implements UPSCommunicationInterface {


    // Protocol settings
    private static final int BAUD_RATE = 2400;
    private static final int DATA_BITS = 8;
    private static final int STOP_BITS = SerialPort.ONE_STOP_BIT;
    private static final int PARITY = SerialPort.NO_PARITY;
    private static final int TIMEOUT_MS = 5000;
    private static final char START_BYTE = '(';
    private static final char STOP_BYTE = '\r';

    private SerialPort serialPort;
    private String lastPortNumber;
    private volatile boolean connected = false;
    private final Object lock = new Object();

    public UPSSerialCommunication(String comPort) throws UPSCommunicationException {
        connect(comPort);
    }

    @Override
    public boolean connect(String portNumber) throws UPSCommunicationException {
//        synchronized (lock) {
            try {
                if (connected && serialPort != null) {
                    Logger.tag(LoggerTag.APP).info("Already connected to " + serialPort.getSystemPortName());
                    return true;
                }

                serialPort = SerialPort.getCommPort(portNumber);
                serialPort.setBaudRate(BAUD_RATE);
                serialPort.setNumDataBits(DATA_BITS);
                serialPort.setNumStopBits(STOP_BITS);
                serialPort.setParity(PARITY);
                serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, TIMEOUT_MS, 0);

                // Set DTR high as per protocol requirement
                serialPort.setDTR();
//                serialPort.closePort();
                Logger.tag(LoggerTag.APP).info("Serial Port : "+serialPort.isOpen());

                if (serialPort.openPort()) {
                    lastPortNumber = portNumber;
                    connected = true;
                    Logger.tag(LoggerTag.APP).info("Successfully connected to UPS on " + portNumber);

                    // Verify connection with status inquiry
                    try {
                        TimeUnit.MILLISECONDS.sleep(500); // Allow port to stabilize
                        getUPSResponseObject();
                        return true;
                    } catch (Exception e) {
                        Logger.tag(LoggerTag.APP).warn("Connection established but UPS not responding: " + e.getMessage());
                        disconnect();
                        throw new UPSCommunicationException("UPS not responding on " + portNumber, e);
                    }
                } else {
                    throw new UPSCommunicationException("Failed to open serial port: " + portNumber);
                }
            } catch (Exception e) {
                connected = false;
                throw new UPSCommunicationException("Connection failed: " + e.getMessage(), e);
            }
//        }
    }

    @Override
    public boolean isConnected() {
        boolean isCon= connected && serialPort != null && serialPort.isOpen();
        if(!isCon){
            try {
                reconnect();
            } catch (UPSCommunicationException e) {
                e.getMessage();
            }
        }
        return isCon;
    }

    @Override
    public boolean reconnect() throws UPSCommunicationException {
//        synchronized (lock) {
            if (lastPortNumber == null) {
                throw new UPSCommunicationException("No previous connection to reconnect");
            }

            Logger.tag(LoggerTag.APP).info("Attempting to reconnect to " + lastPortNumber);
            disconnect();

            try {
                TimeUnit.MILLISECONDS.sleep(1000); // Wait before reconnect
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return connect(lastPortNumber);
//        }
    }

    @Override
    public void disconnect() {
//        synchronized (lock) {
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.closePort();
                Logger.tag(LoggerTag.APP).info("Disconnected from UPS on " + serialPort.getSystemPortName());
            }
            connected = false;
            serialPort = null;
//        }
    }

    @Override
    public double getIPVoltage() throws UPSCommunicationException {
        UPSResponse response = getUPSResponseObject();
        return response.getInputVoltage();
    }

    @Override
    public UPSResponse getUPSResponseObject() throws UPSCommunicationException {
        String response = fireCommand(UPSCommand.STATUS_INQUIRY);
        return parseUPSResponse(response);
    }

    @Override
    public String fireCommand(UPSCommand command) throws UPSCommunicationException {
        return fireCommand(command.getCommand());
    }

    @Override
    public String fireCommand(String commandString) throws UPSCommunicationException {
        synchronized (lock) {
            if (!isConnected()) {
                throw new UPSCommunicationException("UPS not connected");
            }

            try {
                // Send command
                byte[] commandBytes = (commandString + "\r").getBytes(StandardCharsets.US_ASCII);
                int bytesWritten = serialPort.writeBytes(commandBytes, commandBytes.length);

                if (bytesWritten != commandBytes.length) {
                    throw new UPSCommunicationException("Failed to write complete command");
                }

                Logger.tag(LoggerTag.APP).info("Sent command: " + commandString);

                // Read response
                String response = readResponse();

                // Check for echoed command (invalid command indicator)
                if (response.trim().equals(commandString)) {
                    throw new UPSCommunicationException("Invalid command echoed back: " + commandString);
                }

                return response;

            } catch (Exception e) {
                Logger.tag(LoggerTag.APP).error( "Command execution failed", e);
                throw new UPSCommunicationException("Failed to execute command: " + commandString, e);
            }
        }
    }

    /**
     * Read response from UPS until stop byte or timeout
     */
    private String readResponse() throws UPSCommunicationException {
        StringBuilder response = new StringBuilder();
        byte[] buffer = new byte[1];
        long startTime = System.currentTimeMillis();
        boolean startByteReceived = false;

        while (System.currentTimeMillis() - startTime < TIMEOUT_MS) {
            int bytesRead = serialPort.readBytes(buffer, 1);

            if (bytesRead > 0) {
                char ch = (char) buffer[0];

                if (ch == START_BYTE) {
                    startByteReceived = true;
                    response.append(ch);
                } else if (ch == STOP_BYTE) {
                    if (startByteReceived) {
                        Logger.tag(LoggerTag.APP).info("Received response: " + response.toString());
                        return response.toString();
                    }
                } else if (startByteReceived) {
                    response.append(ch);
                }
            }
        }

        throw new UPSCommunicationException("Response timeout - no complete response received");
    }

    /**
     * Parse UPS status response
     * Format: (MMM.M NNN.N PPP.P QQQ RR.R S.SS TT.T b7b6b5b4b3b2b1b0
     */
    private UPSResponse parseUPSResponse(String rawResponse) throws UPSCommunicationException {
        try {
            UPSResponse response = new UPSResponse();
            response.setRawResponse(rawResponse);

            // Remove start byte
            String data = rawResponse.substring(1).trim();

            // Split by spaces
            String[] fields = data.split("\\s+");

            if (fields.length < 8) {
                throw new UPSCommunicationException("Invalid response format - insufficient fields");
            }

            // Parse numeric fields (handle '@' for unavailable data)
            response.setInputVoltage(parseDoubleField(fields[0]));
            response.setInputFaultVoltage(parseDoubleField(fields[1]));
            response.setOutputVoltage(parseDoubleField(fields[2]));
            response.setOutputCurrent(parseIntField(fields[3]));
            response.setInputFrequency(parseDoubleField(fields[4]));
            response.setBatteryVoltage(parseDoubleField(fields[5]));
            response.setTemperature(parseDoubleField(fields[6]));

            // Parse status byte
            String statusByte = fields[7];
            response.setStatus(new UPSStatus(statusByte));

            return response;

        } catch (Exception e) {
            throw new UPSCommunicationException("Failed to parse UPS response: " + rawResponse, e);
        }
    }

    private double parseDoubleField(String field) {
        if (field.contains("@")) {
            return 0.0; // Unavailable data
        }
        return Double.parseDouble(field);
    }

    private int parseIntField(String field) {
        if (field.contains("@")) {
            return 0; // Unavailable data
        }
        return Integer.parseInt(field);
    }

    /**
     * Get list of available serial ports
     */
    public static String[] getAvailablePorts() {
        SerialPort[] ports = SerialPort.getCommPorts();
        String[] portNames = new String[ports.length];
        for (int i = 0; i < ports.length; i++) {
            portNames[i] = ports[i].getSystemPortName();
        }
        return portNames;
    }
}
