package com.amay.tvm.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.grpc.monotoring.AppCloseCommand;
import com.amay.tom.grpc.monotoring.RebootCommand;
import com.amay.tom.grpc.monotoring.ShutdownCommand;
import com.amay.tom.maintenance.service.component.MyOperation;
import com.amay.tom.maintenance.service.component.StatusWindowPopup;
import com.amay.tom.maintenance.service.component.model.StatusWindowModel;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tom.service.events.Remote;
import com.amay.tom.service.tom.IApplicationService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.tinylog.Logger;

import java.io.IOException;

public class ApplicationController {

    private SceneManager sceneManager;
    private final Agent agent;



    public ApplicationController(Agent agent, SceneManager sceneManager) {
        this.agent=agent;
        this.sceneManager=sceneManager;
        agent.getApplicationService();
    }


    @FXML
    private void onSystemShutdown(ActionEvent event) {
        MyOperation myOperation = () -> {
            new Remote(new ShutdownCommand(  agent.getApplicationService())).pressButton();
            return true;
        };
        StatusWindowModel statusWindowModel=new StatusWindowModel();
        statusWindowModel.setPermission("Do you want to shutdown?");
        new StatusWindowPopup(myOperation, statusWindowModel, agent.getThreadPool()).show();
//
    }

    @FXML
    private void onSystemRestart(ActionEvent actionEvent) {
        MyOperation myOperation = () -> {
            new Remote(new RebootCommand(agent.getApplicationService())).pressButton();
            return true;
        };
        StatusWindowModel statusWindowModel=new StatusWindowModel();
        statusWindowModel.setPermission("Do you want to Restart?");
        new StatusWindowPopup(myOperation, statusWindowModel, agent.getThreadPool()).show();
//
    }



    @FXML
    private void onApplicationRestart(ActionEvent actionEvent) {


    }
    @FXML
    private void onApplicationClose(ActionEvent actionEvent) {
//        Logger.tag(LoggerTags.MAINTENANCE).info("Close operation clicked");
        MyOperation myOperation = () -> {
            new Remote(new AppCloseCommand(agent.getApplicationService())).pressButton();
            return true;
        };
        StatusWindowModel statusWindowModel=new StatusWindowModel();
        statusWindowModel.setPermission("Do you want to close?");
        new StatusWindowPopup(myOperation, statusWindowModel, agent.getThreadPool()).show();
    }

    public void onBack(ActionEvent event) {
        this.sceneManager.back();
        event.consume();
    }
}
