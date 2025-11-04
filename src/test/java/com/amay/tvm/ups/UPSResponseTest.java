package com.amay.tvm.ups;

import static org.junit.jupiter.api.Assertions.*;

import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.ups.command.UPSCommand;
import com.amay.tvm.ups.communication.UPSCommunicationInterface;
import com.amay.tvm.ups.communication.UPSSerialCommunication;
import com.amay.tvm.ups.exception.UPSCommunicationException;
import com.amay.tvm.ups.model.UPSResponse;
import com.amay.tvm.ups.model.UPSStatus;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Test class for UPSResponse
 */
public class UPSResponseTest {

    @Test
    public void testDefaultConstructor() {
        UPSResponse response = new UPSResponse();
        assertNotNull(response, "Response object should not be null");
        assertTrue(response.getTimestamp() > 0, "Timestamp should be positive");
    }

    @Test
    public void testSettersAndGetters() {
        UPSResponse response = new UPSResponse();

        response.setInputVoltage(208.4);
        response.setInputFaultVoltage(140.0);
        response.setOutputVoltage(208.4);
        response.setOutputCurrent(34);
        response.setInputFrequency(59.9);
        response.setBatteryVoltage(2.05);
        response.setTemperature(35.0);
        response.setRawResponse("(208.4 140.0 208.4 034 59.9 2.05 35.0 00110000)");

        UPSStatus status = new UPSStatus("00110000");
        response.setStatus(status);

        assertEquals(208.4, response.getInputVoltage());
        assertEquals(140.0, response.getInputFaultVoltage());
        assertEquals(208.4, response.getOutputVoltage());
        assertEquals(34, response.getOutputCurrent());
        assertEquals(59.9, response.getInputFrequency());
        assertEquals(2.05, response.getBatteryVoltage());
        assertEquals(35.0, response.getTemperature());
        assertEquals(status, response.getStatus());
        Logger.tag(LoggerTag.APP).info(response.toString());
//        assertEquals("UPSStatus{\"utilityFail\":false,\"batteryLow\":False,...}", status.toString());
    }

    @Test
    public void testToStringIncludesAllFields() {
        UPSResponse response = new UPSResponse();
        response.setInputVoltage(230.0);
        response.setInputFaultVoltage(120.0);
        response.setOutputVoltage(230.0);
        response.setOutputCurrent(50);
        response.setInputFrequency(60.0);
        response.setBatteryVoltage(3.7);
        response.setTemperature(30.0);
        UPSStatus status = new UPSStatus("11110000");
        response.setStatus(status);
        String str = response.toString();
        assertTrue(str.contains("inputVoltage=230.0"));
        assertTrue(str.contains("batteryVoltage=3.7"));
        assertTrue(str.contains("status=" + status.toString()));
    }

    @Test
    public void testParseStatusFlags() {
        // Status byte: 11110000 (all flags set)
        UPSStatus status = new UPSStatus("11110000");
//        assertTrue(status.isUtilityFail(), "Utility Fail flag should be true");
//        assertTrue(status.isBatteryLow(), "Battery Low flag should be true");
//        assertTrue(status.isBypassBoostActive(), "Bypass/Boost Active flag should be true");
//        assertTrue(status.isUpsFailed(), "UPS Failed flag should be true");
//        assertTrue(status.isStandbyType(), "Standby Type should be true");
//        assertFalse(status.isTestInProgress(), "Test In Progress should be false");
//        assertFalse(status.isShutdownActive(), "Shutdown Active should be false");
//        assertFalse(status.isBeeperOn(), "Beeper On should be false");
    }

    @Test
    public void testHandleInvalidStatusByte() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new UPSStatus("abcdefg"); // Not 8 characters
        });
        assertEquals("Status byte must be 8 characters", exception.getMessage());
    }

    @Test
    public void testCreateResponseFromSampleData() {
        String sampleResponseStr = "(208.4 140.0 208.4 034 59.9 2.05 35.0 00110000)";
        UPSResponse response = new UPSResponse();
        response.setRawResponse(sampleResponseStr);
        try {
            UPSResponse parsedResponse = parseSampleResponse(sampleResponseStr);
            assertNotNull(parsedResponse);
            assertEquals(208.4, parsedResponse.getInputVoltage());
            assertEquals(34, parsedResponse.getOutputCurrent());
//            assertTrue(parsedResponse.getStatus().isUtilityFail());
        } catch (Exception e) {
            fail("Parsing should not throw exception");
        }
    }

    // Helper method to simulate parsing string as in your main code
    private UPSResponse parseSampleResponse(String rawResponse) throws Exception {
        UPSResponse response = new UPSResponse();
        response.setRawResponse(rawResponse);
        String data = rawResponse.substring(1, rawResponse.length() - 1).trim(); // remove parentheses
        String[] fields = data.split("\\s+");
        response.setInputVoltage(Double.parseDouble(fields[0]));
        response.setInputFaultVoltage(Double.parseDouble(fields[1]));
        response.setOutputVoltage(Double.parseDouble(fields[2]));
        response.setOutputCurrent(Integer.parseInt(fields[3]));
        response.setInputFrequency(Double.parseDouble(fields[4]));
        response.setBatteryVoltage(Double.parseDouble(fields[5]));
        response.setTemperature(Double.parseDouble(fields[6]));
        response.setStatus(new UPSStatus(fields[7]));
        return response;
    }


//    @Test
   public void testCommunication() {
        // Create UPS communication instance
//        UPSCommunicationInterface upsComm = new UPSSerialCommunication();

        try {
            // List available ports
//            System.out.println("Available ports:");
//            String[] ports = UPSSerialCommunication.getAvailablePorts();
//            for (String port : ports) {
//                System.out.println("  - " + port);
//            }

            // Connect to UPS
            String portNumber = "COM6"; // Change to your port
            System.out.println("\nConnecting to UPS on " + portNumber + "...");
            boolean connected = UPS.INTERFACE.setupUPS(portNumber);

            if (connected) {
                System.out.println("Connected: " + UPS.INTERFACE.isConnected());

                // Get input voltage
                double inputVoltage = UPS.INTERFACE.getIPVoltage();
                System.out.println("\nInput Voltage: " + inputVoltage + "V");

                // Get complete UPS response
                UPSResponse response = UPS.INTERFACE.getUPSResponseObject();
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
//                String info = upsComm.fireCommand(UPSCommand.UPS_INFORMATION);
//                System.out.println("\nUPS Information: " + info);

                // Execute test commands (commented for safety)
                // System.out.println("\nStarting 10-second test...");
                // String testResponse = upsComm.fireCommand(UPSCommand.TEST_10_SECONDS);
                // System.out.println("Test response: " + testResponse);

                // Custom command example
                // String customResponse = upsComm.fireCommand("T05"); // Test for 5 minutes
                // System.out.println("Custom command response: " + customResponse);

            }
            // Start continuous monitoring
            startContinuousMonitoring();

            Scanner sc=new Scanner(System.in);
            sc.next();

        } catch (UPSCommunicationException e) {
            System.err.println("UPS Communication Error: " + e.getMessage());
        } finally {
            // Cleanup (comment out if continuous monitoring is running)
            // upsComm.disconnect();
        }
    }


    private  void startContinuousMonitoring() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (!UPS.INTERFACE.isConnected()) {
                    System.out.println("Connection lost. Attempting to reconnect...");
                    return;
                }

                UPSResponse response = UPS.INTERFACE.getUPSResponseObject();
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
            UPS.INTERFACE.disconnect();
        }));
    }

}