package com.amay.tom.pdu.controller;

import com.amay.tom.model.station.Station;
import com.amay.tom.pdu.controller.service.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;


public class PDUController {
    @FXML
    private AnchorPane header;
    @FXML
    private GridPane mainGrid;
@FXML
    private PDUHeaderController pduHeaderController;
//        @FXML private Label labelDate;
    @FXML
    private StackPane stackPane;

    private SceneManager sceneManager;

    @FXML
    public void initialize() {
        this.sceneManager = new SceneManager(stackPane);

    }

    public void showView(FXMLLoader fxmlPath) {
        sceneManager.switchTo(fxmlPath);
    }

    public void setStation(Station station) {
        Label titleLabel = (Label) header.lookup("#labelStationName"); // fx:id in header.fxml
        if (titleLabel != null) {
            Platform.runLater(()->titleLabel.setText(station.getStationName()));
        }
    }
}
