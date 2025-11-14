package com.amay.tom.pdu.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.model.user.entity.UserPrivilege;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.bnr.BNRIntegration;
import com.amay.tvm.util.Page.FocusUtil;
import com.jxfs.events.JxfsException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.checkerframework.checker.units.qual.A;
import org.tinylog.Logger;

public class MoneyManagementBnrController {
    private final Agent agent;
    private final SceneManager sceneManager;
    @FXML
    private GridPane root;
    @FXML
    private Button bnrLoadButton;
    @FXML
    private Button bnrUnloadButton;
    @FXML
    private Button bnrRebootButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button backButton;


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
    @FXML
    private void initialize(){
        UserPrivilege privilege=agent.getUserPrivilege();
        FocusUtil.configureFocus(bnrLoadButton,backButton);
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
        actionEvent.consume();
    }

    public void onReset(ActionEvent actionEvent) {
        try {
            BNRIntegration.resetBnr();
        } catch (JxfsException e) {
            Logger.tag(LoggerTag.APP).error("Error resetting BNR: {}", e.getMessage());
        }
        actionEvent.consume();
    }

    public void onBack(ActionEvent actionEvent) {
        sceneManager.back();
        actionEvent.consume();
    }
}
