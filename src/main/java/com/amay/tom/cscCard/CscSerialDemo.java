package com.amay.tom.cscCard;

import com.fazecast.jSerialComm.SerialPort;

public class CscSerialDemo {
    public static void main(String[] args) {
        String portName = args.length > 0 ? args[0] : "COM8";

        SerialPort port = SerialPort.getCommPort(portName);
        port.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 2000);

        if (!port.openPort()) {
            System.err.println("Failed to open port " + portName);
            return;
        }

        System.out.println("Opened port " + portName);

        CscProtocolClient client = new CscProtocolClient(port);

        try {
            // Example flow: reset, request status, dispense, request status, issue, request status
            client.resetModule();
            Thread.sleep(1000);

            printStatus(client);

            client.dispenseTicket();
            Thread.sleep(1000);

            printStatus(client);

            client.issueTicket();
            Thread.sleep(1000);

            printStatus(client);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            port.closePort();
            System.out.println("Port closed");
        }
    }

    private static void printStatus(CscProtocolClient client) throws Exception {
        byte[] resp = client.requestStatus();
        byte d1 = resp[3];
        byte d2 = resp[4];
        byte d3 = resp[5];
        String status = CscStatusDecoder.decodeStatus(d1, d2, d3);
        System.out.printf("Status: %s (D1=%02X D2=%02X D3=%02X)%n", status, d1, d2, d3);
    }
}
