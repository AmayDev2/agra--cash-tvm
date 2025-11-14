package com.amay.tvm.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.backend.model.MaintenanceLog;
import com.amay.tvm.backend.repository.MaintenanceRepository;
import com.amay.tvm.util.Page.FocusUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Collections;
import java.util.List;

public class MaintenanceAlarmController {
    @FXML
    private Button refreshButton;
    @FXML
    private Button backButton;
    @FXML
    private TableView<MaintenanceLog> logTable;

    @FXML
    private TableColumn<MaintenanceLog, String> deviceCol;
    @FXML
    private TableColumn<MaintenanceLog, String> statusCol;
    @FXML
    private TableColumn<MaintenanceLog, String> timestampCol;

    private MaintenanceRepository maintenanceRepository;
    private Agent agent;
    private SceneManager sceneManager;


    public MaintenanceAlarmController(Agent agent, SceneManager sceneManager) {
            this.agent=agent;
            this.sceneManager=sceneManager;
        this.maintenanceRepository = agent.getMaintenanceRepository();
    }


    @FXML
    private void initialize() {
        logTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        deviceCol.setCellValueFactory(new PropertyValueFactory<>("event"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        FocusUtil.configureFocus(refreshButton,backButton);

        Platform.runLater(this::loadLogs);
    }


    @FXML
    private void loadLogs() {
        List<MaintenanceLog> logs = maintenanceRepository.getAllLogs();
        Collections.reverse(logs);
        logTable.setItems(FXCollections.observableArrayList(logs));
    }

    @FXML
    private void onRefresh(ActionEvent event) {
        loadLogs();
    }

    public void onBack(ActionEvent event) {
        sceneManager.back();
        event.consume();
    }
}
