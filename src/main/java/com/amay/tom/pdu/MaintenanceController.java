package com.amay.tom.pdu;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.MoneyManageController;
import com.amay.tom.pdu.controller.SystemInfoController;
import com.amay.tom.pdu.controller.service.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MaintenanceController {
    private final Agent agent;
    private final SceneManager sceneManager;
    public MaintenanceController(Agent agent, SceneManager sceneManager) {
        this.agent = agent;
        this.sceneManager = sceneManager;
    }
    @FXML
    private void initialize() {

    }

    @FXML
    private void onSystemInfo(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader= ViewFactory.getSystemInfo();
        SystemInfoController controller=new SystemInfoController(this.agent,this.sceneManager);
        fxmlLoader.setControllerFactory((x)->controller);
        this.sceneManager.addToScene(fxmlLoader);
        actionEvent.consume();

    }

    @FXML
    private void onMoneyManage(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader= ViewFactory.getMoneyManagement();
        MoneyManageController controller=new MoneyManageController(this.agent,this.sceneManager);
        fxmlLoader.setControllerFactory((x)->controller);
        this.sceneManager.addToScene(fxmlLoader);
        actionEvent.consume();
    }

    @FXML
    private void onMain(ActionEvent actionEvent) {
    }

    @FXML
    private void onImpExp(ActionEvent actionEvent) {
    }

    @FXML
    private void onReports(ActionEvent actionEvent) {
    }

    @FXML
    private void onLogout(ActionEvent actionEvent) {
        this.sceneManager.back();
        actionEvent.consume();
    }
}
