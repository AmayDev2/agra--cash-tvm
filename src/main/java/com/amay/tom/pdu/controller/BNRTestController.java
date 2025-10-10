package com.amay.tom.pdu.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.bnr.BNRIntegration;
import com.mei.bnr.exception.BnrException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class BNRTestController {

    private final SceneManager sceneManager;
    private final Agent agent;


    public  BNRTestController(SceneManager sceneManager, Agent agent){
        this.agent=agent;
        this.sceneManager=sceneManager;
    }

    @FXML
    void initialize(){


    }

    public void onCashInStart(ActionEvent actionEvent) throws BnrException {

        BNRIntegration.bnr.cashInStart();
        actionEvent.consume();
    }

    public void onCashInEnd(ActionEvent actionEvent) throws BnrException {

        BNRIntegration.bnr.cashInEnd();
        actionEvent.consume();
    }

    public void onRollback(ActionEvent actionEvent) throws BnrException {

        BNRIntegration.bnr.cashInRollBack();
        actionEvent.consume();
    }

    public void onCancel(ActionEvent actionEvent) throws BnrException {

        BNRIntegration.bnr.cancel();
        actionEvent.consume();
    }

    public void onBack(ActionEvent actionEvent) {

        sceneManager.back();
        actionEvent.consume();
    }
}
