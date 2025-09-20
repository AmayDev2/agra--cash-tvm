package com.amay.tom.pdu.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.controller.LoginController;
import com.amay.tom.model.station.Station;
import com.amay.tom.pdu.controller.PDUHeaderController;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tom.service.devices.DeviceStatusListener;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.coin.service.HoppersRegistry;
import com.amay.tvm.controller.MaintenanceLoginController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.tinylog.Logger;

import java.io.IOException;


public class PDUController {
    @FXML
    private AnchorPane header;
    @FXML
    private GridPane mainGrid;
    @FXML
    private PDUHeaderController pduHeaderController;

    @FXML
    private StackPane stackPane;

    private SceneManager sceneManager;

    private Agent agent;

    public PDUController(){

    }

    @FXML
    public void initialize() {
        this.sceneManager = new SceneManager(stackPane);

        try{
            Logger.tag(LoggerTag.APP).debug("Loading Maintenance Screen");
            MaintenanceLoginController maintenanceLoginController= new MaintenanceLoginController(sceneManager);
            FXMLLoader fxmlLoader=ViewFactory.getWelcomeLogin();
            fxmlLoader.setControllerFactory((x)->maintenanceLoginController);
            sceneManager.addToRoot(fxmlLoader);
            Logger.tag(LoggerTag.APP).debug("Loaded Maintenance Screen !!!");
        }catch(Exception e){
            Logger.tag(LoggerTag.APP).error("Loading Maintenance Screen {}",e.getMessage());
        }

    }

    public void showView(FXMLLoader fxmlPath) {
//        sceneManager.switchTo(fxmlPath);
    }

    public void setAgent(Station station) {


    }


    public void setAgent(Agent agent) {
        this.agent=agent;



        try{
            Logger.tag(LoggerTag.APP).debug("Loading Maintenance Screen");
            MaintenanceLogin maintenanceLoginController= new MaintenanceLogin(this.agent,sceneManager);
            FXMLLoader fxmlLoader=ViewFactory.getMaintenanceLogin();
            fxmlLoader.setControllerFactory((x)->maintenanceLoginController);
            sceneManager.addToRoot(fxmlLoader);
            Logger.tag(LoggerTag.APP).debug("Loaded Maintenance Screen !!!");
        }catch(Exception e){
            Logger.tag(LoggerTag.APP).error("Loading Maintenance Screen {}",e.getMessage());
        }
    }


}


