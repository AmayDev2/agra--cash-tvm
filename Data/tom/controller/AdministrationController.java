package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.controllerInterface.controllerInt.ControllerAdapter;
import com.amay.tom.model.equipment.entity.EquipmentPrivilege;
import com.amay.tom.model.user.entity.UserPrivilege;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class AdministrationController {

    private Agent agent=null;
    @FXML
    public Button paidFreeTicket;

//    @FXML
//    private Button paidFreeTicket;

    @FXML
    private Button pauseEOShift;

    private UserPrivilege userPrivilege;
    private EquipmentPrivilege equipmentPrivilege;
    private BorderPane borderPane;

    public AdministrationController(UserPrivilege userPrivilege, EquipmentPrivilege equipmentPrivilege, BorderPane borderPane) {
        this.equipmentPrivilege=equipmentPrivilege;
        this.userPrivilege=userPrivilege;
        this.borderPane=borderPane;
    }

    public AdministrationController(Agent agent,BorderPane borderPane) {
        this(agent.getUserPrivilege(), agent.getEquipmentPrivilege(), borderPane);
        this.agent=agent;
    }

    @FXML
    void initialize() {

    }

    //    @FXML
//    private AnchorPane anchorPane;
    public void onClickPaidFreeTicket(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader=ViewFactory.getPaidFreeTicket();
        fxmlLoader.setControllerFactory(x->new PaidFreeTicket(this.agent,borderPane));

        Platform.runLater(()->{
            Pane pane = null;
            try {
//                anchorPane.getChildren().clear();
//                anchorPane.getChildren().add(fxmlLoader.load());
                ControllerAdapter.INSTANCE.setChildInCenterAnchorPane(fxmlLoader, null);
//                pane = fxmlLoader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            paidFreeTicket.getScene().setRoot(pane);
        });


    }



    public void onClickPauseEOShift(ActionEvent actionEvent) {

        FXMLLoader fxmlLoader=ViewFactory.getPauseEosSelection();
        fxmlLoader.setControllerFactory(x->new PauseEosSelectionViewController(this.agent.getInternalListener(),this.agent.getUserAuth()));

        Platform.runLater(()->{
            try {
//                anchorPane.getChildren().clear();
//                anchorPane.getChildren().add(fxmlLoader.load());
                ControllerAdapter.INSTANCE.setChildInCenterAnchorPane(fxmlLoader, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }


    public void onClickStockManagement(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader=ViewFactory.getStockManagement();
        fxmlLoader.setControllerFactory(x->new StocksAddViewController(this.agent));

        Platform.runLater(()->{
            try {
//                anchorPane.getChildren().clear();
//                anchorPane.getChildren().add(fxmlLoader.load());
                ControllerAdapter.INSTANCE.setChildInCenterAnchorPane(fxmlLoader, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        actionEvent.consume();
    }
}
