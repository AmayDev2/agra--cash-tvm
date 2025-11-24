package com.amay.tom.cscCard;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.Arrays;

public class CscJavaFxUIController {

    private SerialPort port;
    private CscProtocolClient client;

    @FXML private Button openPortBtn;
    @FXML private Button closePortBtn;
    @FXML private Button resetBtn;
    @FXML private Button dispenseBtn;
    @FXML private Button issueBtn;
    @FXML private Button captureBtn;
    @FXML private Button statusBtn;
    @FXML private TextArea responseArea;
    @FXML private ComboBox<String> portComboBox;




    @FXML
    private void handleOpenPort() {
        String selectedPort = portComboBox.getSelectionModel().getSelectedItem();
        if (selectedPort == null) {
            appendResponse("No COM port selected");
            return;
        }
        try {
            port = SerialPort.getCommPort(selectedPort);
            port.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 2000);

            if (port.openPort()) {
                client = new CscProtocolClient(port);
                appendResponse("Port " + selectedPort + " opened.");
            } else {
                appendResponse("Failed to open port " + selectedPort);
            }
        } catch (Exception e) {
            appendResponse("Error opening port: " + e.getMessage());
        }
    }

    @FXML
    private void handleClosePort() {
        if (port != null && port.isOpen()) {
            port.closePort();
            appendResponse("Port closed.");
        } else {
            appendResponse("Port not open.");
        }
    }


    @FXML
    private void handleReset() {
        sendCommand(() -> {
            try {
                client.resetModule();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void handleDispense() {
        sendCommand(() -> {
            try {
                client.dispenseTicket();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void handleIssue() {
        sendCommand(() -> {
            try {
                client.issueTicket();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void handleCapture() {
        sendCommand(() -> {
            try {
                client.captureTicket();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void handleStatus() {
        if (client == null || port == null || !port.isOpen()) {
            appendResponse("Port not open");
            return;
        }
        new Thread(() -> {
            try {
                byte[] resp = client.requestStatus();
                byte d1 = resp[3], d2 = resp[4], d3 = resp[5];
                String status = CscStatusDecoder.decodeStatus(d1, d2, d3);
                appendResponse("Status: " + status);
            } catch (Exception e) {
                appendResponse("Failed to get status: " + e.getMessage());
            }
        }).start();
    }
    @FXML
    public void initialize() {
        // Populate combo box with available COM ports on startup
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            portComboBox.getItems().add(port.getSystemPortName());
        }
        if (!portComboBox.getItems().isEmpty()) {
            portComboBox.getSelectionModel().selectFirst();
        }
    }
    @FXML
    private void handleOpenSelectedPort() {
        String selectedPort = portComboBox.getSelectionModel().getSelectedItem();
        if (selectedPort == null) {
            appendResponse("No COM port selected!");
            return;
        }
        // Open port, e.g.:
        SerialPort port = SerialPort.getCommPort(selectedPort);
        port.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 2000);

        if (port.openPort()) {
            appendResponse("Opened port " + selectedPort);
            // assign to client etc.
        } else {
            appendResponse("Failed to open port " + selectedPort);
        }
    }


    private void sendCommand(Runnable command) {
        if (client == null || port == null || !port.isOpen()) {
            appendResponse("Open port first.");
            return;
        }
        new Thread(() -> {
            try {
                command.run();
                appendResponse("Command executed.");
            } catch (Exception e) {
                appendResponse("Error: " + e.getMessage());
            }
        }).start();
    }

    private void appendResponse(String message) {
        Platform.runLater(() -> responseArea.appendText(message + "\n"));
    }
}
