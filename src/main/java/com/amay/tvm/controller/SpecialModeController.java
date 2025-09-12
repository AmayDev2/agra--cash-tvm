package com.amay.tvm.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.StationMode;
import com.amay.tom.repository.StationData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class SpecialModeController {
    @FXML  private Label messageLabel;
    @FXML  private  Label label;
    @FXML  private ImageView imageView;
    private String header;
    private String message;
    private String path;

    @FXML void initialize() {
        imageView.setImage(new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm()));
        label.setText(header);
        messageLabel.setText(message);
    }

    public SpecialModeController(BorderPane borderPane, StackPane stackPane, Agent agent, StationData stationData, StationMode stationMode) {
        this.header = stationMode.getLabel();
        this.message = stationMode.getMessage();
        this.path = stationMode.getPath();

    }

    public void onCallHelp(ActionEvent actionEvent) {
    }

    public void onBackToHome(ActionEvent actionEvent) {
    }
}
