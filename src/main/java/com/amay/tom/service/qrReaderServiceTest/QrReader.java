package com.amay.tom.service.qrReaderServiceTest;

//import javafx.scene.control.Alert;
//import javafx.scene.control.TextArea;
//import javafx.scene.control.TextField;
//import org.tinylog.Logger;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicLong;
/*
public class QrReader extends Thread {

    @Override
    public void run() {
        Thread.currentThread().setName("QR SCANNER");this.readQr();
    }

    private void showErrorAlert(Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("QR Scanner Error");
        alert.setHeaderText("Error in QR Scanner");
        alert.setContentText(throwable.getMessage());

    }

    private List<QrCodeListener> listeners = new ArrayList<>();

    private volatile boolean isRunning = true;
    public String readData= "";

    public void addQrCodeListener(QrCodeListener listener) {
        listeners.add(listener);
    }

    public void removeQrCodeListener(QrCodeListener listener) {
        listeners.remove(listener);
    }

    public void stopReading() {
        Logger.info(Thread.currentThread().getName());

            Thread.currentThread().interrupt();
    }

    public void readQr() {
        // Obtain a list of available serial ports
        SerialPort[] serialPorts = SerialPort.getCommPorts();

        // Choose the first serial port (you can modify this based on your needs)
        SerialPort chosenPort = null;
        if (serialPorts.length == 0) {
            Logger.error("No serial ports available.");
        } else {
            // Print the available serial ports
            System.out.println("Available Serial Ports:");
            for (SerialPort serialPort : serialPorts) {
                Logger.info("Serial Port: {} {} {}", serialPort.getSystemPortName(), serialPort.getDescriptivePortName(), serialPort.getPortDescription());
                if(serialPort.getPortDescription().equals(EnvFile.getQRScannerModel())){
                    chosenPort = serialPort;

                }

            }

            // Open the chosen serial port
            if (chosenPort!=null && chosenPort.openPort()) {
                Logger.info("Serial port opened.");

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
                final SerialPort finalChosenPort = chosenPort;

//                Thread readerThread = new Thread(() -> {
                    try {
//                        while (isRunning && !Thread.interrupted()) {
                            // Read data from the serial port
                            byte[] buffer = new byte[400];
                            int bytesRead = finalChosenPort.readBytes(buffer, buffer.length);

                            // Process the received data (replace this with your own logic)
                        if(bytesRead > 0) {
                                String receivedData = new String(buffer, 0, bytesRead);
//                                readData = receivedData;

                                Logger.info("Received data: {}", receivedData.substring(0, receivedData.length() - 1).length());

                                // Notify listeners
                                notifyListeners(receivedData.substring(0, receivedData.length() - 1));

                            }

                    }

                    catch (Exception e) {
                        e.printStackTrace();
                    }

                chosenPort.closePort();
                Logger.debug("Serial port closed.");
            } else {
                System.out.println("Error opening serial port.");
                notifyListeners("MDNzdDAxZXEwOWVxMzIxc3QwMXN0MDgwMzI0MzAxNDQ4MDEwMDMwQTAxMDAwMDAwMDYw");
            }
        }
    }

    private void notifyListeners(String qrCodeData) {
        for (QrCodeListener listener : listeners) {
            listener.onQrCodeDetected(qrCodeData);
        }
    }
}*/

import com.amay.tom.service.qrReaderServiceTest.QrCodeListener;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

public class QrReader extends Thread {

    private final List<QrCodeListener> listeners = new ArrayList<>();
    private final StringBuilder buffer = new StringBuilder();
    private final StringBuilder allScannedData = new StringBuilder();
    private long lastKeyTime = 0;

    private final Pane targetPane; // your main screen's layout container

    public QrReader(Pane targetPane) {
        this.targetPane = targetPane;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("QR SCANNER");
        Platform.runLater(this::initializeScannerField); // integrate into existing UI
    }

    private void initializeScannerField() {
        TextField scannerInput = new TextField();
        scannerInput.setOpacity(0);               // Invisible
        scannerInput.setPrefSize(1, 1);           // Minimal size
        scannerInput.setFocusTraversable(true);

        // Add to your main screen pane
        targetPane.getChildren().add(scannerInput);

        scannerInput.setOnKeyTyped(event -> {
            long now = System.currentTimeMillis();
            if (now - lastKeyTime > 100) buffer.setLength(0);
            lastKeyTime = now;

            String ch = event.getCharacter();
            if ("\r".equals(ch) || "\n".equals(ch)) {
                String qr = buffer.toString().trim();
                buffer.setLength(0);
                if (!qr.isEmpty()) {
                    Logger.info("Scanned QR: {}", qr);
                    allScannedData.append(qr).append("\n");
                    notifyListeners(qr);
                }
            } else {
                buffer.append(ch);
            }
        });

        // Ensure it gains focus after scene loads
        Platform.runLater(scannerInput::requestFocus);
    }

    private void notifyListeners(String qrCodeData) {
        for (QrCodeListener listener : listeners) {
            listener.onQrCodeDetected(qrCodeData);
        }
    }

    public void addQrCodeListener(QrCodeListener listener) {
        listeners.add(listener);
    }

    public void removeQrCodeListener(QrCodeListener listener) {
        listeners.remove(listener);
    }
}
