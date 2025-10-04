package com.amay.tom.pdu.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.bnr.BNRIntegration;
import com.jxfs.events.JxfsException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import org.checkerframework.checker.units.qual.A;
import org.tinylog.Logger;

public class MoneyManagementBnrController {
    private final Agent agent;
    private final SceneManager sceneManager;
    public MoneyManagementBnrController(Agent agent, SceneManager sceneManager) {
        this.agent = agent;
        this.sceneManager = sceneManager;
    }

    public void onBnrLoad(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader= ViewFactory.getMoneyManagementBnrLoad();
        MoneyManagementBnrLoadController controller=new MoneyManagementBnrLoadController(this.agent,this.sceneManager);
        fxmlLoader.setControllerFactory((x)->controller);
        this.sceneManager.addToScene(fxmlLoader);
        actionEvent.consume();
    }

    public void onBnrUnload(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader= ViewFactory.getMoneyManagementBnrUnload();
        MoneyManagementBnrUnloadController controller=new MoneyManagementBnrUnloadController(this.agent,this.sceneManager);
        fxmlLoader.setControllerFactory((x)->controller);
        this.sceneManager.addToScene(fxmlLoader);
        actionEvent.consume();
    }

    public void onReceipt(ActionEvent actionEvent) {
        BNRIntegration.reBootBnr();
    }

    public void onReset(ActionEvent actionEvent) {
        try {
            BNRIntegration.resetBnr();
        } catch (JxfsException e) {
            Logger.tag(LoggerTag.APP).error("Error resetting BNR: {}", e.getMessage());
        }
    }

    public void onBack(ActionEvent actionEvent) {
        sceneManager.back();
        actionEvent.consume();
    }
}
