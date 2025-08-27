package com.amay.tom.controllerInterface.controllerInt;

import com.amay.tom.ViewFactory;
import com.amay.tom.controller.Controller;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public enum ControllerAdapter {

    INSTANCE;

    private BorderPane borderPane;

    public void setCenterAnchorPane(BorderPane borderPane) {
        INSTANCE.borderPane = borderPane;

    }


    public void setChildInCenterAnchorPane(FXMLLoader fxmlLoader, Controller controller) throws IOException {

        borderPane.setCenter(fxmlLoader.load());

    }

    public void setChildInCenterAnchorPane(Parent parent) throws IOException {

        Platform.runLater(()->borderPane.setCenter(parent));
    }


    public void addWaitForPayment() {
//        Platform.runLater(() -> {
//            try {
//                StackPane root = (StackPane) borderPane.getScene().getRoot();
//                Image image = new Image("E:\\Amay Technosystems\\Tom\\src\\main\\resources\\icons\\assets_tom\\loading.png"); // replace with your image path
//                ImageView imageView = new ImageView(image);
//                root.getChildren().add(imageView);
//            } catch (Exception e) {
//                throw new RuntimeException("Error in setting waitForPayment "+e.getMessage());
//            }
//        });
    }





    public void setLogout() throws IOException {
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = ViewFactory.getLogin();
                Stage stage=(Stage)borderPane.getScene().getWindow();
                stage.setScene(new Scene(fxmlLoader.load()));
            } catch (IOException e) {
                throw new RuntimeException("Error in setting logout");
            }
        });

    }
}
