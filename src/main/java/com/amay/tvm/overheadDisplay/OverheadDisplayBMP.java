package com.amay.tvm.overheadDisplay;

import com.fazecast.jSerialComm.SerialPort;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OverheadDisplayBMP {
    private SerialPort serialPort;
    private OutputStream out;

    public OverheadDisplayBMP(String portName) throws IOException {
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(57600);  // Must match display spec
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        if (!serialPort.openPort()) {
            throw new IOException("Unable to open serial port " + portName);
        }
        out = serialPort.getOutputStream();
    }

    public void close() throws IOException {
        if (out != null) out.close();
        if (serialPort != null) serialPort.closePort();
    }

    public void sendBMP(String bmpFilePath) throws IOException {
        File file = new File(bmpFilePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("BMP file not found: " + bmpFilePath);
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Send BMP header and data in chunks
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                out.flush();
                // Optional: add small delay if necessary for device to catch up
                // Thread.sleep(5);
            }
        }
    }

    public static void main(String[] args) {
        String portName = "COM8";  // Change as needed
        String bmpFile = "D://Overhead Display Test Tool V3.0 (3)//Overhead Display Test Tool V3.0//BMP1.BMP";  // Change to your BMP file path

        OverheadDisplayBMP sender = null;
        try {
            sender = new OverheadDisplayBMP(portName);
            sender.sendBMP(bmpFile);
            System.out.println("BMP image sent successfully.");
        } catch (Exception e) {
            System.err.println("Error sending BMP image: " + e.getMessage());
        } finally {
            if (sender != null) {
                try {
                    sender.close();
                } catch (IOException ignored) {}
            }
        }
    }
}
