package com.amay.tvm.util;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

public enum Snackbar {
    INSTANCE;
    private HBox snackbar;

    public Runnable showSnackbar(StackPane container, String message, boolean isSuccess, double timeout) {
        container.getChildren().remove(snackbar);

//        var close = new FontIcon(Material2AL.CLOSE);
//
//        var successIcon = new FontIcon(Material2MZ.THUMB_UP);
//        successIcon.getStyleClass().add(Styles.SUCCESS);


        // Create snackbar
        snackbar = new HBox(10); // Add spacing for icon and text
        snackbar.setAlignment(Pos.BOTTOM_LEFT);
        snackbar.setMaxHeight(30);
        snackbar.setPrefWidth(100);
        snackbar.setMaxWidth(300);
        snackbar.setSpacing(10);
        snackbar.setStyle("-fx-background-color: " +  (isSuccess ? "#4CAF50" : "#F44336") + "; " +
                "-fx-background-radius: 5; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0.5, 0, 2); " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10 10; " +
                "-fx-alignment: center;");

        // Snackbar text
        Text snackbarText = new Text(message);
        snackbarText.setFill(Color.WHITE);

        // Add text to snackbar
        snackbar.getChildren().add(snackbarText);
//        if(isSuccess)snackbar.getChildren().add(successIcon);
//        else snackbar.getChildren().add(close);

        // Add snackbar to container
        container.getChildren().add(snackbar);

        // Position the snackbar in the bottom-left corner
        StackPane.setAlignment(snackbar, Pos.BOTTOM_LEFT);
        snackbar.setTranslateX(20); // Offset from the left edge
        snackbar.setTranslateY(-20); // Offset from the bottom edge

        // Animate snackbar
        TranslateTransition moveLeftToRight = new TranslateTransition(Duration.millis(500), snackbar);
        moveLeftToRight.setFromX(-300); // Start off-screen on the left
        moveLeftToRight.setToX(50);     // Stop at the desired position
        moveLeftToRight.setInterpolator(Interpolator.EASE_BOTH);

        // Color transition effect
        FillTransition colorTransition = new FillTransition(Duration.millis(500), snackbarText,
                Color.WHITE, isSuccess ? Color.LIGHTGREEN : Color.LIGHTCORAL);
        colorTransition.setCycleCount(2);
        colorTransition.setAutoReverse(true);

        if(timeout==0)timeout=2;
        // Pause and remove snackbar
        PauseTransition pause = new PauseTransition(Duration.seconds(timeout));

        pause.setOnFinished(event -> {container.getChildren().remove(snackbar);});

        // Play animations sequentially
        SequentialTransition sequentialTransition = new SequentialTransition(
                moveLeftToRight, colorTransition, pause);

        // Add Mouse Click Event Handler to Close Icon
//        close.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//            sequentialTransition.stop(); // Stop the entire animation sequence
//            container.getChildren().remove(snackbar); // Remove the snackbar immediately
//        });

        sequentialTransition.play();
        return null;
    }
}

