package com.amay.tom;


import com.amay.tom.controller.TestToolController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.tinylog.Logger;

import java.awt.*;
import java.io.IOException;


public class Main extends Application {



    @Override
    public void init() throws Exception {

    }

    @Override
    public void stop() throws Exception {

        super.stop();
    }

    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {

        var screens = javafx.stage.Screen.getScreens();


        try {

//            FXMLLoader fxmlLoader =new FXMLLoader(Main.class.getResource("testTool.fxml"));
            FXMLLoader fxmlLoader =new FXMLLoader(Main.class.getResource("/com/amay/tom/CscFxUI.fxml"));

//            fxmlLoader.setControllerFactory(param -> new TestToolController());
            Scene scene = new Scene(fxmlLoader.load(), screens.getFirst().getOutputScaleX(),  screens.getFirst().getOutputScaleY());
//            scene.getStylesheets().add(getClass().getResource("/com/amay/tom/tvm/css/theme.css").toExternalForm());

            // Add key event filter to prevent system keys
//            scene.addEventFilter(KeyEvent.ANY, event -> {
//                if (event.getCode() == KeyCode.WINDOWS ||
//                        event.getCode() == KeyCode.COMMAND ||
//                        event.getCode() == KeyCode.ALT ||
//                        event.getCode() == KeyCode.TAB ||
//                        event.getCode() == KeyCode.ESCAPE ||
//                        (event.isAltDown() && event.getCode() == KeyCode.F4)) {
//                    event.consume();
//                    Platform.runLater(() -> {
//                        stage.setFullScreen(true);
//                        stage.setAlwaysOnTop(true);
//                        stage.toFront();
//                    });
//                }
//            });
//
//            // Prevent window from being minimized
//            stage.iconifiedProperty().addListener((obs, wasIconified, isNowIconified) -> {
//                if (isNowIconified) {
//                    Platform.runLater(() -> {
//                        stage.setIconified(false);
//                        stage.setFullScreen(true);
//                        stage.setAlwaysOnTop(true);
//                        stage.toFront();
//                    });
//                }
//            });

            // Keep window always on top
            stage.setAlwaysOnTop(true);

            // Set window properties
            stage.setMinHeight(768);
            stage.setMinWidth(1024);
            stage.setMaxHeight(768);
            stage.setMaxWidth(1024);
//            stage.setFullScreen(true);
//            stage.setFullScreenExitHint(null);
//            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.setTitle("TVM Application");
            stage.setScene(scene);


//            // Add window closing event handler to prevent Alt+F4
//            stage.setOnCloseRequest(event -> {
//                event.consume();
//                Platform.runLater(() -> {
//                    stage.setFullScreen(true);
//                    stage.setAlwaysOnTop(true);
//                    stage.toFront();
//                });
//            });
//
//            // Add window state change listener
//            stage.setOnShown(event -> {
//                Platform.runLater(() -> {
//                    stage.setFullScreen(true);
//                    stage.setAlwaysOnTop(true);
//                    stage.toFront();
//                });
//            });
//
//            // Add focus listener to maintain full screen
//            stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
//                if (isNowFocused) {
//                    Platform.runLater(() -> {
//                        stage.setFullScreen(true);
//                        stage.setAlwaysOnTop(true);
//                        stage.toFront();
//                    });
//                }
//            });
//
//            // Add window state change listener
//            stage.setOnHiding(event -> {
//                Platform.runLater(() -> {
//                    stage.show();
//                    stage.setFullScreen(true);
//                    stage.setAlwaysOnTop(true);
//                    stage.toFront();
//                });
//            });
//
//            // Handle window state changes
//            stage.iconifiedProperty().addListener((obs, wasIconified, isNowIconified) -> {
//                if (isNowIconified) {
//                    Platform.runLater(() -> {
//                        stage.setIconified(false);
//                        stage.setFullScreen(true);
//                        stage.setAlwaysOnTop(true);
//                        stage.toFront();
//                    });
//                }
//            });
//
//            // Add window state change listener for window state changes
//            stage.setOnShowing(event -> {
//                Platform.runLater(() -> {
//                    stage.setFullScreen(true);
//                    stage.setAlwaysOnTop(true);
//                    stage.toFront();
//                });
//            });
//
//            // Add window state change listener for window state changes
//            stage.setOnHidden(event -> {
//                Platform.runLater(() -> {
//                    stage.show();
//                    stage.setFullScreen(true);
//                    stage.setAlwaysOnTop(true);
//                    stage.toFront();
//                });
//            });

            stage.show();


        }catch (Exception e){
            Logger.error("Error in loading main scene: {}", e);
        }

    }





    public static void main(String[] args) throws IOException {
//        EnvFile.loadEnv();
        launch();

    }

}