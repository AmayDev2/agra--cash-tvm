package com.amay.tom.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tom.repository.adjustment.AdjustedTicketRepository;
import com.amay.tom.repository.refund.RefundTicketRepository;
import com.amay.tom.repository.session.ShiftRepository;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tvm.backend.service.DataSync.DataSyncCountService;
import com.amay.tvm.util.DataSync.DataSyncInfo;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DataSync {
    @FXML
    private Label syncStatusLabel;
    @FXML
    private VBox root;
    @FXML
    private Label lastSyncTimestampCCU;
    @FXML
    private Label lastSyncTimestampSCU;
    @FXML
    private Label totalDataCount1;
    @FXML
    private Label totalDataCount2;
    @FXML
    private Label onlineDataCountCCU;
    @FXML
    private Label onlineDataCountSCU;
    @FXML
    private Label offlineDataCountCCU;
    @FXML
    private Label offlineDataCountSCU;
    private Agent agent;

    private TicketsRepository ticketsRepository;
    private ShiftRepository shiftRepository;
    private DataSyncCountService dataSyncCountService;
    private Timeline refreshTimeline;
    private SceneManager sceneManager;

    @FXML
    public void initialize() {
        startAutoRefresh();
        root.sceneProperty().addListener((obs,oldScene,newScene)->{
            if(newScene!=null){
                newScene.windowProperty().addListener((o,oldWindow,newWindow)->{
                    if(newWindow!=null){
                        newWindow.focusedProperty().addListener((fObs,wasFocused, isFocused)->{
                            if(isFocused){
                                startAutoRefresh();
                            }else {
                                stopAutoRefresh();
                            }
                        });
                    }
                });
            }
        });
    }


    public DataSync(Agent agent, SceneManager sceneManager){
        this.agent = agent;
        this.sceneManager = sceneManager;
        this.ticketsRepository= agent.getTicketsRepository();
        this.shiftRepository=agent.getShiftRepository();

        this.dataSyncCountService = new DataSyncCountService(
                ticketsRepository,shiftRepository
        );
    }

    private void stopAutoRefresh() {
        if(refreshTimeline!=null){
            refreshTimeline.stop();
            refreshTimeline=null;
        }
    }

    private void startAutoRefresh() {
        if(refreshTimeline!=null && refreshTimeline.getStatus()== Animation.Status.RUNNING)
            return;

        refreshData();

        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5),event->refreshData())
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    private void handleSyncDataWarningVisibility(){
        if(DataSyncInfo.offlineDataCountCCU==0 && DataSyncInfo.offlineDataCountSCU==0)
            syncStatusLabel.setVisible(false);
        else syncStatusLabel.setVisible(true);
    }


    private void refreshData(){
        dataSyncCountService.updateSyncCount();
        totalDataCount1.setText(String.valueOf(DataSyncInfo.totalDataCount));
        totalDataCount2.setText(String.valueOf(DataSyncInfo.totalDataCount));
        onlineDataCountCCU.setText(String.valueOf(DataSyncInfo.onlineDataCountCCU));
        offlineDataCountCCU.setText(String.valueOf(DataSyncInfo.offlineDataCountCCU));
        onlineDataCountSCU.setText(String.valueOf(DataSyncInfo.onlineDataCountSCU));
        offlineDataCountSCU.setText(String.valueOf(DataSyncInfo.offlineDataCountSCU));
        lastSyncTimestampCCU.setText(String.valueOf(DataSyncInfo.lastSyncCCU));
        lastSyncTimestampSCU.setText(String.valueOf(DataSyncInfo.lastSyncSCU));
        this.handleSyncDataWarningVisibility();
    }

    public void onBack(ActionEvent event) {
        sceneManager.back();
                event.consume();
    }
}