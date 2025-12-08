package com.amay.tvm.ohdSerial;

import com.fazecast.jSerialComm.SerialPort;

public class SerialPortHelper {

    public static boolean isPortAvailable(String portName) {
        for (SerialPort port : SerialPort.getCommPorts()) {
            if (port.getSystemPortName().equalsIgnoreCase(portName)) {
                return true;
            }
        }
        return false;
    }

    public static void printPorts() {
        System.out.println("Available COM Ports:");
        for (SerialPort port : SerialPort.getCommPorts()) {
            System.out.println(" - " + port.getSystemPortName());
        }
    }
}
