package com.amay.tom.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;



public class MauritiusCscController {
        @FXML private ComboBox<String> comboComPort;
        @FXML private ComboBox<String> comboMotor;

        @FXML private RadioButton rbIssuePassenger;
         @FXML private RadioButton rbIssueReject;

        @FXML private RadioButton rbAutoFeed;
        @FXML private RadioButton rbManualFeed;

         @FXML private TextArea txtLog;
         @FXML private TextField txtLogDays;

         @FXML
        public void initialize(){
             comboComPort.getItems().addAll("COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "COM10");
             comboMotor.getItems().addAll("Open all motor", "Feeder Motor", "Transport Motor", "Motor Testing off");


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
            append("Opening Port: " + comboComPort.getValue());
        }

        @FXML
        private void onClosePort() {
            append("Closing Port...");
        }

        @FXML
        private void onGetStatus() {
            append("Getting Status...");
        }

        @FXML
        private void onMotorTest() {
            append("Motor Test Started...");
        }



        @FXML
        private void onFeedTicket() {
            append("Feed Ticket clicked.");
        }

        @FXML
        private void onTicketResult() {
            append("Checking Ticket Result...");
        }

        @FXML
        private void onGetVersion() {
            append("Fetching Version...");
        }

        @FXML
        private void onChangeTicketBox() {
            append("Changing Ticket Box...");
        }



        private void append(String msg) {
            txtLog.appendText(msg + "\n");
        }

}
