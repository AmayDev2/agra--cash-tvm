package com.amay.tom.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.grpc.monotoring.AppCloseCommand;
import com.amay.tom.grpc.monotoring.RebootCommand;
import com.amay.tom.grpc.monotoring.ShutdownCommand;
import com.amay.tom.maintenance.service.component.MyOperation;
import com.amay.tom.maintenance.service.component.StatusWindowPopup;
import com.amay.tom.maintenance.service.component.model.StatusWindowModel;
import com.amay.tom.service.events.Remote;
import com.amay.tom.service.tom.IApplicationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MaintenanceApplicationControl {

    private final IApplicationService applicationService;
    private final Agent agent;
    public MaintenanceApplicationControl(IApplicationService applicationService, Agent agent) {
        this.applicationService = applicationService;
        this.agent = agent;
    }

    @FXML
    private void onShutdown(ActionEvent actionEvent) {
        MyOperation myOperation = () -> {
            new Remote(new ShutdownCommand(applicationService)).pressButton();
            return true;
        };
        StatusWindowModel statusWindowModel=new StatusWindowModel();
        statusWindowModel.setPermission("Do you want to shutdown?");
        new StatusWindowPopup(myOperation, statusWindowModel, agent.getThreadPool()).show();
    }

    @FXML
    private void onClose(ActionEvent actionEvent) {
        MyOperation myOperation = () -> {
            new Remote(new AppCloseCommand(applicationService)).pressButton();
            return true;
        };
        StatusWindowModel statusWindowModel=new StatusWindowModel();
        statusWindowModel.setPermission("Do you want to close?");
        new StatusWindowPopup(myOperation, statusWindowModel, agent.getThreadPool()).show();
    }

    @FXML
    private void onReboot(ActionEvent actionEvent) {
        MyOperation myOperation = () -> {
            new Remote(new RebootCommand(applicationService)).pressButton();
            return true;
        };
        StatusWindowModel statusWindowModel=new StatusWindowModel();
        statusWindowModel.setPermission("Do you want to reboot?");
        new StatusWindowPopup(myOperation, statusWindowModel, agent.getThreadPool()).show();
    }

    @FXML
    private void onRestart(ActionEvent actionEvent) {
        //TODO: Implement application restart
    }
}



