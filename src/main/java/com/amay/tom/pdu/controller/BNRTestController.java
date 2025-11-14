package com.amay.tom.pdu.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.model.user.entity.UserPrivilege;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.bnr.BNRIntegration;
import com.amay.tvm.util.Page.FocusUtil;
import com.mei.bnr.exception.BnrException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class BNRTestController {

    private final SceneManager sceneManager;
    private final Agent agent;

    @FXML
    private Button cashInButton;
    @FXML
    private Button cashEndButton;
    @FXML
    private Button rolloutButton;

    @FXML
    private Button backButton;

    @FXML
    private Button cancelButton;



    public BNRTestController( Agent agent,SceneManager sceneManager){
        this.agent=agent;
        this.sceneManager=sceneManager;
    }

    @FXML
    public void initialize(){
        FocusUtil.configureFocus(cashEndButton,backButton);
    }

    public void onCashInStart(ActionEvent actionEvent) throws BnrException {

        BNRIntegration.bnr.cashInStart();
        BNRIntegration.bnr.cashIn(0);
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
