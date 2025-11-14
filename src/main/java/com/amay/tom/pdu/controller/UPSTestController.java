package com.amay.tom.pdu.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tvm.ups.UPS;
import com.amay.tvm.ups.command.UPSCommand;
import com.amay.tvm.ups.model.UPSResponse;
import com.amay.tvm.ups.model.UPSStatus;
import com.amay.tvm.util.Page.FocusUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class UPSTestController {

    private final SceneManager sceneManager;
    @FXML private Button btnRefresh;
    @FXML private Button btnBack;
    @FXML private Label txtInputVoltage, txtInputFaultVoltage, txtOutputVoltage, txtOutputCurrent,
            txtInputFrequency, txtBatteryVoltage, txtTemperature;

    @FXML private Label lblUtilityNormal, lblBatteryNormal, lblBypass, lblUPSNormal,
            lblStandbyUPS, lblTestProgress, lblShutdown, lblBeeper;

    @FXML private TextArea txtLog;


    public UPSTestController(Agent agent, SceneManager sceneManager){
        this.sceneManager=sceneManager;
    }

    @FXML
    public void initialize(){
        FocusUtil.configureFocus(btnRefresh,btnBack);
    }

    @FXML private void onBack(ActionEvent actionEvent) {

        sceneManager.back();
        actionEvent.consume();
    }

    @FXML private void onOpenPort(ActionEvent actionEvent) {
        if (UPS.INTERFACE.setupUPS(EnvFile.getUPSComPort())) {
            txtLog.appendText("Port opened successfully.\n");
        } else {
            txtLog.appendText("Failed to open port.\n");
        }

        actionEvent.consume();
    }

    @FXML private void onClosePort(ActionEvent actionEvent) {
        UPS.INTERFACE.disconnect();
        txtLog.appendText("Port closed.\n");
        actionEvent.consume();
    }

    @FXML private void onClearDisplay(ActionEvent actionEvent) {

        actionEvent.consume();
    }

    @FXML private void onGetStatus(ActionEvent actionEvent) {

        try {
            UPSResponse response = UPS.INTERFACE.getUPSResponseObject();

            if (response != null) {
                fillUI(response);
                txtLog.appendText("✅ Status Inquiry successful.\nASCII: " + response + "\n");
            } else {
                txtLog.appendText("⚠️ Unable to parse UPS response properly.\nRaw: " + response + "\n");
            }
        } catch (Exception e) {
            txtLog.appendText("❌ Exception while getting UPS status: " + e.getMessage() + "\n");
        }

        actionEvent.consume();
    }

    @FXML private void onCancelShutdown(ActionEvent actionEvent) {
        try {
            String response = UPS.INTERFACE.fireCommand(UPSCommand.CANCEL_SHUTDOWN);
            txtLog.appendText("Shutdown cancelled successfully. UPS response: " + response + "\n");
            lblShutdown.setText("Shutdown not Active");
        } catch (Exception e) {
            txtLog.appendText("Error cancelling shutdown: " + e.getMessage() + "\n");
        }

        actionEvent.consume();
    }

    @FXML private void onShutdown(ActionEvent actionEvent) {

        try {
            String response = UPS.INTERFACE.fireCommand(UPSCommand.shutdown(1));
            txtLog.appendText("Shutdown command sent successfully. UPS will shut down in 1 minute: " + "\n");
            lblShutdown.setText("Shutdown Active");
        } catch (Exception e) {
            txtLog.appendText("Error during shutdown: " + e.getMessage() + "\n");
        }

        actionEvent.consume();
    }

    private void fillUI(UPSResponse dto) {
        txtInputVoltage.setText(String.valueOf(dto.getInputVoltage()));
        txtInputFrequency.setText(String.valueOf(dto.getInputFrequency()));
        txtInputFaultVoltage.setText(String.valueOf(dto.getInputFaultVoltage()));
        txtOutputVoltage.setText(String.valueOf(dto.getOutputVoltage()));
        txtOutputCurrent.setText(String.valueOf(dto.getOutputCurrent()));
        txtBatteryVoltage.setText(String.valueOf(dto.getBatteryVoltage()));
        txtTemperature.setText(String.valueOf(dto.getTemperature()));
        UPSStatus upsStatus=dto.getStatus();
        lblUtilityNormal.setText(upsStatus.isUtilityFail() ? "Utility Fail" : "Utility Normal");
        lblBatteryNormal.setText(upsStatus.isBatteryLow() ? "Battery Low" : "Battery Normal");
        lblBypass.setText(upsStatus.isBypassBoostActive() ? "Bypass Active" : "Bypass not Active");
        lblUPSNormal.setText(upsStatus.isUpsFailed() ? "UPS Failed" : "UPS Normal");
        lblStandbyUPS.setText(upsStatus.isStandbyType() ? "On-Battery UPS" : "Standby UPS");
        lblShutdown.setText(upsStatus.isShutdownActive() ? "Shutdown Active" : "Shutdown not Active");
        lblTestProgress.setText(upsStatus.isTestInProgress() ? "Test in Progress" : "Test not in Progress");
        lblBeeper.setText(upsStatus.isBeeperOn() ? "UPS Beeper On" : "UPS Beeper Off");
    }
}
