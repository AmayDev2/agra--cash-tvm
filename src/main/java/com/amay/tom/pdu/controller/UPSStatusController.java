package com.amay.tom.pdu.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tom.utils.env.EnvLoader;
import com.amay.tvm.ups.UPS;
import com.amay.tvm.ups.UPSInterface;
import com.amay.tvm.ups.command.UPSCommand;
import com.amay.tvm.ups.communication.UPSCommunicationInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UPSStatusController {

    @FXML private TextField txtInputVoltage, txtInputFaultVoltage, txtOutputVoltage, txtOutputCurrent,
            txtInputFrequency, txtBatteryVoltage, txtTemperature;

    @FXML private Label lblUtilityNormal, lblBatteryNormal, lblBypass, lblUPSNormal,
            lblStandbyUPS, lblTestProgress, lblShutdown, lblBeeper;

    @FXML private TextArea txtLog;


    private Agent agent;
    private SceneManager sceneManager;

    public UPSStatusController(Agent agent, SceneManager sceneManager) {

        this.agent = agent;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {

    }

    @FXML
    private void onOpenPort() {
        if (UPS.INTERFACE.setupUPS(EnvFile.getUPSComPort())) {
            txtLog.appendText("Port opened successfully.\n");
        } else {
            txtLog.appendText("Failed to open port.\n");
        }
    }

    @FXML
    private void onClosePort() {
        UPS.INTERFACE.disconnect();
        txtLog.appendText("Port closed.\n");
    }

    @FXML
    private void onGetStatus() {
        try {
            String response = String.valueOf(UPS.INTERFACE.getUPSResponseObject());

            // Check if response is empty or not
            if (response == null || response.trim().isEmpty()) {
                txtLog.appendText("No response from UPS.\n");
                return;
            }

//            UPSDataPareserDto dto = UPSDataParser.parse(response);

//            if (dto != null) {
//                fillUI(dto);
//                txtLog.appendText("✅ Status Inquiry successful.\nASCII: " + response + "\n");
//            } else {
//                txtLog.appendText("⚠️ Unable to parse UPS response properly.\nRaw: " + response + "\n");
//            }
        } catch (Exception e) {
            txtLog.appendText("❌ Exception while getting UPS status: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    @FXML
    private void onShutdown() {
        try {
            int minutes = 1;
            String response = UPS.INTERFACE.fireCommand(UPSCommand.shutdown(1));
            txtLog.appendText("Shutdown command sent successfully. UPS will shut down in 1 minute: " + "\n");
            lblShutdown.setText("Shutdown Active");
        } catch (Exception e) {
            txtLog.appendText("Error during shutdown: " + e.getMessage() + "\n");
        }
    }


    @FXML
    private void onClearDisplay() {
        txtLog.clear();
    }

    @FXML
    private void onCancelShutdown() {
        try {
            String response = UPS.INTERFACE.fireCommand(UPSCommand.CANCEL_SHUTDOWN);
            txtLog.appendText("Shutdown cancelled successfully. UPS response: " + response + "\n");
            lblShutdown.setText("Shutdown not Active");
        } catch (Exception e) {
            txtLog.appendText("Error cancelling shutdown: " + e.getMessage() + "\n");
        }
    }

//    private void fillUI(UPSDataPareserDto dto) {
//        txtInputVoltage.setText(dto.getInputVoltage());
//        txtInputFrequency.setText(dto.getInputFrequency());
//        txtInputFaultVoltage.setText(dto.getInputFaultVoltage());
//        txtOutputVoltage.setText(dto.getOutputVoltage());
//        txtOutputCurrent.setText(dto.getOutputCurrent());
//        txtBatteryVoltage.setText(dto.getBatteryVoltage());
//        txtTemperature.setText(dto.getTemperature());
//
//        String statusBits = dto.getStatus();
//
//        lblUtilityNormal.setText(statusBits.charAt(0) == '1' ? "Utility Fail" : "Utility Normal");
//        lblBatteryNormal.setText(statusBits.charAt(1) == '1' ? "Battery Low" : "Battery Normal");
//        lblBypass.setText(statusBits.charAt(2) == '1' ? "Bypass Active" : "Bypass not Active");
//        lblUPSNormal.setText(statusBits.charAt(3) == '1' ? "UPS Failed" : "UPS Normal");
//        lblStandbyUPS.setText(statusBits.charAt(4) == '1' ? "On-Battery UPS" : "Standby UPS");
//        lblShutdown.setText(statusBits.charAt(5) == '1' ? "Shutdown Active" : "Shutdown not Active");
//        lblTestProgress.setText(statusBits.charAt(6) == '1' ? "Test in Progress" : "Test not in Progress");
//        lblBeeper.setText(statusBits.charAt(7) == '1' ? "UPS Beeper On" : "UPS Beeper Off");
//    }

    @FXML
    private void onBack(ActionEvent event) {
        this.sceneManager.back();
        event.consume();
    }
}
