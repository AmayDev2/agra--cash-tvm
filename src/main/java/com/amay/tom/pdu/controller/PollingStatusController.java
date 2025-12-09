package com.amay.tom.pdu.controller;


import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.coin.CoinModuleInterface;
import com.amay.tvm.coin.model.PollingStatusResponse;
import com.amay.tvm.util.Page.FocusUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PollingStatusController {

    private final SceneManager sceneManager;
    private final Agent agent;
    @FXML private Button backButton;
    @FXML private Label hopper1Pos, hopper1Empty;
    @FXML private Label hopper2Pos, hopper2Empty;
    @FXML private Label hopper3Pos, hopper3Empty;

    @FXML private Label maintenanceDoor, coinRefillDoor, hopperDoor, banknoteModule;
    @FXML private Label statusDescription;
    @FXML private Button refreshButton;

    public PollingStatusController(Agent agent, SceneManager sceneManager) {
        this.agent = agent;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        refreshButton.setOnAction(e -> loadStatus());
        Platform.runLater(() -> backButton.requestFocus());
        FocusUtil.configureTabOrder(backButton,refreshButton);
//        loadStatus();
    }

    private void loadStatus() {
        // TODO: Replace this mock with real device polling result
        PollingStatusResponse pollingStatusResponse=CoinModuleInterface.INSTANCE.pooling();
        updateUI(pollingStatusResponse);

    }

    private void updateUI(PollingStatusResponse res) {
        hopper1Pos.setText("Position: " + (res.isHopper1InPosition() ? "In" : "Out"));
        hopper1Empty.setText("Empty: " + (res.isHopper1AlmostEmpty() ? "Yes" : "No"));

        hopper2Pos.setText("Position: " + (res.isHopper2InPosition() ? "In" : "Out"));
        hopper2Empty.setText("Empty: " + (res.isHopper2AlmostEmpty() ? "Yes" : "No"));

        hopper3Pos.setText("Position: " + (res.isHopper3InPosition() ? "In" : "Out"));
        hopper3Empty.setText("Empty: " + (res.isHopper3AlmostEmpty() ? "Yes" : "No"));

        maintenanceDoor.setText("Maintenance Door: " + (res.isMaintenanceDoorOpen() ? "OPEN" : "CLOSED"));
        coinRefillDoor.setText("Coin Refill Door: " + (res.isCoinRefillDoorOpen() ? "OPEN" : "CLOSED"));
        hopperDoor.setText("Hopper Maintenance Door: " + (res.isHopperMaintenanceDoorOpen() ? "OPEN" : "CLOSED"));
        banknoteModule.setText("Banknote Module: " + (res.isBanknoteModuleInPosition() ? "IN POSITION" : "NOT IN POSITION"));

        statusDescription.setText("Status: " + res.getStatusDescription());

        // Optional: color coding
        setColor(hopper1Pos, res.isHopper1InPosition());
        setColor(hopper2Pos, res.isHopper2InPosition());
        setColor(hopper3Pos, res.isHopper3InPosition());
        setColor(maintenanceDoor, !res.isMaintenanceDoorOpen());
    }

    private void setColor(Label label, boolean ok) {
        label.setStyle(ok ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    public void onClickBack(ActionEvent actionEvent) {
        sceneManager.back();
        actionEvent.consume();
    }
}