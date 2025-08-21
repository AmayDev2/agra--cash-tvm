package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.config.TicketConfig;
import com.amay.tom.controller.components.StatusBottomBarView;
import com.amay.tom.controllerInterface.controllerInt.ControllerAdapter;
import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.enums.DeviceStatus;
import com.amay.tom.enums.EOSType;
import com.amay.tom.enums.FareMedium;
import com.amay.tom.model.Station;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.equipment.entity.EquipmentPrivilege;
import com.amay.tom.model.siftdata.ShiftHeader;
import com.amay.tom.model.tickets.RequestedTicket;
import com.amay.tom.model.user.entity.UserPrivilege;
import com.amay.tom.pdu.controller.command.*;
import com.amay.tom.repository.StationData;
import com.amay.tom.repository.TicketTypeData;
import com.amay.tom.service.chield.TicketService;
import com.amay.tom.service.chield.ticketservice.ImplTicketService;
import com.amay.tom.service.qrService2.QRControllerService;
import com.amay.tom.service.siftservice.SiftService;
import com.amay.tom.service.siftservice.impl.ImplSiftService;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tom.utils.helper.Helper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tinylog.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller {

    public static Controller controller;
    private final SiftService siftService;
    // Date and Time
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final StationData stationData;
    private final TicketTypeData ticketTypeData;
    @FXML
    public ImageView clockIcon;
    @FXML
    public ImageView calendarIcon;
    @FXML private ListView ticketDetailsList;

    @FXML
    private Button qrtOperations;

    @FXML
    protected Label dateLabel;
    @FXML
    protected Label timeLabel;
     int MIN_GROUP_TICKET_PASSENGER = 2;
     int MAX_GROUP_TICKET_PASSENGER =30;
    @FXML
    private Label shiftIdLabel;
    @FXML
    private Button redisStatus;
    @FXML
    private ColumnConstraints navigationGrid;
    @FXML
    private Button internetStatus;
    @FXML
    private Button logout;
    @FXML
    private Label userId;
    @FXML
    private Label equipmentId;
    @FXML
    private ToggleButton sjt, rjt, group;
    @FXML
    private Label csn;
    @FXML
    private MenuButton destinationMenu;
    @FXML
    private ImageView logo;
    @FXML
    private Button ncmc;
//    @FXML
//    private Text ncmcAviable;
    @FXML
    private Text ncmcSale;
    @FXML
    private Label ncmcStock;
    @FXML
    private Text qrAviable;
    @FXML
    private Text qrSale;
    @FXML
    private Label qrStock;
    @FXML
    private Button tCountInc, tCountDec;
    @FXML
    private Label tCountText;
    @FXML
    private Button analysisButton;
    @FXML
    private MenuButton noOfPassengerMenu;
    @FXML
    private Button pay;
    @FXML
    private Button qrticket;
    @FXML
    private MenuButton ticketTypeMenu;
    @FXML
    private Label welcomeText;
    @FXML
    private BorderPane borderPane;
    @FXML
    private ToggleGroup toggleGroup;
    private Station selectedDestination;
    private int selectedNoOfPassenger = 1;
    private TicketType selectedTicketType;
    private Station currentStation = null;
    //    private var qrScreen=null;
    private TicketService ticketService = null;
    @FXML
    private VBox ticketGen;
    @FXML
    private Button printerStatus;
    @FXML
    private Button scannerStatus;
    @FXML
    private Button otherDevice;
    private int[] previousStatus = null;
    @FXML
    private Button administration;
    @FXML
    private Label serviceModeLabel;
    @FXML
    private Button countThread;
    @FXML
    private Label selectedStationLabel;  // New label to show selected station

    private UserPrivilege userPrivilege;
    private EquipmentPrivilege equipmentPrivilege;

    @FXML
    private ObservableList<RequestedTicket> items;

    @FXML
    private GridPane stationGrid;
    @FXML
    private VBox selectedStationsList; // Add this field for selected stations list
    private ToggleButton selectedButton = null;

    public Controller() {
        siftService = ImplSiftService.INSTANCE;
        stationData = StationData.getInstance();
        ticketTypeData = TicketTypeData.getInstance();
        currentStation = SystemConfig.getInstance().getCurrentStation();
        ticketService = new ImplTicketService();
        items = FXCollections.observableArrayList();
    }

    private Agent agent;

    private QRControllerService qrControllerService;
    public Controller(Agent agent) {
        this();
        this.agent=agent;
        this.userPrivilege = agent.getUserPrivilege();
        this.equipmentPrivilege = agent.getEquipmentPrivilege();
//        agent.getPeripheralMonitor().addDeviceStatusListener(new UIDeviceListener(this));
        System.out.println("Controller Constructor :" + userPrivilege);
        qrControllerService = new QRControllerService(agent,borderPane,this);
    }




    public static Controller getController() {return controller;}

    @FXML
    void onNoOfPassengerClick(ActionEvent event) {
        System.out.println("Listener for number of passengers menu");
    }

    @FXML
    void onTicketTypeClick(ActionEvent event) {
        System.out.println("Listener for ticket type menu");
    }

    @FXML
    void onDestinationClick(ActionEvent event) {
        System.out.println("Listener for destination menu");
    }

    /*@FXML
    void onPayClick(ActionEvent event) throws IOException {

        if (selectedDestination == null || selectedNoOfPassenger == 0 || selectedTicketType == null) {
            Logger.error("Destination, No of Passenger and Ticket Type are mandatory");
            return;
        }

        if (selectedDestination.getStationId().equals(currentStation.getStationId())) {
            Logger.error("Destination and current station cannot be same");
            return;
        }
        if (FareMedium.QR.getAvailableStock() <= selectedNoOfPassenger){
            Logger.error("No stock available for QR Ticket");
            return;
        }

        String orderId=Helper.generateOrderId();

        ArrayList<MetroTicket> metroTicketsList = new ArrayList<>();
        if (!selectedTicketType.equals(TicketType.GROUP) && selectedNoOfPassenger < MIN_GROUP_TICKET_PASSENGER) {
            for (int i = 0; i < selectedNoOfPassenger; i++) {
                MetroTicket metroTicket=ticketService.generateTicket(currentStation, selectedDestination, currentStation, 1, selectedTicketType);

                metroTicketsList.add(metroTicket);
            }
        } else {
            metroTicketsList.add(ticketService.generateTicket(currentStation, selectedDestination, currentStation, selectedNoOfPassenger, selectedTicketType));
        }
//        MetroTicket metroTicket = ticketService.generateTicket(currentStation, selectedDestination, currentStation, selectedNoOfPassenger, selectedTicketType);
        MetroTicket[] metroTickets = metroTicketsList.toArray(new MetroTicket[0]);


        //Redirect to payment Screen
        try {
            FXMLLoader fxmlLoader = ViewFactory.getPayment();
            fxmlLoader.setControllerFactory(x->new PaymentController(agent));
            Parent root = fxmlLoader.load();
            PaymentController paymentController = fxmlLoader.getController();
            paymentController.setTicketsGrid(metroTickets,orderId);
            paymentController.setParent(borderPane);
            borderPane.setCenter(root);

        } catch (Exception e) {
            Logger.error("Error in loading payment scene: {}", e.getMessage());
            e.printStackTrace();
        }

    }*/

    private static boolean timeout=false;

    private void outOfWorkingHour(){
        timeout=true;
        PDUCommandDispatcher.INSTANCE.dispatch(new AbnormalStationModeCommand(DeviceOperationMode.OUT_OF_SERVICE));
        Platform.runLater(()->
                {
                    this.timeLabel.setStyle("-fx-text-fill: red;");
                    equipmentPrivilege.setQrPaidTicket(false);
                    equipmentPrivilege.setQrFreeTicket(false);
                    equipmentPrivilege.setQrTicketAdjustment(false);
                    equipmentPrivilege.setQrTicketIssue(false);
                    equipmentPrivilege.setQrTicketRefund(false);
                    equipmentPrivilege.setQrTicketReplacement(false);
                }
        );
    }

    @FXML  //Confirm Button
    void onPayClick(ActionEvent event) throws IOException {
        if (!agent.getBusinessRule().isActiveWorkingHour()) {
            outOfWorkingHour();
            // A: You can place your alert here
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Access Denied");
            alert.setHeaderText("Outside Working Hours");
            alert.setContentText("You are trying to access the system outside of the allowed working hours.");
            alert.showAndWait(); // blocks until user closes the alert
            return;
        }

        if(items.isEmpty()){
            Logger.error("No ticket to pay");
            return;
        }
        Parent root = qrControllerService.createTicketRequest(items,borderPane);
        borderPane.setCenter(root);
        event.consume();
    }

    @FXML
    void onCancelClick(ActionEvent event) {
        selectedDestination = null;
        selectedNoOfPassenger = 1;
        selectedTicketType = null;
        tCountText.setText(selectedNoOfPassenger + "");
        destinationMenu.setText("Destination");
//        noOfPassengerMenu.setText("No of Passenger");
//        ticketTypeMenu.setText("Ticket Type");
        sjt.setSelected(false);
        rjt.setSelected(false);
        group.setSelected(false);


        System.out.println("Listener for cancel button");
        event.consume();
    }

    @FXML
    public void qrTicket(ActionEvent actionEvent) {
        try {
            PDUCommandDispatcher.INSTANCE.dispatch(new WelcomePageCommand());
            borderPane.setCenter(ticketGen);
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception as per your requirement
        }
        actionEvent.consume();
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
    public void onSJTClick(ActionEvent actionEvent) {
        System.out.println("Listener for SJT button");
        sjt.setSelected(true);
        rjt.setSelected(false);
        group.setSelected(false);
        selectedNoOfPassenger = 1;
        this.setTicketCount();
        selectedTicketType = ticketTypeData.getTicketTypeArray()[0];
        actionEvent.consume();
    }

    @FXML
    public void onRJTClick(ActionEvent actionEvent) {
        selectedTicketType = ticketTypeData.getTicketTypeArray()[1];
        rjt.setSelected(true);
        sjt.setSelected(false);
        group.setSelected(false);
        selectedNoOfPassenger = 1;
        this.setTicketCount();
        System.out.println("Listener for RJT button");
        actionEvent.consume();
    }

    @FXML
    public void onGroupClick(ActionEvent actionEvent) {
        selectedTicketType = ticketTypeData.getTicketTypeArray()[2];
        group.setSelected(true);
        sjt.setSelected(false);
        rjt.setSelected(false);
        selectedNoOfPassenger = MIN_GROUP_TICKET_PASSENGER;
        this.setTicketCount();

        System.out.println("Listener for Group button");
        actionEvent.consume();
    }


    public void populateStations(List<Station> stations) {
        stationGrid.getChildren().clear();
        stationGrid.setHgap(20);
        stationGrid.setVgap(10);
        int columns = 3;
        int row = 0, col = 0;

        ToggleGroup group = new ToggleGroup();

        for (Station station : stations) {
            ToggleButton btn = new ToggleButton(station.getStationName());
            btn.setMinWidth(175);
            btn.setMinHeight(45);
            btn.setId(station.getStationId());
            btn.setUserData(station);
            btn.setToggleGroup(group);
            btn.getStyleClass().add("station-toggle");

            if (station.getStationId().equals(currentStation.getStationId())) {
                btn.setDisable(true);
                btn.getStyleClass().add("disabled-station");
            }

            // ✅ Use MousePressed instead of setOnAction
            btn.setOnMousePressed(e -> {
                if (btn.equals(group.getSelectedToggle())) {
                    // Deselect
                    group.selectToggle(null);
                    selectedDestination = null;
                    e.consume(); // Prevent default selection behavior
                }
            });

            // ✅ Set destination on action (after actual toggle happens)
            btn.setOnAction(e -> {
                if (btn.isSelected()) {
                    selectedDestination = (Station) btn.getUserData();
                }
            });

            stationGrid.add(btn, col, row);

            col++;
            if (col == columns) {
                col = 0;
                row++;
            }
        }
    }



//    public void populateStations(List<Station> stations) {
//        stationGrid.getChildren().clear();
//        stationGrid.setHgap(20); // Reduced horizontal gap
//        stationGrid.setVgap(10); // Reduced vertical gap
//        int columns = 3;
//        int row = 0, col = 0;
//
//        ToggleGroup group = new ToggleGroup();
//
//        for (Station station : stations) {
//            ToggleButton btn = new ToggleButton(station.getStationName());
//            btn.setMinWidth(175); // Slightly reduced width to accommodate 5 columns
//            btn.setMinHeight(45); // Slightly reduced height
//            btn.setId(station.getStationId());
//            btn.setUserData(station);
//            btn.setStyle("-fx-font-size: 14; -fx-background-radius: 6; -fx-background-color: #f1f1f1; -fx-border-color: #bbb; -fx-border-radius: 6; -fx-font-weight: bold;");
//            btn.setToggleGroup(group);
//
//            // Disable and style current station
//            if (station.getStationId().equals(currentStation.getStationId())) {
//                btn.setStyle("-fx-font-size: 14; -fx-background-radius: 6; -fx-background-color: #f1f1f1; -fx-border-color: #bbb; -fx-border-radius: 6; -fx-font-weight: bold; -fx-text-fill: red;");
//                btn.setDisable(true);
//            }
//
//            btn.setOnAction(e -> {
//                ToggleButton selectedBtn = (ToggleButton) e.getSource();
//                selectedDestination = (Station) selectedBtn.getUserData();
//
//            });
//
//            stationGrid.add(btn, col, row);
//
//            col++;
//            if (col == columns) {
//                col = 0;
//                row++;
//            }
//        }
//    }



    @FXML
    public void onTCountIncClick(ActionEvent actionEvent) {
        if (selectedDestination != null && selectedTicketType != null
                && ((selectedNoOfPassenger < MIN_GROUP_TICKET_PASSENGER-1
                && !selectedTicketType.equals(TicketType.GROUP))
                || (selectedNoOfPassenger < MAX_GROUP_TICKET_PASSENGER && selectedTicketType.equals(TicketType.GROUP))))
            selectedNoOfPassenger += 1;

        Logger.info("Listener for tCountInc button " + selectedNoOfPassenger);
        this.setTicketCount();
        actionEvent.consume();

    }

    private void setTicketCount() {
        Platform.runLater(() -> tCountText.setText(String.valueOf(selectedNoOfPassenger)));
    }

    @FXML
    public void onTCountDecClick(ActionEvent actionEvent) {
        if (selectedNoOfPassenger > 1 && selectedDestination != null && ((selectedNoOfPassenger > 0 && !selectedTicketType.equals(TicketType.GROUP)) || (selectedNoOfPassenger > MIN_GROUP_TICKET_PASSENGER && selectedTicketType.equals(TicketType.GROUP)))) {
            selectedNoOfPassenger -= 1;
        }
        this.setTicketCount();

    }

    @FXML
    void onLogout(ActionEvent event) {
        try {
            siftService.endOfShift(EOSType.OPERATOR);
            ((Node) event.getSource()).getScene().setRoot(ViewFactory.getLogin().load());

            Logger.info("Logout successful for user: {}", ShiftHeader.getInstance().getOperatorId());
            Logger.info("Shift Details:  {}", ShiftHeader.getInstance().toString());
        } catch (IOException e) {
            Logger.warn("Error in loading login scene " + e.getMessage());
            e.printStackTrace();

        }
        event.consume();

    }

    @FXML
    public void onClickAnalysis(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = ViewFactory.getAnalysis();
            fxmlLoader.setControllerFactory(x->new AnalysisController(this.agent,borderPane));
            borderPane.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception as per your requirement
        }

    }

    public void updateStatus(int[] deviceStatus) {
        Logger.debug("Home Device Status: {}", Arrays.toString(deviceStatus));
        if (deviceStatus != null && !Arrays.equals(deviceStatus, previousStatus)) {
            scannerStatus.setStyle(deviceStatus[0] == 1 ? "-fx-background-color: green" : "-fx-background-color: red");
            printerStatus.setStyle(deviceStatus[1] == 1 ? "-fx-background-color: green" : "-fx-background-color: red");
            internetStatus.setStyle(deviceStatus[2] == 1 ? "-fx-background-color: green" : "-fx-background-color: red");
            redisStatus.setStyle(deviceStatus[3] == 1 ? "-fx-background-color: green" : "-fx-background-color: red");
            previousStatus = Arrays.copyOf(deviceStatus, deviceStatus.length);
        }
    }

    @FXML
    public void onClickAdministration(ActionEvent actionEvent) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/administration.fxml"));
            fxmlLoader.setControllerFactory(x->new AdministrationController(this.agent,borderPane));
            Parent parent = fxmlLoader.load();
            borderPane.setCenter(parent);
        } catch (IOException e) {
            Logger.error("Error in loading Administration View: {}", e.getMessage());
            e.printStackTrace(); // Handle the exception as per your requirement
        }
        actionEvent.consume();
    }


    @FXML
    void initialize() {
        MIN_GROUP_TICKET_PASSENGER= TicketConfig.INSTANT.getProductTypeDefDTO().getMinGroupTicket();
        MAX_GROUP_TICKET_PASSENGER=TicketConfig.INSTANT.getProductTypeDefDTO().getMaxGroupTicket();
        controller = this;
        setOperationModeListener();
        System.out.println("Controller Initialized "+controller.hashCode()+" "+agent.getDeviceStatus().getCurrentStatus());
        userPrivilege(userPrivilege,equipmentPrivilege);
        updateStock();
        setStockManagement();
        ControllerAdapter.INSTANCE.setCenterAnchorPane(borderPane);
        // Add event handler to countThread button
        countThread.addEventHandler(ActionEvent.ACTION, event -> {
            try {
                Logger.debug("Total Thread: {}", new Helper().countRunningThreads());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        try {
            FXMLLoader fxmlLoader = ViewFactory.getBottomNav();
            fxmlLoader.setControllerFactory(x->new StatusBottomBarView(agent.getPeripheralMonitor(),agent.getVersions()));
            borderPane.setBottom(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


//        // Set the service mode
//        serviceModeLabel.setText(DeviceStatus.IN_SERVICE.getDeviceStatusName());
//        serviceModeLabel.setStyle("-fx-text-fill: " + DeviceStatus.IN_SERVICE.getColor());
        this.updateDateTime();


        userId.setText(agent.getShift().getOperatorId());
        ToggleGroup toggleGroup = new ToggleGroup();
        csn.setText(currentStation.getStationId().replace("st", ""));
        shiftIdLabel.setText(this.agent.getShift().getShiftId());
        equipmentId.setText( SystemConfig.getInstance().getCurrentEquipment().getEquipmentId());
//        logo.setImage(new Image("file:amaylogo.png"));
        calendarIcon.setImage(new Image("file:calender.png"));
        clockIcon.setImage(new Image("file:clock.png"));
        tCountText.setText(String.valueOf(selectedNoOfPassenger));
        Station[] stationArray = Arrays.stream(stationData.getStationArray())
//                .sorted(Comparator.comparing(Station::getStationName)) // replace getStationName with your method to get the station name
                .toArray(Station[]::new);

        // Populate grid with stations
        populateStations(Arrays.stream(stationArray).toList());

        // Bind the ListView to the ObservableList
        ticketDetailsList.setItems(items);

        // Set a custom cell factory for multi-column display and remove button
        ticketDetailsList.setCellFactory(listView -> new ListCell<RequestedTicket>() {
            private final HBox content;
            private final Label destinationLabel;
            private final Label typeLabel;
            private final Label quantityLabel;
            private final Button removeButton;

            {
                destinationLabel = new Label();
                destinationLabel.setPrefWidth(110);
                destinationLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #222;");
                typeLabel = new Label();
                typeLabel.setPrefWidth(80);
                typeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                quantityLabel = new Label();
                quantityLabel.setPrefWidth(20);
                quantityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                removeButton = new Button("✖");
                removeButton.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #b71c1c; -fx-font-size: 15px; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 2 8;");
                content = new HBox(10, destinationLabel, typeLabel, quantityLabel, removeButton);
                content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                content.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-padding: 8 10; -fx-effect: dropshadow(gaussian, #e0e0e0, 2, 0.1, 0, 1);");
                content.setOnMouseEntered(e -> content.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 10; -fx-padding: 8 10; -fx-effect: dropshadow(gaussian, #b3e5fc, 4, 0.2, 0, 2);"));
                content.setOnMouseExited(e -> content.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-padding: 8 10; -fx-effect: dropshadow(gaussian, #e0e0e0, 2, 0.1, 0, 1);"));
            }

            @Override
            protected void updateItem(RequestedTicket ticket, boolean empty) {
                super.updateItem(ticket, empty);
                if (empty || ticket == null) {
                    setGraphic(null);
                } else {
                    destinationLabel.setText(ticket.destination().getStationName());
                    typeLabel.setText(ticket.ticketType().name());
                    quantityLabel.setText(String.valueOf(ticket.quantity()));
                    removeButton.setOnAction(e -> getListView().getItems().remove(ticket));
                    setGraphic(content);
                }
            }
        });
    }

    private void userPrivilege(UserPrivilege userPrivilege, EquipmentPrivilege equipmentPrivilege) {
        qrticket.setDisable(!(userPrivilege.isQrTicketIssue() && equipmentPrivilege.isQrTicketIssue().get()));
        analysisButton.setVisible(userPrivilege.isQrTicketAnalysis() && equipmentPrivilege.isQrTicketAnalysis().get());


            equipmentPrivilege.isQrTicketIssue().addListener((observable, oldValue, newValue) -> {
                qrticket.setDisable(!(userPrivilege.isQrTicketIssue() && equipmentPrivilege.isQrTicketIssue().get() && newValue));
            });

            equipmentPrivilege.isQrTicketAnalysis().addListener((observable, oldValue, newValue) -> {
                analysisButton.setVisible(userPrivilege.isQrTicketAnalysis() && equipmentPrivilege.isQrTicketAnalysis().get() && newValue);
            });


    }

    private void setOperationModeListener(){
        setOperationMode(agent.getDeviceStatus().getCurrentStatus());


        agent.getDeviceStatus().addDeviceStatusListener(this::setOperationMode);
    }

    //TODO: Implement the logic to update the service mode
    private void setOperationMode(DeviceOperationMode newStatus){
//        if(DeviceOperationMode.EMERGENCY.equals(newStatus)){
            PDUCommandDispatcher.INSTANCE.dispatch(new AbnormalStationModeCommand(newStatus));
//        }else if(DeviceOperationMode.STATION_CLOSE.equals(newStatus)){
//            PDUCommandDispatcher.INSTANCE.dispatch(new AbnormalStationModeCommand(DeviceOperationMode.STATION_CLOSE));
//        }else{
//
//        }

        Platform.runLater(() -> {
            serviceModeLabel.setText(newStatus.getDeviceStatusName());
            serviceModeLabel.setStyle("-fx-text-fill: " + newStatus.getColor());
       /* switch (newStatus){
            case IN_SERVICE:
                serviceModeLabel.setText(DeviceStatus.IN_SERVICE.getDeviceStatusName());
                serviceModeLabel.setStyle("-fx-text-fill: " + DeviceStatus.IN_SERVICE.getColor());
                break;
            case PAUSE:
                serviceModeLabel.setText(DeviceStatus.PAUSE.getDeviceStatusName());
                serviceModeLabel.setStyle("-fx-text-fill: " + DeviceStatus.PAUSE.getColor());
                break;
            case EMERGENCY:
                serviceModeLabel.setText(DeviceStatus.EMERGENCY.getDeviceStatusName());
                serviceModeLabel.setStyle("-fx-text-fill: " + DeviceStatus.EMERGENCY.getColor());
                break;
            case OUT_OF_SERVICE:
                serviceModeLabel.setText(DeviceStatus.OUT_OF_SERVICE.getDeviceStatusName());
                serviceModeLabel.setStyle("-fx-text-fill: " + DeviceStatus.OUT_OF_SERVICE.getColor());
                break;
            case NORMAL:
                serviceModeLabel.setText(DeviceStatus.NORMAL.getDeviceStatusName());
                serviceModeLabel.setStyle("-fx-text-fill: " + DeviceStatus.NORMAL.getColor());
                break;
            case STATION_CLOSE:
                serviceModeLabel.setText(DeviceStatus.STATION_CLOSE.getDeviceStatusName());
                serviceModeLabel.setStyle("-fx-text-fill: " + DeviceStatus.STATION_CLOSE.getColor());
                break;
            default:
        }*/
        });
    }

    public void PauseShift() {
//        serviceModeUpdate(true);
//        serviceModeLabel.setText(DeviceStatus.PAUSE.getDeviceStatusName());
//        serviceModeLabel.setStyle("-fx-text-fill: " + DeviceStatus.PAUSE.getColor());
    }

    public void onClickCSCOperation(ActionEvent actionEvent) {

        try {
            FXMLLoader fxmlLoader = ViewFactory.getCscOperationsView();
            borderPane.setCenter(fxmlLoader.load());
        } catch (Exception e) {
            Logger.error("Error in loading CSC Operations View: {}", e.getMessage());
            e.printStackTrace();
        }

    }


    public void updateStock() {
        Platform.runLater(() -> {
            ncmcSale.setText(String.valueOf(FareMedium.NCMC.getFareMediumSale()));
            qrSale.setText(String.valueOf(FareMedium.QR.getFareMediumSale()));

//            ncmcAviable.setText(String.valueOf(FareMedium.NCMC.getFareMediumTotal()));
//            qrAviable.setText(String.valueOf(FareMedium.QR.getFareMediumTotal()));

//            if (FareMedium.NCMC.getAvailableStock() <= 10) {
//                ncmcStock.setStyle("-fx-background-color: red;-fx-text-fill: white");
//            } else if (ncmcStock.getStyle().contains("red")) {
                ncmcStock.setStyle("-fx-background-color: green;-fx-text-fill: white");
//            }
//            if (FareMedium.QR.getAvailableStock() <= 10) {
//                qrStock.setStyle("-fx-background-color: red ;-fx-text-fill: white");
//            } else if (qrStock.getStyle().contains("red")) {
                qrStock.setStyle("-fx-background-color: green;-fx-text-fill: white");
//            }

        });
    }

    private void setStockManagement() {
        try {
            FXMLLoader fxmlLoader = ViewFactory.getStockManagement();
            fxmlLoader.setControllerFactory(x->new StocksAddViewController(this.agent,true));
            borderPane.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception as per your requirement
        }
    }

    private void serviceModeUpdate(boolean status) {
        borderPane.getLeft().setDisable(status);
        logout.setDisable(status);

    }

    public void ResumeShift() {


    }


    private void updateDateTime() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            Platform.runLater(() -> {
                timeLabel.setText(timeFormatter.format(now));
                dateLabel.setText(dateFormatter.format(now));
                if (!timeout && !agent.getBusinessRule().isActiveWorkingHour())outOfWorkingHour();
            });
        }));
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE); // Run indefinitely
        timeline.play(); // Start the timeline
    }

    public void onClickQRTOperation(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = ViewFactory.getQRTOperationsView();
            fxmlLoader.setControllerFactory(x->new QRTOperations(borderPane,this.agent));
            borderPane.setCenter(fxmlLoader.load());
        } catch (Exception e) {
            Logger.error("Error in loading QRT Operations View: {}", e.getMessage());
            e.printStackTrace();
        }
        actionEvent.consume();
    }

   public void onAddTicket(ActionEvent actionEvent) throws IOException {
       if (selectedDestination == null || selectedNoOfPassenger == 0 || selectedTicketType == null) {
           Logger.error("Destination, No of Passenger and Ticket Type are mandatory");
           return;
       }

       if (selectedDestination.getStationId().equals(currentStation.getStationId())) {
           Logger.error("Destination and current station cannot be same");
           return;
       }

       //cartLimit
       if(TicketConfig.INSTANT.getProductTypeDefDTO().getMaxTicket()-1<items.size()){
           Alert alert=new Alert(Alert.AlertType.ERROR);
                   alert.setContentText("NO SPACE IN CART");
                   alert.showAndWait();
           throw new RuntimeException("NO SPACE IN CART");
       }
//       if (FareMedium.QR.getAvailableStock() <= selectedNoOfPassenger){
//           Logger.error("No stock available for QR Ticket");
//           return;
//       }
        items.add(new RequestedTicket(agent.getSystemConfig().getCurrentStation(),selectedDestination,  selectedTicketType,selectedNoOfPassenger));

        items.forEach(x->{
            Logger.info("Ticket: {}",x.source().getStationName()+" "+x.destination().getStationName()+" "+x.ticketType()+" "+x.quantity());
        });

        actionEvent.consume();

    }


//    private class ControllerListener {
//
//
//        @Override
//        public void updateTime() {
//            Logger.info("Updating Time");
//            LocalDateTime now = LocalDateTime.now();
//            Platform.runLater(() -> {
//                timeLabel.setText(timeFormatter.format(now));
//                dateLabel.setText(dateFormatter.format(now));
//            });
//
//        }
//
//        @Override
//        public void updateServiceMode(DeviceStatus deviceStatus) {
//            Logger.debug("Updating Service Mode");
//            Platform.runLater(() -> {
//                serviceModeLabel.setText(deviceStatus.getDeviceStatusName());
//                serviceModeLabel.setStyle("-fx-background-color: "+deviceStatus.getColor());
//            });
//
//        }
//    }


}


