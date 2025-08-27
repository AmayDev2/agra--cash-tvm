package com.amay.tom.pdu.controller.service;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class SceneManager {
    private final StackPane container;

    public SceneManager(StackPane container) {
        this.container = container;
    }

    public void switchTo(FXMLLoader fxmlLoader) {
            Platform.runLater(() -> {
                try {
                    container.getChildren().setAll((Node) fxmlLoader.load());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
    }
}

