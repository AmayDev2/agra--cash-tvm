package com.amay.tvm.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.backend.enums.LoggerTag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import org.tinylog.Logger;

public class MaintenanceLoginController {

    private final  SceneManager sceneManager;

    public MaintenanceLoginController(SceneManager sceneManager){
        this.sceneManager=sceneManager;
    }

    @FXML private
    void onClickLogin(ActionEvent actionEvent) {
        Logger.tag(LoggerTag.APP).debug("Loading Hoppers Screen");
        FXMLLoader fxmlLoader=ViewFactory.getHopper();
        CoinRagistoryPageController coinRagistoryPageController=new CoinRagistoryPageController(sceneManager);
        fxmlLoader.setControllerFactory((x)->coinRagistoryPageController);
        this.sceneManager.addToScene(fxmlLoader);
        Logger.tag(LoggerTag.APP).debug("Loaded Hoppers Screen !!!");
        actionEvent.consume();
    }


}
