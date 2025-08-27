package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.enums.Language;
import com.amay.tom.model.MetroTicket;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.station.Station;
import com.amay.tom.service.controller.MainUIService;
import com.amay.tom.service.controller.impl.ImplMainUiService;
import com.amay.tom.utils.helper.Helper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tinylog.Logger;


public class MainController {

    @FXML
    private Button administration;

    @FXML
    private Button analysisButton;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Label cesr;

    @FXML
    private Button cscOperations;

    @FXML
    private Label csn;

    @FXML
    private MenuButton destinationMenu;

    @FXML
    private ToggleButton group;

    @FXML
    private Button internetStatus;

    @FXML
    private ImageView logo;

    @FXML
    private Button logout;

    @FXML
    private ColumnConstraints navigationGrid;

    @FXML
    private Button ncmc;

    @FXML
    private Text ncmcAvailable;

    @FXML
    private Text ncmcSale;

    @FXML
    private Label ncmcStock;

    @FXML
    private Button pay;

    @FXML
    private Button pay1;

    @FXML
    private Button printerStatus;

    @FXML
    private Text qrAviable;

    @FXML
    private Text qrSale;

    @FXML
    private Label qrStock;

    @FXML
    private Button qrticket;

    @FXML
    private ToggleButton rjt;

    @FXML
    private Button scannerStatus;

    @FXML
    private ToggleButton sjt;

    @FXML
    private Button tCountDesc;

    @FXML
    private Button tCountInc;

    @FXML
    private Label tCountText;

    @FXML
    private VBox ticketGen;

    @FXML
    private Label userId;
//
    @FXML
    private Label welcomeText;
///
    private  MainUIService mainUIService;

    public MainController() {
        mainUIService=new ImplMainUiService();
    }
    private Station selectedDestination;


    @FXML
     void initialize() {
//        mainUIService=new ImplMainUiService();



//        updateStock();
//        setStockManagement();
//
//
//
//        userId.setText(ShiftHeader.getInstance().getOperatorId());
//        ToggleGroup toggleGroup = new ToggleGroup();
//        csn.setText(currentStation.getStationName());
//        cesr.setText(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial());
          logo.setImage(new Image("file:amaylogo.png"));
//        tCountText.setText(String.valueOf(selectedNoOfPassenger));
            Station[] st=mainUIService.getStationDetails();
            for (Station station:st){
                System.out.println(station.getStationName());
            }
//        // Populate destination menu
        for (Station station : mainUIService.getStationDetails()) {
            MenuItem mi = new MenuItem(station.getStationName());
            mi.setId(station.getStationId());
            mi.setUserData(station);
            mi.setOnAction(a -> {
                destinationMenu.setText(((MenuItem) a.getSource()).getText());
                selectedDestination = (Station) ((MenuItem) a.getSource()).getUserData();
            });
            destinationMenu.getItems().add(mi);
        }


    }

























    @FXML
    private void qrTicket(ActionEvent actionEvent) {
    }

    @FXML
    public void onClickNCMC(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = ViewFactory.getServiceUnavailable();
            borderPane.setCenter(fxmlLoader.load());
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception as per your requirement
        } finally {
            actionEvent.consume();
        }
    }

    @FXML
    private void onClickCSCOperation(ActionEvent actionEvent) {
    }

    @FXML
    private void onClickAnalysis(ActionEvent actionEvent) {
    }

    @FXML
    private void onClickAdministration(ActionEvent actionEvent) {
    }

    @FXML
    private void onLogout(ActionEvent actionEvent) {
    }

    @FXML
    private void onDestinationClicknDes(ActionEvent actionEvent) {
    }

    private int selectedNoOfPassenger = 0;
    @FXML
    public void onTCountIncClick(ActionEvent actionEvent) {
        if (selectedDestination != null) selectedNoOfPassenger+=1;
        Logger.info("Listener for tCountInc button " + selectedNoOfPassenger);
        this.setTicketCount();


    }

    private void setTicketCount() {
        Platform.runLater(() -> tCountText.setText(String.valueOf(selectedNoOfPassenger)));
    }

    @FXML
    public void onTCountDecClick(ActionEvent actionEvent) {

        if (selectedNoOfPassenger > 0 && selectedDestination != null) {
            selectedNoOfPassenger-=1;
        }
        this.setTicketCount();

    }



    TicketType selectedTicketType=null;
    @FXML
    public void onSJTClick(ActionEvent actionEvent) {
        System.out.println("Listener for SJT button");
        sjt.setSelected(true);
        rjt.setSelected(false);
        group.setSelected(false);
        selectedTicketType = mainUIService.getTicketTypeDetails()[0];
    }

    @FXML
    public void onRJTClick(ActionEvent actionEvent) {
        selectedTicketType =  mainUIService.getTicketTypeDetails()[1];
        rjt.setSelected(true);
        sjt.setSelected(false);
        group.setSelected(false);
        System.out.println("Listener for RJT button");
    }

    @FXML
    public void onGroupClick(ActionEvent actionEvent) {
        selectedTicketType =  mainUIService.getTicketTypeDetails()[2];
        group.setSelected(true);
        sjt.setSelected(false);
        rjt.setSelected(false);

        System.out.println("Listener for Group button");
    }



    @FXML
    private void onPayClick(ActionEvent actionEvent) {

        if (selectedDestination == null || selectedNoOfPassenger == 0 || selectedTicketType == null) {
            Logger.error("Destination, No of Passenger and Ticket Type are mandatory");
            return;
        }

        if(selectedDestination.getStationId().equals(SystemConfig.getInstance().getCurrentStation().getStationId())){
            Logger.error("Source and Destination are same");
            return;
        }

        String orderId= SystemConfig.setLastOrderId(Helper.generateOrderId());

       MetroTicket[] metroTickets =  mainUIService.ConfirmTicket(selectedDestination, selectedNoOfPassenger, selectedTicketType).toArray(new MetroTicket[0]);
        //Redirect to payment Screen
        try {
            FXMLLoader fxmlLoader = ViewFactory.getPayment();
            Parent root = fxmlLoader.load();
            PaymentController paymentController = fxmlLoader.getController();
            paymentController.setTicketsGrid(metroTickets,orderId);
            paymentController.setParent(borderPane);
            borderPane.setCenter(root);

        } catch (Exception e) {
            Logger.error("Error in loading payment scene: {}", e.getMessage());

        }
        actionEvent.consume();

    }

    @FXML
    private void onAddClick(ActionEvent actionEvent) {
        try{
        mainUIService.addTicket(selectedDestination, selectedNoOfPassenger, selectedTicketType, Language.ENGLISH);
        }catch (Exception e){
            Logger.error("Error in adding ticket: {}",e.getMessage());
        }

    }

    @FXML
    private void onCancelClick(ActionEvent actionEvent) {
    }

    @FXML
    void onDestinationClick(ActionEvent actionEvent) {
    }
}
