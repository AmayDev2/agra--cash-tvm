package com.amay.tvm.controller;

import com.amay.printer.BNRLoadUnload;
import com.amay.printer.BalanceReport;
import com.amay.printer.CoinLoadedReport;
import com.amay.printer.PrinterCommandDispatcher;
import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.controller.components.StatusBottomBarView;
import com.amay.tom.enums.Alarm;
import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.model.TicketType;
import com.amay.tom.pdu.controller.StationMode;
import com.amay.tom.pdu.controller.command.PDUCommandDispatcher;
import com.amay.tom.repository.StationData;
import com.amay.tom.service.siftservice.ShiftService;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.bnr.BNRIntegration;
import com.amay.tvm.coin.CoinModuleInterface;
import com.amay.tvm.ups.UPS;
import com.amay.tvm.ups.UPSInterface;
import com.amay.tvm.ups.command.UPSCommand;
import com.amay.tvm.ups.communication.UPSCommunicationInterface;
import com.amay.tvm.ups.exception.UPSCommunicationException;
import com.amay.tvm.util.Snackbar;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.network.monitorandcontrol.OperationMode;
import org.network.monitorandcontrol.SpecialMode;
import org.tinylog.Logger;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

public class TVMController {
    @FXML private Button sjtButton;
    @FXML private Button rjtButton;
    @FXML private Button gtButton;
    @FXML private Button balanceUpdateButton;
    @FXML private Button cardInquiryButton;
    @FXML private Button ncmcButton;
    @FXML private GridPane home;
    @FXML private Label labelStationName;
    @FXML private Label lableTime;
    @FXML private Label labelDate;

    @FXML private Button qrPurchaseButton;

    @FXML private StackPane stackPane;
    @FXML private BorderPane borderPane;
    private final Agent agent;
    private StationData stationData;
    DeviceOperationMode currentMode;
    private StatusBottomBarView statusBottomBarView;
    private Timeline timeline;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final PauseTransition inactivityTimer;


    public TVMController(Agent agent) {
        this.agent = agent;
        int idealTimeOut=agent.getTvmConfigRepository().findTVMConfig().getIdleScreenTimeout();
        idealTimeOut=15;
        Logger.tag(LoggerTag.APP).debug("Ideal Timeout {}",idealTimeOut);
        inactivityTimer= new PauseTransition(Duration.seconds(idealTimeOut));
        inactivityTimer.setOnFinished(e -> navigateToHomeScreen());
    }

    public void CleanUp(){
        Logger.tag(LoggerTag.APP).info("TVMController CleanUp called");
        this.agent.getDeviceStatus().removeDeviceStatusListener(this::setOperationMode);
        if(this.statusBottomBarView!=null){
            this.statusBottomBarView.CleanUp();
        }
        if(this.stackPane!=null){
            this.stackPane.getChildren().clear();
        }
        if(this.borderPane!=null){
            this.borderPane.setCenter(null);
            this.borderPane.setBottom(null);
        }
        if(this.timeline!=null && this.timeline.getStatus()==Animation.Status.RUNNING){
            this.timeline.stop();
        }
    }

    @FXML
    void initialize() {
        this.setOperationModeListener();
        stationData = StationData.getInstance();
        labelStationName.setText(SystemConfig.getInstance().getCurrentStation().getStationName());
        this.addBottomBarView();
        this.stackPane.getChildren().addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                int count=this.stackPane.getChildren().size();
                if ( count== 1) {
                    this.showBottomBarView();
                    // apply current mode
                    setOperationMode(this.agent.getDeviceStatus().getCurrentStatus());
                    //UPS check
//                    this.checkUPSStatus(this.agent.getPeripheralMonitor().isUps_connected()
//                            && this.agent.getPeripheralMonitor().isUps_on());
                }else{
                    if(count<4)setTimeout(); else inactivityTimer.stop();
                    this.hideBottomBarView();
                }
            }
        });


        this.updatePeakHour(ZonedDateTime.now(ZoneId.systemDefault()));
        boolean isWeekDay=this.agent.getBusinessRule().getToday().getDayType().equals("WEEKDAYS");
        this.updateDateTime(isWeekDay);
    }
    private PauseTransition pauseTransition;

    private void checkUPSStatus(boolean isPowerCut) {
        Logger.tag(LoggerTag.APP).info("UPS ON");
        if(1!=stackPane.getChildren().size())return;

        try {
        if(isPowerCut){
                if(null==pauseTransition)pauseTransition=new PauseTransition(Duration.seconds(10));
                pauseTransition.setOnFinished(event ->  this.agent.getInternalListener().EOShift());
                pauseTransition.playFromStart();
                setOperationMode(DeviceOperationMode.POWER_CUT);
                UPS.INTERFACE.fireCommand(UPSCommand.shutdown(1));
                Logger.tag(LoggerTag.APP).info("UPS SHOUTDOWN Command");
        }else{
            Logger.tag(LoggerTag.APP).info("UPS SHOUTDOWN Cancel Command");
            UPS.INTERFACE.fireCommand(UPSCommand.CANCEL_SHUTDOWN);
            if(null!=pauseTransition){
                pauseTransition.stop();
                pauseTransition=null;
            }
        }
        } catch (UPSCommunicationException e) {
            Logger.tag(LoggerTag.APP).error("UPS SHOUTDOWN FAILED {}",e.getMessage());
        }
    }

    private void setTimeout() {
        inactivityTimer.playFromStart();
    }

    private void navigateToHomeScreen() {
        GridPane gridPane=(GridPane) this.stackPane.getChildren().getFirst();
        this.stackPane.getChildren().clear();
        this.stackPane.getChildren().add(gridPane);
    }

    private void showBottomBarView() {
        if (borderPane.getBottom() != null) {
            statusBottomBarView.resumeListener();
            borderPane.getBottom().setVisible(true);
            borderPane.getBottom().setManaged(true);
        }
    }

    private void hideBottomBarView() {
        if (borderPane.getBottom() != null) {
            statusBottomBarView.pauseListener();
            borderPane.getBottom().setVisible(false);
            borderPane.getBottom().setManaged(false);
        }
    }

    private static boolean timeout=false;

    private void updateDateTime(boolean isWeekDay) {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    // Use system default timezone
                    ZonedDateTime nowZoned = ZonedDateTime.now(ZoneId.systemDefault());
                    LocalDateTime now = nowZoned.toLocalDateTime();
                    Platform.runLater(() -> {
                        lableTime.setText(now.format(TIME_FORMATTER).toUpperCase(Locale.ROOT));
                        labelDate.setText(now.format(DATE_FORMATTER));

                        if (isWeekDay && now.getSecond() == 0 ) {

                            // Determine peak time based on system timezone
                            boolean isPeakTime=this.updatePeakHour(nowZoned);

                            Logger.info("Time is a multiple of 1 minute: {} PeakTime: {}", now, isPeakTime);
                        }
                    });
                })
        );
        timeline.setCycleCount((int) agent.getBusinessRule().getRemainingSecondsOfWorkingHour());
        timeline.play();
        timeline.setOnFinished(event -> {
            if(!timeout){
                timeout=true;
                this.agent.getInternalListener().EOShift();
            }
        });
    }

    private boolean updatePeakHour(ZonedDateTime nowZoned){
        boolean isPeakTime = false;
        try {
            long epochSeconds = nowZoned.toInstant().toEpochMilli();
            isPeakTime = agent.getBusinessRule().isUnderPeakTime(epochSeconds);
            boolean finalIsPeakTime = isPeakTime;
            Logger.debug("Peak Time: {}", finalIsPeakTime);
//            Platform.runLater(()->peakHour.setVisible(finalIsPeakTime));
        } catch (Exception e) {
            Logger.error("Error checking peak time: {}", e.getMessage());
        }
        return isPeakTime;
    }

    private void addBottomBarView() {
        try {
            Consumer<Boolean> handler = this::checkUPSStatus;
            FXMLLoader fxmlLoader = ViewFactory.getBottomNav();
            statusBottomBarView=new StatusBottomBarView(agent.getPeripheralMonitor(), agent.getVersions(),agent.getMasterConfigInfo(),handler);
            fxmlLoader.setControllerFactory(x -> statusBottomBarView);
            borderPane.setBottom(fxmlLoader.load());
        } catch (RuntimeException | IOException e) {
            System.err.println("Error loading bottom navigation view: " + e.getMessage());
        }
    }


//    @FXML private void  onClickQRTicketButton(ActionEvent actionEvent) {
//        FXMLLoader loader=  ViewFactory.getTicketSelectionView();
//        loader.setControllerFactory(c -> new TicketSelectionController(borderPane, stackPane, agent, stationData));
//        try {
//            stackPane.getChildren().add(loader.load());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        actionEvent.consume();
//    }


    private void setOperationModeListener(){
        agent.getDeviceStatus().addDeviceStatusListener(this::setOperationMode);
    }

    //TODO: Implement the logic to updateToAdd the service mode
    private void setOperationMode(DeviceOperationMode newStatus) {
        if(this.stackPane.getChildren().size()!=1)return;   // to prevent operation mode when not a  home screen; other option is  to remove listener but there every time you have to remove and add listener[NOT IMPLEMENTING]
        Logger.tag(LoggerTag.APP).info("TVMController setOperationMode: " + newStatus);
        Platform.runLater(() -> {
            if (currentMode != newStatus && newStatus == DeviceOperationMode.IN_SERVICE) {
                borderPane.setCenter(stackPane);
                agent.getGrpcApiListener().sendAlarm(Alarm.IN_SERVICE);
                agent.getGrpcApiListener().sendOperationMode(OperationMode.IN_SERVICE);
                agent.getGrpcApiListener().sendPeripheralStatus(agent.getPeripheralMonitor().getDeviceStatus());
            } else {
                if (currentMode != newStatus && newStatus == DeviceOperationMode.EMERGENCY) {
                    FXMLLoader loader = ViewFactory.getSpecialModeScreen();
                    loader.setControllerFactory(c -> new SpecialModeController(borderPane, stackPane, agent, stationData, StationMode.EMERGENCY));

                    try {
                        borderPane.setCenter(loader.load());
                        agent.getGrpcApiListener().sendAlarm(Alarm.EMERGENCY);
                        agent.getGrpcApiListener().sendSpecialMode(SpecialMode.EMERGENCY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (currentMode != newStatus && newStatus == DeviceOperationMode.STATION_CLOSE) {
                    FXMLLoader loader = ViewFactory.getSpecialModeScreen();
                    loader.setControllerFactory(c -> new SpecialModeController(borderPane, stackPane, agent, stationData, StationMode.STATION_CLOSED));
                    try {
                        borderPane.setCenter(loader.load());
                        agent.getGrpcApiListener().sendAlarm(Alarm.STATION_CLOSE);
                        agent.getGrpcApiListener().sendSpecialMode(SpecialMode.STATION_CLOSED_MODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (currentMode != newStatus && newStatus == DeviceOperationMode.OUT_OF_SERVICE) {
                    FXMLLoader loader = ViewFactory.getSpecialModeScreen();
                    loader.setControllerFactory(c -> new SpecialModeController(borderPane, stackPane, agent, stationData, StationMode.OUT_OF_SERVICE));
                    try {
                        borderPane.setCenter(loader.load());
                        agent.getGrpcApiListener().sendAlarm(Alarm.OUT_OF_SERVICE);
                        agent.getGrpcApiListener().sendOperationMode(OperationMode.OUT_OF_SERVICE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (currentMode != newStatus && newStatus == DeviceOperationMode.MAINTENANCE) {
                    FXMLLoader loader = ViewFactory.getSpecialModeScreen();
                    loader.setControllerFactory(c -> new SpecialModeController(borderPane, stackPane, agent, stationData, StationMode.MAINTENANCE));
                    try {
                        borderPane.setCenter(loader.load());
                        newStatus.performAction();
                        agent.getGrpcApiListener().sendAlarm(Alarm.MAINTENANCE_MODE);
                        agent.getGrpcApiListener().sendOperationMode(OperationMode.MAINTENANCE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (currentMode != newStatus && newStatus == DeviceOperationMode.POWER_CUT) {
                    FXMLLoader loader = ViewFactory.getSpecialModeScreen();
                    loader.setControllerFactory(c -> new SpecialModeController(borderPane, stackPane, agent, stationData, StationMode.OUT_OF_SERVICE));
                    try {
                        borderPane.setCenter(loader.load());
                        newStatus.performAction();
                        agent.getGrpcApiListener().sendAlarm(Alarm.POWER_CUT);
                        agent.getGrpcApiListener().sendOperationMode(OperationMode.OUT_OF_SERVICE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            currentMode = newStatus;

        });
    }



    public void onClickSjtButton(ActionEvent actionEvent) {
//        borderPane.setBottom(null);
        if(!agent.getPeripheralMonitor().isPrinter_connected()){
//                throw new RuntimeException("Printer not connected");
            Snackbar.INSTANCE.showSnackbar(this.stackPane,"Printer not connected",false,0);
            return;
        }

        FXMLLoader loader=  ViewFactory.getTicketSelectionView();
        loader.setControllerFactory(c -> new TicketSelectionController(borderPane, stackPane, agent, stationData, TicketType.SINGLE));
        try {
            stackPane.getChildren().add(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }

        actionEvent.consume();
    }

    public void onClickRjtButton(ActionEvent actionEvent) {
        if(!agent.getPeripheralMonitor().isPrinter_connected()){
//                throw new RuntimeException("Printer not connected");
            Snackbar.INSTANCE.showSnackbar(this.stackPane,"Printer not connected",false,0);
            return;
        }
//        borderPane.setBottom(null);
        FXMLLoader loader=  ViewFactory.getTicketSelectionView();
        loader.setControllerFactory(c -> new TicketSelectionController(borderPane, stackPane, agent, stationData, TicketType.RETURN));
        try {
            stackPane.getChildren().add(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }

        actionEvent.consume();
    }

    public void onClickGtButton(ActionEvent actionEvent) {
        if(!agent.getPeripheralMonitor().isPrinter_connected()){
            Snackbar.INSTANCE.showSnackbar(this.stackPane,"Printer not connected",false,0);
            return;
        }
        FXMLLoader loader=  ViewFactory.getTicketSelectionView();
        loader.setControllerFactory(c -> new TicketSelectionController(borderPane, stackPane, agent, stationData, TicketType.GROUP));
        try {
            stackPane.getChildren().add(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }

        actionEvent.consume();
    }

    public void onClickNcmc(ActionEvent actionEvent) {
        this.agent.getInternalListener().EOShift();
        actionEvent.consume();
    }

    public void onClickBalanceUpdate(ActionEvent actionEvent) {
        actionEvent.consume();
    }

    public void onClickCardInquiry(ActionEvent actionEvent) {

    }
}