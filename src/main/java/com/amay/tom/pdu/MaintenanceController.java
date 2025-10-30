package com.amay.tom.pdu;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.controller.DataSync;
import com.amay.tom.controller.MaintenanceImportExport;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.session.ShiftMapper;
import com.amay.tom.pdu.controller.MoneyManageController;
import com.amay.tom.pdu.controller.ReportController;
import com.amay.tom.pdu.controller.SystemInfoController;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.backend.enums.LoggerTag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.amaytechnosystems.ShiftStatus;
import org.tinylog.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
        FXMLLoader fxmlLoader= ViewFactory.getMaintenanceLoad();
        MaintenanceTestController controller=new MaintenanceTestController(this.agent,this.sceneManager);
        fxmlLoader.setControllerFactory((x)->controller);
        this.sceneManager.addToScene(fxmlLoader);
        actionEvent.consume();
    }

    @FXML
    private void onImpExp(ActionEvent actionEvent) {

        FXMLLoader fxmlLoader= ViewFactory.getMaintenanceImportExport();
        DataSync controller=new DataSync(this.agent,this.sceneManager);
        fxmlLoader.setControllerFactory((x)->controller);
        this.sceneManager.addToScene(fxmlLoader);
        actionEvent.consume();


    }

    @FXML
    private void onReports(ActionEvent actionEvent) {

        FXMLLoader fxmlLoader= ViewFactory.getReportsView();
        ReportController controller=new ReportController(this.agent,this.sceneManager);
        fxmlLoader.setControllerFactory((x)->controller);
        this.sceneManager.addToScene(fxmlLoader);
        actionEvent.consume();

    }

    @FXML
    private void onLogout(ActionEvent actionEvent) {
        Logger.tag(LoggerTag.APP).info("Logout pressed in maintenance");
        agent.getShiftService().resumeShift("admin");

        Shift shiftM = agent.getShiftMaintenance();
        agent.getShiftService().markLastShiftAsCompleted(shiftM.getShiftId());

        this.sceneManager.back();
        actionEvent.consume();
    }
}
