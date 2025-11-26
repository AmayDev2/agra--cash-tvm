package com.amay.tvm.overheadDisplay;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.OutputStream;

public class OverheadDisplaySerial {
    private final SerialPort port;
    private OutputStream out;

    public OverheadDisplaySerial(String portName) throws IOException {
        port = SerialPort.getCommPort(portName);
        port.setBaudRate(57600);
        port.setNumDataBits(8);
        port.setNumStopBits(SerialPort.ONE_STOP_BIT);
        port.setParity(SerialPort.NO_PARITY);
        port.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        if (!port.openPort()) {
            throw new IOException("Failed to open port " + portName);
        }
        out = port.getOutputStream();
    }

    public void close() throws IOException {
        if (out != null) out.close();
        if (port != null) port.closePort();
    }

    // Method to write raw bytes (placeholder for actual API command serialization)
    public void writeBytes(byte[] data) throws IOException {
        out.write(data);
        out.flush();
    }

    // Placeholder for sending User_AddProgram command
    public int addProgram(int cardNum, boolean waitToEnd, int playTime) throws IOException {
        // Build and send protocol bytes for AddProgram
        // Return program index parsed from device response
        return 1; // Dummy for example
    }

    // Placeholder for sending User_AddText command
    public boolean addText(int cardNum, OverheadDisplayModes.UserText text, int programIndex) throws IOException {
        // Serialize UserText info via protocol, send it
        return true; // Dummy success
    }

    // Placeholder for sending User_SendToScreen command
    public boolean sendToScreen(int cardNum) throws IOException {
        // Send command bytes to apply changes on device
        return true;
    }
}
