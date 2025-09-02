package com.amay.tom.service.siftservice;


import com.amay.tom.ViewFactory;
import com.amay.tom.controller.ResumeShiftController;
import com.amay.tom.service.siftservice.InternalListener;
import com.amay.tom.service.siftservice.impl.ShiftServiceImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.network.monitorandcontrol.EquipmentType;

public class PopupContent {
    private Stage popupStage;
    private EquipmentType equipmentType;
    ResumeShiftController resumeShiftController;


    public PopupContent(ShiftServiceImpl shiftService) {

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


        FXMLLoader loader = ViewFactory.getResumeShift();
        InternalListener internalListener = null;
        resumeShiftController=new ResumeShiftController(shiftService);
        loader.setControllerFactory(x->resumeShiftController);
        try {
            Parent p = loader.load();

            Scene scene = new Scene(p, 700, 700);
            scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
//            scene.getStylesheets().add(getClass().getResource("/css/agra-theme.css").toExternalForm());
            scene.setFill(Color.TRANSPARENT); // Set the scene fill to transparent
            popupStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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

    public void setError(String message) {
        if(resumeShiftController!=null){
            resumeShiftController.setError(message);
        }

    }
}
