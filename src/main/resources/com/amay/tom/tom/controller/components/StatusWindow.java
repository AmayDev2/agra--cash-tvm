package com.amay.tom.controller.components;

import com.amay.tom.config.ENVURL;
import com.amay.tom.controller.MaintenanceModuleTest;
import com.amay.tom.maintenance.service.component.StatusWindowPopupListener;
import com.amay.tom.maintenance.service.component.model.StatusWindowModel;
import com.amay.tom.service.print.impl.ImplPrintTicket;
import com.amay.tom.threadpool.ThreadPool;
import com.amay.tom.utils.env.EnvFile;
import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.tinylog.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StatusWindow {
    @FXML
    private VBox statusWindow;

    @FXML
    private Label statusLabel;

    @FXML
    private Label statusTextLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Button recheckButton;
    @FXML
    private Button testButton;
    @FXML
    private Button cancelButton;

    private final StatusWindowModel statusWindowModel;
    private final StatusWindowPopupListener statusWindowPopupListener;
    private final MaintenanceModuleTest maintenanceModuleTest;
    private final ThreadPool threadPool;
    private  Thread thread ;

    public StatusWindow(MaintenanceModuleTest maintenanceModuleTest, StatusWindowModel statusWindowModel, StatusWindowPopupListener statusWindowPopupListener, ThreadPool threadPool) {
        this.statusWindowModel = statusWindowModel;
        this.statusWindowPopupListener = statusWindowPopupListener;
        this.maintenanceModuleTest = maintenanceModuleTest;
        this.threadPool = threadPool;
    }

    public void initialize() {
        statusLabel.setText(statusWindowModel.isConnected()?"Connected":"Disconnected");
        statusLabel.setStyle(statusWindowModel.isConnected()?"-fx-text-fill: green;":"-fx-text-fill: red;");
        recheckButton.setText(statusWindowModel.getRelativeOperation());
        titleLabel.setText(statusWindowModel.getTitle());
        cancelButton.setOnAction(event -> {
            statusWindowPopupListener.Close();});
        if(statusWindowModel.isConnected())testButton.setOnAction(event -> this.performOperation());
        else testButton.setDisable(true);
    }

    private void performOperation() {
        testButton.setDisable(true);
        switch (statusWindowModel.getTitle()) {
            case "EFT":
                System.out.println("EFT operation performed");
                break;
            case "QR Printer":
                System.out.println("QR Printer operation performed");
                BufferedImage bufferedImage= this.findTicket("test");
                ImplPrintTicket.printImageMaintenance(bufferedImage,
                        EnvFile.getThermalPrinterModel(),   // printer name (or null for default)
                        "png",             // image format
                        true,              // auto-scale to fit page
                        300,               // DPI
                        0,                 // margin in mm
                        true               // verbose logging
                        );
                break;
            case "Receipt printer":
                System.out.println("Receipt printer operation performed");
                break;
            case "CSE":
                System.out.println("CSE operation performed");
                break;
            case "Pole":
                System.out.println("Pole operation performed");
                break;
            case "QR Scanner":
                System.out.println("QR Scanner operation performed");
                Platform.runLater(() -> statusTextLabel.setText("Scanning QR code..."));
                threadPool.getSingleThread().execute(() -> {
                    Thread.currentThread().setName("QR Scanner Thread");
                    thread=Thread.currentThread();
                    String qrCode = readQr();
                    Platform.runLater(() -> {
                        statusTextLabel.setText("QR code: " + qrCode);
                        testButton.setDisable(false);
                    });
                });

                break;
        }
    }

    //get ticket to print
    private BufferedImage findTicket(String ticketId) {
        if (ticketId != null) {
            File ticketsDirectory = new File(ENVURL.CONFIG+"\\tom-config\\ticket_images\\13-06-2025");
            if (!ticketsDirectory.exists() || !ticketsDirectory.isDirectory()) {
                System.err.println("Tickets directory not found or is not a directory.");
                return null;
            }

            File[] ticketFile = ticketsDirectory.listFiles();
            assert ticketFile != null;
            if (ticketFile[0].exists() && ticketFile[0].isFile()) {
                try {
                    return ImageIO.read(ticketFile[0]);
                } catch (IOException e) {
                    System.err.println("Error reading ticket image: " + e.getMessage());
                }

            }

        }
            System.out.println("Ticket image not found for ticket ID: " + ticketId);
        testButton.setDisable(false);
            return null;

    }


   private String readQr() {
        String readData = null;
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
                try {
                    // Read data from the serial port
                    byte[] buffer = new byte[400];
                    int bytesRead = finalChosenPort.readBytes(buffer, buffer.length);

                    // Process the received data (replace this with your own logic)
                    if(bytesRead > 0) {
                        String receivedData = new String(buffer, 0, bytesRead);
//                                readData = receivedData;
                        String[] arr=receivedData.split("\r");
                        Logger.info("Received data: {}", arr[arr.length-1]);

                        readData= arr[arr.length-1];

                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }

                chosenPort.closePort();
                Logger.debug("Serial port closed.");
            } else {
                System.out.println("Error opening serial port.");
            }
        }
        testButton.setDisable(false);
        return readData;
    }
}
