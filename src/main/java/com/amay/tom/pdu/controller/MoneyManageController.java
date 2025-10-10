package com.amay.tom.pdu.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.controller.CoinRagistoryPageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class MoneyManageController {
    private final Agent agent;
    private final SceneManager sceneManager;
    public MoneyManageController(Agent agent, SceneManager sceneManager) {
        this.agent = agent;
        this.sceneManager = sceneManager;

    }

    @FXML
    private void onCoin(ActionEvent actionEvent) {

        FXMLLoader fxmlLoader= ViewFactory.getMoneyManagementCoin();
        CoinRagistoryPageController controller=new CoinRagistoryPageController(this.sceneManager,this.agent);
        fxmlLoader.setControllerFactory((x)->controller);
        this.sceneManager.addToScene(fxmlLoader);
        actionEvent.consume();

    }

    @FXML
    private  void onBnr(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader= ViewFactory.getMoneyManagementBnr();
        MoneyManagementBnrController controller=new MoneyManagementBnrController(this.agent,this.sceneManager);
        fxmlLoader.setControllerFactory((x)->controller);
        this.sceneManager.addToScene(fxmlLoader);
        actionEvent.consume();
    }

    @FXML
    private  void onBack(ActionEvent actionEvent) {
        this.sceneManager.back();
        actionEvent.consume();
    }
}
