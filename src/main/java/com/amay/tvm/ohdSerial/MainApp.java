package com.amay.tvm.ohdSerial;

public class MainApp {

    public static final String DISPLAY_PORT = "COM10";
    public static final int cardNum = 1;

    public static void main(String[] args) {

        System.out.println("=== EQ2008 Display Test ===");

        // Step 1: Check Serial Port Connectivity
        if (!SerialPortHelper.isPortAvailable(DISPLAY_PORT)) {
            System.out.println("❌ Display not detected on " + DISPLAY_PORT);
            SerialPortHelper.printPorts();
            return;
        }
        System.out.println("✔ Display found on " + DISPLAY_PORT);

        // Step 2: Use Display Service
        DisplayService display = new DisplayService(cardNum);
        IniHelper.updateBaudRate(57600);

        if (!display.connect()) {
            System.out.println("❌ DLL could not connect");
            return;
        }

        // Step 3: Send Text
        System.out.println("Sending text...");
        display.sendText("HelloEQ2008!", 0, 0, 128, 16);

        // Step 4: Disconnect
        display.disconnect();

        System.out.println("=== Finished ===");
    }
}
