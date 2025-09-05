package com.amay.tom.pdu.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.pdu.controller.service.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PDUController {
    @FXML private Label labelDate;
    @FXML private StackPane stackPane;

    private SceneManager sceneManager;

    @FXML
    public void initialize() {
        this.sceneManager = new SceneManager(stackPane);

    }

    public void showView(FXMLLoader fxmlPath) {
        sceneManager.switchTo(fxmlPath);
    }
}
