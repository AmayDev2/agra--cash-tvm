package com.amay.tom.controller;

import com.amay.tom.service.api.impl.SCUConnection;
import com.amay.tom.service.initialize.ITomInitialize;
import com.amay.tom.service.initialize.impl.TomInitialize;
import com.amay.tom.service.tom.IApplicationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import java.io.IOException;
import java.util.function.BiConsumer;

public class TomInitializeViewController {


    @FXML
    private ProgressIndicator progressIndicator;
    private ITomInitialize tomInitialize;
    private Scene currentScene;

//    @FXML
//    private ProgressBar progressIndicator;

    @FXML
    private Label progressInfo;

    private final IApplicationService applicationService;

    public TomInitializeViewController(IApplicationService applicationService) {
        this.applicationService = applicationService;
    }


    @FXML
    private void initialize() {
        this.tomInitialize = new TomInitialize(this, SCUConnection.INSTANCE, applicationService);
        progressIndicator.setProgress(0.2);
        progressInfo.setText("Initializing...");
        this.tomInitialize.deviceInitialization(progressIndicator.getScene());
    }


    public void updateProgress(double progress, String info) {
        Platform.runLater(() -> {
            progressIndicator.setProgress(progress);
            progressInfo.setText(info);
        });
    }

    private BiConsumer<Double, String> updateProgress1 = (progress, info) -> {
        progressIndicator.setProgress(progress);
        progressInfo.setText(info);
    };

    public void onSuccessfulInitialization(Scene scane, FXMLLoader fxmlLoader) throws IOException {
        if(scane != null) {
            currentScene = scane;
        }else{
            this.currentScene = progressIndicator.getScene();
        }
        currentScene.setRoot(fxmlLoader.load());
    }
}
