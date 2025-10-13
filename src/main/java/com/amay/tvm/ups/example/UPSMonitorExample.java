package com.amay.tvm.ups.example;


import com.amay.tvm.ups.command.UPSCommand;
import com.amay.tvm.ups.communication.UPSCommunicationInterface;
import com.amay.tvm.ups.communication.UPSSerialCommunication;
import com.amay.tvm.ups.exception.UPSCommunicationException;
import com.amay.tvm.ups.model.UPSResponse;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Usage example for UPS Communication Interface
 */
public class UPSMonitorExample {

    public static void main(String[] args) {
        // Create UPS communication instance
        UPSCommunicationInterface upsComm ;

        try {
            // List available ports
            System.out.println("Available ports:");
            String[] ports = UPSSerialCommunication.getAvailablePorts();
            for (String port : ports) {
                System.out.println("  - " + port);
            }

            // Connect to UPS
            String portNumber = "COM3"; // Change to your port
            System.out.println("\nConnecting to UPS on " + portNumber + "...");
            upsComm=new UPSSerialCommunication("COM6");
            boolean connected = upsComm.isConnected();


            if (connected) {
                System.out.println("Connected: " + upsComm.isConnected());

                // Get input voltage
                double inputVoltage = upsComm.getIPVoltage();
                System.out.println("\nInput Voltage: " + inputVoltage + "V");

                // Get complete UPS response
                UPSResponse response = upsComm.getUPSResponseObject();
                System.out.println("\nComplete UPS Status:");
                System.out.println(response);
                System.out.println("\nUPS Status Details:");
                System.out.println(response.getStatus());

                // Check critical conditions
                if (response.getStatus().isUtilityFail()) {
                    System.out.println("\n⚠️ WARNING: Utility power failed!");
                }
                if (response.getStatus().isBatteryLow()) {
                    System.out.println("⚠️ WARNING: Battery low!");
                }
                if (response.getStatus().isUpsFailed()) {
                    System.out.println("🔴 CRITICAL: UPS failed!");
                }

                // Get UPS information
                String info = upsComm.fireCommand(UPSCommand.UPS_INFORMATION);
                System.out.println("\nUPS Information: " + info);

                // Execute test commands (commented for safety)
                // System.out.println("\nStarting 10-second test...");
                // String testResponse = upsComm.fireCommand(UPSCommand.TEST_10_SECONDS);
                // System.out.println("Test response: " + testResponse);

                // Custom command example
                // String customResponse = upsComm.fireCommand("T05"); // Test for 5 minutes
                // System.out.println("Custom command response: " + customResponse);

                // Start continuous monitoring
                startContinuousMonitoring(upsComm);

            }

        } catch (UPSCommunicationException e) {
            System.err.println("UPS Communication Error: " + e.getMessage());
        } finally {
            // Cleanup (comment out if continuous monitoring is running)
            // upsComm.disconnect();
        }
    }

    /**
     * Start continuous monitoring of UPS status
     */
    private static void startContinuousMonitoring(UPSCommunicationInterface upsComm) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (!upsComm.isConnected()) {
                    System.out.println("Connection lost. Attempting to reconnect...");
                    upsComm.reconnect();
                    return;
                }

                UPSResponse response = upsComm.getUPSResponseObject();
                System.out.printf("\n[%tT] Monitoring - Input: %.1fV, Output: %.1fV, Battery: %.2fV, Load: %d%%\n",
                        System.currentTimeMillis(),
                        response.getInputVoltage(),
                        response.getOutputVoltage(),
                        response.getBatteryVoltage(),
                        response.getOutputCurrent());

                // Check for alerts
                if (response.getStatus().isUtilityFail()) {
                    System.out.println("  ⚠️ ALERT: Running on battery!");
                }
                if (response.getStatus().isBatteryLow()) {
                    System.out.println("  🔴 CRITICAL: Battery low - shutdown imminent!");
                }

            } catch (UPSCommunicationException e) {
                System.err.println("Monitoring error: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS); // Poll every 5 seconds

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down monitor...");
            scheduler.shutdown();
            upsComm.disconnect();
        }));
    }

    /**
     * Example: Shutdown and restore sequence
     */
    private static void shutdownAndRestoreExample(UPSCommunicationInterface upsComm) {
        try {
            // Shutdown in 2 minutes, restore after 60 minutes
            String command = UPSCommand.shutdownAndRestore(2, 60);
            System.out.println("Executing: " + command);
            String response = upsComm.fireCommand(command);
            System.out.println("Response: " + response);

            // Cancel if needed
            // upsComm.fireCommand(UPSCommand.CANCEL_SHUTDOWN);

        } catch (UPSCommunicationException e) {
            System.err.println("Shutdown command failed: " + e.getMessage());
        }
    }
}
