package com.amay.tom.controller;

import com.amay.tom.coin.CoinModuleInterface;
import com.amay.tom.coin.enums.MotorTest;
import com.amay.tom.coin.enums.TicketResultFeed;
import com.amay.tom.coin.enums.TicketResultIssue;
import com.fazecast.jSerialComm.SerialPort;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class MauritiusCscController {
        @FXML private ToggleGroup groupIssue;
        @FXML private ToggleGroup groupFeed;
        @FXML private ComboBox<String> comboComPort;
        @FXML private ComboBox<MotorTest> comboMotor;

        @FXML private RadioButton rbIssuePassenger;
         @FXML private RadioButton rbIssueReject;

        @FXML private RadioButton rbAutoFeed;
        @FXML private RadioButton rbManualFeed;

         @FXML private TextArea txtLog;
         @FXML private TextField txtLogDays;

         @FXML
        public void initialize(){
             append("System Initialized.");
             SerialPort[] portList = SerialPort.getCommPorts();

             // Clear previous values (optional)
             comboComPort.getItems().clear();

             for (SerialPort port : portList) {
                 String portName = port.getSystemPortName();      // Example: COM3, COM4, etc.
                 // String portName = port.getDescriptivePortName(); // If you prefer descriptive names
                 comboComPort.getItems().add(portName);
             }

             if (! comboComPort.getItems().isEmpty()) {
                 comboComPort.getSelectionModel().selectFirst(); // Auto-select first available port
             }

             comboMotor.getItems().addAll(MotorTest.values());

             rbIssuePassenger.setSelected(true);
             rbAutoFeed.setSelected(true);
         }
         @FXML
        private void onExit(){
             System.exit(0);
         }

         @FXML
        private void onClearInfo(){
             txtLog.clear();
             append("Log files Deleted");
         }

         @FXML
        private void onDeleteLogs(){
             append("Log files Deleted");
         }
        @FXML
        private void onOpenPort() {
            CoinModuleInterface.INSTANCE.setupCoinModule(comboComPort.getValue());
            append("Opening Port: " + comboComPort.getValue());

        }

        @FXML
        private void onClosePort() {
             CoinModuleInterface.INSTANCE.closeCoinModule();
        }

        @FXML
        private void onGetStatus() {
            append("Getting Status...");
        }

        @FXML
        private void onMotorTest() {
            MotorTest motorTest=comboMotor.getSelectionModel().getSelectedItem();

        }



        @FXML
        private void onFeedTicket() {
            append("Feed Ticket clicked.");
        }

        @FXML
        private void onTicketResult() {
            TicketResultIssue issue= rbIssuePassenger.isSelected()?TicketResultIssue.TO_PASSENGER:TicketResultIssue.TO_REJECT;
            TicketResultFeed feed= rbAutoFeed.isSelected()?TicketResultFeed.AUTO:TicketResultFeed.MANUAL;

        }

        @FXML
        private void onGetVersion() {
            append("Fetching Version...");
        }

        @FXML
        private void onChangeTicketBox() {

        }


        private void append(String msg) {
            txtLog.appendText(msg + "\n");
        }

}
