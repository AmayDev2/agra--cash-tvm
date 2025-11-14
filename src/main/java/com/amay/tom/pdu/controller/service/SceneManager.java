package com.amay.tom.pdu.controller.service;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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
                    container.getChildren().set(container.getChildren().size()-1, fxmlLoader.load());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public void addToRoot(FXMLLoader fxmlLoader) {
        Platform.runLater(() -> {
            try {
               container.getChildren().clear();
               container.getChildren().add(container.getChildren().size(), fxmlLoader.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void addToScene(FXMLLoader fxmlLoader) {
        Parent root;

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Platform.runLater(() -> {
            container.getChildren().add(root);


            Node firstButton = root.lookup(".button");
            if (firstButton != null) {
                firstButton.requestFocus();
            }
        });
    }


    public void addWaiting(Node node) {
        Platform.runLater(() -> {
            container.getChildren().add(node);
        });
    }



    public void back() {
        Platform.runLater(() -> {
            int size = container.getChildren().size();
            if (size <= 1) return;

            container.getChildren().removeLast();

            Parent previousPage = (Parent) container.getChildren().getLast();

            Node firstButton = previousPage.lookup(".button");
            if (firstButton != null) {
                firstButton.requestFocus();
            }
        });
    }

}

