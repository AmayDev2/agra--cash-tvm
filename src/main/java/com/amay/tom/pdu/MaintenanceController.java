package com.amay.tom.pdu;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.session.ShiftMapper;
import com.amay.tom.pdu.controller.MoneyManageController;
import com.amay.tom.pdu.controller.ReportController;
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

        FXMLLoader fxmlLoader= ViewFactory.getReportsView();
        ReportController controller=new ReportController(this.agent,this.sceneManager);
        fxmlLoader.setControllerFactory((x)->controller);
        this.sceneManager.addToScene(fxmlLoader);
        actionEvent.consume();

    }

    @FXML
    private void onLogout(ActionEvent actionEvent) {
        Shift shift = agent.getShift();
        LocalDateTime currentTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        shift.setCurrentStatus(ShiftStatus.ACTIVE.name())
                .setUpdatedAt(currentTime);
        try {
            agent.getShiftRepository().shiftPauseResume(ShiftMapper.toDto(shift));
        } catch (SQLException e) {
            Logger.error("Error marking Shift status to Resume while maintenance logout : "+e.getMessage());
        }
        this.sceneManager.back();
        actionEvent.consume();
    }
}
