package com.amay.tom.service.print;

import com.fazecast.jSerialComm.*;

public class SerialCommunication {

    public static void serialCommunication(){
        // Obtain a list of available serial ports
        SerialPort[] serialPorts = SerialPort.getCommPorts();

        if (serialPorts.length == 0) {
            //System.out.println("No serial ports found.");
        } else {
            // Print the available serial ports
            //System.out.println("Available Serial Ports:");
            for (SerialPort serialPort : serialPorts) {
                //System.out.println(serialPort.getSystemPortName());
            }

            // Choose the first serial port (you can modify this based on your needs)
            SerialPort chosenPort = serialPorts[1];

            // Open the chosen serial port
            if (chosenPort.openPort()) {
                //System.out.println("Serial port " + chosenPort.getSystemPortName() + " opened successfully.");

                // Set serial port parameters (baud rate, data bits, stop bits, parity)
                chosenPort.setBaudRate(9600);
                chosenPort.setNumDataBits(8);
                chosenPort.setNumStopBits(1);
                chosenPort.setParity(SerialPort.NO_PARITY);

                // Wait for data to be available
                while (chosenPort.bytesAvailable() == 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Create a thread for continuous reading
                Thread readerThread = new Thread(() -> {
                    try {
                        while (!Thread.interrupted()) {
                            // Read data from the serial port
                            byte[] buffer = new byte[400];
                            int bytesRead = chosenPort.readBytes(buffer, buffer.length);

                            // Process the received data (replace this with your own logic)
                            if (bytesRead > 0) {
                                String receivedData = new String(buffer, 0, bytesRead);
                                //System.out.println("Received data: " + receivedData);
                            }

                            // Wait for data to be available
                            while (chosenPort.bytesAvailable() == 0	) {
                                Thread.sleep(100);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                // Start the reader thread
                readerThread.start();

                // Wait for the user to press enter to stop the program
                //System.out.println("Press enter to exit.");
                try {
                    System.in.read();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Stop the reader thread
                readerThread.interrupt();

                // Close the serial port when done
                chosenPort.closePort();
                //System.out.println("Serial port closed.");
            } else {
                //System.out.println("Error opening serial port.");
            }
        }
    }
}
