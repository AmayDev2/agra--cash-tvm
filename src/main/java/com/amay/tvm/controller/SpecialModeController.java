package com.amay.tvm.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.repository.StationData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class SpecialModeController {
    @FXML  private Label messageLabel;
    @FXML  private  Label label;
    @FXML  private ImageView imageView;
    private String header;
    private String message;

    @FXML void initialize() {
        imageView.setImage(new Image("file:src/main/resources/images/emergency.png"));
        label.setText(header);
    }

    public SpecialModeController(BorderPane borderPane, StackPane stackPane, Agent agent, StationData stationData,String header) {
        this.header = header;
    }

    public void onCallHelp(ActionEvent actionEvent) {
    }

    public void onBackToHome(ActionEvent actionEvent) {
    }
}
