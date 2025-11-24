package com.amay.tom.cscCard;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CscProtocolClient {
    private final SerialPort port;

    public CscProtocolClient(SerialPort port) {
        this.port = port;
    }

    // High-level operations

    public void resetModule() throws IOException {
        System.out.println("Sending RESET command...");
        sendCommandAndExecute(CscConstants.CMD_RESET);
    }

    public void dispenseTicket() throws IOException {
        System.out.println("Sending DISPENSE command...");
        sendCommandAndExecute(CscConstants.CMD_DISPENSE);
    }

    public void captureTicket() throws IOException {
        System.out.println("Sending CAPTURE command...");
        sendCommandAndExecute(CscConstants.CMD_CAPTURE);
    }

    public void issueTicket() throws IOException {
        System.out.println("Sending ISSUE command...");
        sendCommandAndExecute(CscConstants.CMD_ISSUE);
    }

    public byte[] requestStatus() throws IOException {
        sendCommandAndExecute(CscConstants.CMD_REQFLAG);

        InputStream in = port.getInputStream();
        byte[] resp = in.readNBytes(8);
        if (resp.length != 8) {
            throw new IOException("Incomplete response (expected 8 bytes)");
        }

        // Check framing and BCC
        if (resp[0] != CscConstants.STX || resp[6] != CscConstants.ETX) {
            throw new IOException("Bad response framing");
        }

        byte calcBcc = 0x00;
        for (int i = 0; i <= 6; i++) {
            calcBcc ^= resp[i];
        }
        if (calcBcc != resp[7]) {
            throw new IOException("BCC mismatch in response");
        }

        // For Request Flag response, codes should be 'S','F'
        if (resp[1] != 0x53 || resp[2] != 0x46) {
            throw new IOException(String.format(
                    "Unexpected response codes: %02X %02X", resp[1], resp[2]));
        }

        return resp;
    }

    // Core send/execute

    private void sendCommandAndExecute(byte[] command) throws IOException {
        InputStream in = port.getInputStream();
        OutputStream out = port.getOutputStream();

        // 1. Send command
        out.write(command);
        out.flush();

        // 2. Wait for ACK / NAK
        int b = in.read();
        if (b == -1) {
            throw new IOException("Timeout waiting for ACK/NAK");
        }
        byte resp = (byte) b;
        if (resp == CscConstants.NAK) {
            throw new IOException("Received NAK from module");
        }
        if (resp != CscConstants.ACK) {
            throw new IOException(String.format("Unexpected byte 0x%02X instead of ACK", resp));
        }

        // 3. Send ENQ to execute
        out.write(CscConstants.ENQ);
        out.flush();
    }
}
