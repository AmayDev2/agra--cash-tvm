package com.amay.tom.pdu.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.model.station.Station;
import com.amay.tom.pdu.controller.PDUHeaderController;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.coin.service.HoppersRegistry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

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

    @FXML
    public void initialize() {
        this.sceneManager = new SceneManager(stackPane);
        try {
            HoppersRegistry.INSTANCE.setHoppers(5,10,10,     0,0,0,null);
            FXMLLoader loader=ViewFactory.getHopper();
            stackPane.getChildren().removeLast();
            stackPane.getChildren().add(loader.load());
        } catch (IOException e) {
            e.getMessage();
        }

    }

    public void showView(FXMLLoader fxmlPath) {
        sceneManager.switchTo(fxmlPath);
    }

    public void setStation(Station station) {

    }
}
