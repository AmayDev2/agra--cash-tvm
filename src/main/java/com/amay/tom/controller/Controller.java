package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.controller.components.StatusBottomBarView;
import com.amay.tom.controllerInterface.controllerInt.ControllerAdapter;
import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.model.station.Station;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.equipment.entity.EquipmentPrivilege;
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
import com.amay.tom.utils.helper.Helper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.tinylog.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Controller {

    public static Controller controller;
    private final SiftService siftService;
    // Date and Time
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final StationData stationData;
    private final TicketTypeData ticketTypeData;
    @FXML
    public ImageView clockIcon;
    @FXML
    public ImageView calendarIcon;
    @FXML private Label peakHour;
    @FXML private ListView ticketDetailsList;

    @FXML
    private ToggleButton qrtOperations;

    @FXML
    protected Label dateLabel;

    @FXML
    protected Label message;
    @FXML
    protected Label timeLabel;
     int MIN_GROUP_TICKET_PASSENGER = 10;
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
    private ToggleButton ncmc;

    @FXML
    private Label ncmcStock;
    @FXML
    private Text qrAviable;

    @FXML
    private Label qrStock;
    @FXML
    private Button tCountInc, tCountDec;
    @FXML
    private Label tCountText;
    @FXML
    private ToggleButton analysisButton;
    @FXML
    private MenuButton noOfPassengerMenu;
    @FXML
    private Button pay;
    @FXML
    private ToggleButton qrticket;
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
    private ToggleButton administration;
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
        //System.out.println("Controller Constructor :" + userPrivilege);
        qrControllerService = new QRControllerService(agent,borderPane,this);
    }




    public static Controller getController() {return controller;}


    private static boolean timeout=false;

    private void outOfWorkingHour(){
        timeout=true;
        PDUCommandDispatcher.INSTANCE.dispatch(new AbnormalStationModeCommand(DeviceOperationMode.OUT_OF_SERVICE));
        Platform.runLater(()->
                {
                    this.timeLabel.setStyle("-fx-text-fill: red;");
//                    equipmentPrivilege.setQrPaidTicket(false);
//                    equipmentPrivilege.setQrFreeTicket(false);
//                    equipmentPrivilege.setQrTicketAdjustment(false);
//                    equipmentPrivilege.setQrTicketIssue(false);
//                    equipmentPrivilege.setQrTicketRefund(false);
//                    equipmentPrivilege.setQrTicketReplacement(false);
                }
        );
    }

    @FXML  //Confirm Button
    void onPayClick(ActionEvent event) throws IOException {
        if (!agent.getBusinessRule().isActiveWorkingHour()) {
            outOfWorkingHour();
            message.setText("End of Business Hours");
            return;
        }

        if(items.isEmpty()){
            Logger.error("No ticket to pay");
            message.setText("Add ticket in Cart!");
            return;
        }
        message.setText("");
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


        //System.out.println("Listener for cancel button");
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

    Runnable runnable =()->{
        qrticket.fire();
    };

    @FXML
    public void onSJTClick(ActionEvent actionEvent) {
        //System.out.println("Listener for SJT button");
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
        //System.out.println("Listener for RJT button");
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

        //System.out.println("Listener for Group button");
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
//
//    @FXML
//    void onLogout(ActionEvent event) {
//        try {
//            siftService.endOfShift(EOSType.OPERATOR);
//            ((Node) event.getSource()).getScene().setRoot(ViewFactory.getLogin().load());
//
//            Logger.info("Logout successful for user: {}", ShiftHeader.getInstance().getOperatorId());
//            Logger.info("Shift Details:  {}", ShiftHeader.getInstance().toString());
//        } catch (IOException e) {
//            Logger.warn("Error in loading login scene " + e.getMessage());
//            e.printStackTrace();
//
//        }
//        event.consume();
//
//    }

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
        MIN_GROUP_TICKET_PASSENGER= TicketType.GROUP.getProduct().getMinTicket();
        MAX_GROUP_TICKET_PASSENGER=TicketType.GROUP.getProduct().getMaxTicket();
        controller = this;
        setOperationModeListener();
        //System.out.println("Controller Initialized "+controller.hashCode()+" "+agent.getDeviceStatus().getCurrentStatus());
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
            Consumer<Boolean> handler = null;
            FXMLLoader fxmlLoader = ViewFactory.getBottomNav();
            fxmlLoader.setControllerFactory(x->new StatusBottomBarView(agent.getPeripheralMonitor(),agent.getVersions(),agent.getMasterConfigInfo(), handler));
            borderPane.setBottom(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.updatePeakHour(ZonedDateTime.now(ZoneId.systemDefault()));
        boolean isWeekDay=this.agent.getBusinessRule().getToday().getDayType().equals("WEEKDAYS");
        this.updateDateTime(isWeekDay);
        message.setText("");


        userId.setText(agent.getShift().getOperatorId());
        ToggleGroup toggleGroup = new ToggleGroup();
        csn.setText(currentStation.getStationName());
        shiftIdLabel.setText(this.agent.getShift().getShiftId());
        equipmentId.setText(agent.getSystemConfig().getCurrentEquipment().getEquipmentName());
        tCountText.setText(String.valueOf(selectedNoOfPassenger));
        Station[] stationArray = Arrays.stream(stationData.getStationArray())
//                .sorted(Comparator.comparing(Station::getStationName)) // replace getStationName with your method to get the station name
                .toArray(Station[]::new);

        // Populate grid with stations
        populateStations(Arrays.stream(stationArray).toList());

        // Bind the ListView to the ObservableList
        ticketDetailsList.setItems(items);

        showCart();

        this.activateProducts();

    }

    private void activateProducts() {
            sjt.setDisable(!TicketType.SINGLE.getProduct().isActive());
            rjt.setDisable(!TicketType.RETURN.getProduct().isActive());
            group.setDisable(!TicketType.GROUP.getProduct().isActive());
    }


    private void showCart() {
        ticketDetailsList.setCellFactory(lv -> new ListCell<RequestedTicket>() {
            private final Label dest = new Label();
            private final Label type = new Label();
            private final Label qty  = new Label();
            private final Button remove = new Button("✖");
            private final Region spacer = new Region();
            private final HBox box = new HBox(10, dest, type,spacer, qty, remove);

            {
                // Styles
                dest.setStyle("-fx-font-weight:bold; -fx-font-size:15px; -fx-text-fill:#222; -fx-min-width: 120px;");
                type.setStyle("-fx-font-size:14px; -fx-text-fill:#555;");
                qty.setStyle("-fx-font-size:14px; -fx-text-fill:#555;");
                remove.setStyle("-fx-background-color:#f8d7da; -fx-text-fill:#b71c1c;");

                HBox.setHgrow(spacer, Priority.ALWAYS);
                box.setAlignment(Pos.CENTER_LEFT);
                box.setPadding(new Insets(8));
                box.setStyle("-fx-background-color:#f5f5f5; -fx-background-radius:8; -fx-min-width: 270px;");

                box.setOnMouseEntered(e -> box.setStyle("-fx-background-color:#e3f2fd; -fx-background-radius:8;"));
                box.setOnMouseExited (e -> box.setStyle("-fx-background-color:#f5f5f5; -fx-background-radius:8;"));

                // ADDED: Configure ListView to prevent horizontal scrolling
                ticketDetailsList.setStyle("-fx-background-color:transparent;");

                // Hide horizontal scrollbar completely
                ticketDetailsList.lookup(".scroll-bar:horizontal").setVisible(false);
                // Make width follow ListView
                box.prefWidthProperty().bind(ticketDetailsList.widthProperty().subtract(20));
            }

            @Override
            protected void updateItem(RequestedTicket item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    dest.setText(item.destination().getStationName());
                    type.setText(item.ticketType().getTicketTypeName());
                    qty.setText( String.valueOf(item.quantity()));
                    remove.setOnAction(e -> {
                                getListView().getItems().remove(item);
                                message.setText("");

                            }
                    );
                    setGraphic(box);
                }
            }
        });

        // Remove default selection highlight
        ticketDetailsList.setStyle("-fx-background-color:transparent;");
        ticketDetailsList.getSelectionModel().selectedItemProperty().addListener((o, oldV, newV) ->
                ticketDetailsList.getSelectionModel().clearSelection());
    }



    private void userPrivilege(UserPrivilege userPrivilege, EquipmentPrivilege equipmentPrivilege) {
//        qrticket.setDisable(!(userPrivilege.isQrTicketIssue() && equipmentPrivilege.isQrTicketIssue().get()));
//        analysisButton.setVisible(userPrivilege.isQrTicketAnalysis() && equipmentPrivilege.isQrTicketAnalysis().get());


//            equipmentPrivilege.isQrTicketIssue().addListener((observable, oldValue, newValue) -> {
//                qrticket.setDisable(!(userPrivilege.isQrTicketIssue() && equipmentPrivilege.isQrTicketIssue().get() && newValue));
//            });

//            equipmentPrivilege.isQrTicketAnalysis().addListener((observable, oldValue, newValue) -> {
//                analysisButton.setVisible(userPrivilege.isQrTicketAnalysis() && equipmentPrivilege.isQrTicketAnalysis().get() && newValue);
//            });


    }

    private void setOperationModeListener(){
        setOperationMode(agent.getDeviceStatus().getCurrentStatus());


        agent.getDeviceStatus().addDeviceStatusListener(this::setOperationMode);
    }

    //TODO: Implement the logic to updateToAdd the service mode
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
            //ncmcSale.setText(String.valueOf(FareMedium.NCMC.getFareMediumSale()));
            //qrSale.setText(String.valueOf(FareMedium.QR.getFareMediumSale()));

//            ncmcAviable.setText(String.valueOf(FareMedium.NCMC.getFareMediumTotal()));
//            qrAviable.setText(String.valueOf(FareMedium.QR.getFareMediumTotal()));

//            if (FareMedium.NCMC.getAvailableStock() <= 10) {
//                ncmcStock.setStyle("-fx-background-color: red;-fx-text-fill: white");
//            } else if (ncmcStock.getStyle().contains("red")) {
//                ncmcStock.setStyle("-fx-background-color: green;-fx-text-fill: white");
//            }
//            if (FareMedium.QR.getAvailableStock() <= 10) {
//                qrStock.setStyle("-fx-background-color: red ;-fx-text-fill: white");
//            } else if (qrStock.getStyle().contains("red")) {
//                qrStock.setStyle("-fx-background-color: green;-fx-text-fill: white");
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


    private void updateDateTime(boolean isWeekDay) {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), event -> {
                    // Use system default timezone
                    ZonedDateTime nowZoned = ZonedDateTime.now(ZoneId.systemDefault());
                    LocalDateTime now = nowZoned.toLocalDateTime();
                    Platform.runLater(() -> {
                        timeLabel.setText(timeFormatter.format(now));
                        dateLabel.setText(dateFormatter.format(now));

                        if(now.getSecond()==0){
                            // Update the clock icon or other features if needed
                            if (!timeout && !agent.getBusinessRule().isActiveWorkingHour()) {
                                outOfWorkingHour();
                            }
                        }

//                        // Check at every 5-minute mark
//                        if (now.getMinute() % 5 == 0 && now.getSecond() == 0) {
//                            // Perform tasks every 5 minutes
//                            Logger.info("Time is a multiple of 5 minutes: {}", now);
//                        }
                        // Check at every minute
                        if (isWeekDay && now.getSecond() == 0 ) {

                            // Determine peak time based on system timezone

                            boolean isPeakTime=this.updatePeakHour(nowZoned);

                            Logger.info("Time is a multiple of 1 minute: {} PeakTime: {}", now, isPeakTime);
                        }
                    });
                })
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    private boolean updatePeakHour(ZonedDateTime nowZoned){
        boolean isPeakTime = false;
        try {
            long epochSeconds = nowZoned.toInstant().toEpochMilli();
            isPeakTime = agent.getBusinessRule().isUnderPeakTime(epochSeconds);
            boolean finalIsPeakTime = isPeakTime;
            Logger.debug("Peak Time: {}", finalIsPeakTime);
            Platform.runLater(()->peakHour.setVisible(finalIsPeakTime));
        } catch (Exception e) {
            Logger.error("Error checking peak time: {}", e.getMessage());
        }
        return isPeakTime;
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


       if(agent.getTomConfig().getCartLimit()-1<items.size()){
           message.setText("CART FULL");
           throw new RuntimeException("NO SPACE IN CART");
       }

        items.add(new RequestedTicket(agent.getSystemConfig().getCurrentStation(), selectedDestination, selectedTicketType, selectedNoOfPassenger));

        items.forEach(x->{
            Logger.info("Ticket: {}",x.source().getStationName()+" "+x.destination().getStationName()+" "+x.ticketType()+" "+x.quantity());
        });

        actionEvent.consume();

    }

    public void disableButtons() {
        borderPane.getLeft().setDisable(true);
    }

    public void enableButtons() {
        borderPane.getLeft().setDisable(false);
    }




}


