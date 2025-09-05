package com.amay.tom.pdu.controller;

import com.amay.tom.config.SystemConfig;
import com.amay.tom.pdu.controller.service.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PDUHeaderController {

    @FXML private Label labelStationName;
    @FXML private Label labelTime;
    @FXML private Label labelDate;
    @FXML private StackPane stackPane;

    private SceneManager sceneManager;



    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM, yyyy");


    private void startTime(){

        Platform.runLater(() -> {
            Timeline clock = new Timeline(
                    new KeyFrame(Duration.ZERO, e -> {
                        LocalDateTime now = LocalDateTime.now();
                        labelTime.setText(now.format(TIME_FORMATTER).toUpperCase(Locale.ROOT));
                        labelDate.setText(now.format(DATE_FORMATTER));
                    }),
                    new KeyFrame(Duration.seconds(1))
            );
            clock.setCycleCount(Timeline.INDEFINITE);
            clock.play();
        });
    }

    @FXML
    public void initialize() {
        this.sceneManager = new SceneManager(stackPane);
        this.startTime();
        labelStationName.setText(SystemConfig.getInstance().getCurrentStation().getStationName());
    }



}
