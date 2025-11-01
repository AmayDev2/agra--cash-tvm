package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.controller.components.StatusBottomBarView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Maintenance implements Initializable{

    @FXML
    private Text equipId;
    @FXML
    private Text stationId;
    @FXML
    private Text shiftId;
    @FXML
    private Text timeLabel;
    @FXML
    private Text dateLabel;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Button moduleTest;
    @FXML
    private Button version;
    @FXML
    private Button alarm;
    @FXML
    private Button importExport;
    @FXML
    private Button Shutdown;



    @FXML
    private Button statusButton;

    private Button activeButton;

    private final Agent agent;

    public Maintenance(Agent agent) {
        this.agent = agent;
    }

    @FXML
    private void initialize() {
      this.setFooter();
        this.setCenter();
        this.moduleTest.fire();
        this.updateDateTime();
        this.setEquipmentDetails();
     }

    private void setEquipmentDetails() {
        equipId.setText(equipId.getText().split("-")[0]+agent.getSystemConfig().getCurrentEquipment().getEquipmentId());
        stationId.setText(stationId.getText().split("-")[0]+agent.getSystemConfig().getCurrentStation().getStationId());
        shiftId.setText(shiftId.getText().split("-")[0]+agent.getShift().getShiftId().split("-")[0]);
    }


    private void setActiveButton(Button button, FXMLLoader loader)  {
        if(activeButton == button)return;
        try {
            if (activeButton != null) activeButton.getStyleClass().remove("active-button");
            button.getStyleClass().add("active-button");
            borderPane.setCenter(loader.load());
            activeButton = button;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setCenter(){
        moduleTest.setOnAction(event ->{
            FXMLLoader fxmlLoader=ViewFactory.getMaintenanceModuleTest();
            fxmlLoader.setControllerFactory(c -> new MaintenanceModuleTest(agent.getPeripheralMonitor(),agent));
            setActiveButton(moduleTest, fxmlLoader);});

        version.setOnAction(event -> {
            FXMLLoader fxmlLoader=ViewFactory.getMaintenanceVersion();
            fxmlLoader.setControllerFactory(c -> new MaintenanceVersion());
            setActiveButton(version, fxmlLoader);
                });

        alarm.setOnAction(event -> {
            FXMLLoader fxmlLoader=ViewFactory.getMaintenanceAlarm();
            fxmlLoader.setControllerFactory(c -> new MaintenanceAlarm());
            setActiveButton(alarm, fxmlLoader);
                });

        importExport.setOnAction(event -> {
            FXMLLoader loader=ViewFactory.getMaintenanceImportExport();
            loader.setControllerFactory(c -> new MaintenanceImportExport());
            setActiveButton(importExport, loader);
        });

        Shutdown.setOnAction(event -> {
            FXMLLoader fxmlLoader=ViewFactory.getMaintenanceApplicationControl();
            fxmlLoader.setControllerFactory(c -> new MaintenanceApplicationControl(agent.getApplicationService(),agent));
            setActiveButton(Shutdown, fxmlLoader);
        });
    }


    //Update time and date
    private void updateDateTime() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            Platform.runLater(() -> {
                timeLabel.setText(timeFormatter.format(now));
                dateLabel.setText(dateFormatter.format(now));
            });
        }));
        timeline.setCycleCount(Animation.INDEFINITE); // Run indefinitely
        timeline.play(); // Start the timeline
    }



    private void setFooter() {
        try {
            FXMLLoader fxmlLoader = ViewFactory.getBottomNav();
            fxmlLoader.setControllerFactory(c -> new StatusBottomBarView(this.agent.getPeripheralMonitor(), agent.getVersions(),agent.getMasterConfigInfo()));
            HBox bottomNav = fxmlLoader.load();
            borderPane.setBottom(bottomNav);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initialize();
    }
}
