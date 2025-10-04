package com.amay.tom.pdu.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.bnr.BNRIntegration;
import javafx.event.ActionEvent;
import org.h2.util.Task;

public class MoneyManagementBnrLoadController{
    private final Agent agent;
    private final SceneManager sceneManager;

    public MoneyManagementBnrLoadController(Agent agent, SceneManager sceneManager) {
        this.agent = agent;
        this.sceneManager = sceneManager;

    }


    public void onBnrLoad(ActionEvent actionEvent) {
        agent.getThreadPool().getFixedThreadPool().submit(new Task() {
                                                              @Override
                                                              public void call() throws Exception {
                                                                  BNRIntegration.bnrLoad();
                                                              }
                                                          }
              );

        actionEvent.consume();
    }

    public void onCommit(ActionEvent actionEvent) {
        BNRIntegration.bnrLoadCommit();
        actionEvent.consume();
    }

    public void onBackBnr(ActionEvent actionEvent) {
        sceneManager.back();
        actionEvent.consume();
    }

    public void onRollback(ActionEvent actionEvent) {

        BNRIntegration.bnrLoadRollback();
        actionEvent.consume();
    }
}
