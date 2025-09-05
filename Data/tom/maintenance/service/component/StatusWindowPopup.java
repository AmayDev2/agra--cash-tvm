package com.amay.tom.maintenance.service.component;

import com.amay.tom.ViewFactory;
import com.amay.tom.controller.components.ConfermationWindow;
import com.amay.tom.controller.components.StatusWindow;
import com.amay.tom.maintenance.service.component.model.StatusWindowModel;
import com.amay.tom.threadpool.ThreadPool;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.network.monitorandcontrol.DeviceType;

public class StatusWindowPopup implements StatusWindowPopupListener {
    private Stage popupStage;
    private DeviceType deviceType;


    public StatusWindowPopup(MyOperation myOperation, StatusWindowModel statusWindowModel, ThreadPool threadPool) {

        popupStage = new Stage();
        Window window = findActiveWindow();
        if (window instanceof Stage) {
            popupStage.initOwner((Stage) window);
        } else {
            throw new IllegalStateException("Could not find active Stage.");
        }

        popupStage.initModality(Modality.WINDOW_MODAL);// base stage will become inactive
        popupStage.initStyle(StageStyle.UNDECORATED); // Remove window decorations
        popupStage.initStyle(StageStyle.TRANSPARENT); // Set the stage style to transparent


        FXMLLoader loader;
        if (null==statusWindowModel.getPermission()) {
            loader = ViewFactory.getPopupView();
            loader.setControllerFactory(x -> new StatusWindow(null, statusWindowModel, this, threadPool));
        } else {
            loader = ViewFactory.getConfermationWindow();
            loader.setControllerFactory(x -> new ConfermationWindow(myOperation, statusWindowModel, this));
        }


        try {
            Parent p = loader.load();
            Scene scene = new Scene(p, 400, 200);
//            scene.setFill(Color.TRANSPARENT); // Set the scene fill to transparent
            popupStage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error in loading popup"+e.getMessage());
            e.printStackTrace();
        }

    }


    @Override
    public void Close(){
        popupStage.close();
    }


    // Method to find the current active window
    private Window findActiveWindow() {
        return Stage.getWindows().stream()
                .filter(Window::isFocused)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active window found."));
    }

    public void show() {
        popupStage.showAndWait();
    }

}


