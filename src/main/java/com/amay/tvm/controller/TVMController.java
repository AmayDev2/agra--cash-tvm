package com.amay.tvm.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.controller.components.StatusBottomBarView;
import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.model.TicketType;
import com.amay.tom.pdu.controller.StationMode;
import com.amay.tom.repository.StationData;
import com.amay.tvm.util.Snackbar;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.tinylog.Logger;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM, yyyy");


    public TVMController(Agent agent) {
        this.agent = agent;
    }

    private void startTime(){

            Platform.runLater(() -> {
                Timeline clock = new Timeline(
                        new KeyFrame(Duration.ZERO, e -> {
                            LocalDateTime now = LocalDateTime.now();
                            lableTime.setText(now.format(TIME_FORMATTER).toUpperCase(Locale.ROOT));
                            labelDate.setText(now.format(DATE_FORMATTER));
                        }),
                        new KeyFrame(Duration.seconds(1))
                );
                clock.setCycleCount(Timeline.INDEFINITE);
                clock.play();
            });
    }

    @FXML
    void initialize() {
        this.setOperationModeListener();
        stationData = StationData.getInstance();
        labelStationName.setText(SystemConfig.getInstance().getCurrentStation().getStationName());
        this.startTime();
        this.addBottomBarView();

        this.updatePeakHour(ZonedDateTime.now(ZoneId.systemDefault()));
        boolean isWeekDay=this.agent.getBusinessRule().getToday().getDayType().equals("WEEKDAYS");
        this.updateDateTime(isWeekDay);
    }

    private static boolean timeout=false;

    private void updateDateTime(boolean isWeekDay) {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), event -> {
                    // Use system default timezone
                    ZonedDateTime nowZoned = ZonedDateTime.now(ZoneId.systemDefault());
                    LocalDateTime now = nowZoned.toLocalDateTime();
                    Platform.runLater(() -> {
                        lableTime.setText(now.format(TIME_FORMATTER).toUpperCase(Locale.ROOT));
                        labelDate.setText(now.format(DATE_FORMATTER));

                        if(now.getSecond()==0){
                            // Update the clock icon or other features if needed
                            if (!timeout && !agent.getBusinessRule().isActiveWorkingHour()) {
//                                outOfWorkingHour();
                                this.agent.getInternalListener().EOShift();

                                //TODO: Show out of working hour screen
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
//            Platform.runLater(()->peakHour.setVisible(finalIsPeakTime));
        } catch (Exception e) {
            Logger.error("Error checking peak time: {}", e.getMessage());
        }
        return isPeakTime;
    }

    private void addBottomBarView() {
        try {
            FXMLLoader fxmlLoader = ViewFactory.getBottomNav();
            fxmlLoader.setControllerFactory(x -> new StatusBottomBarView(agent.getPeripheralMonitor(), agent.getVersions(),agent.getMasterConfigInfo()));
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
//
//        actionEvent.consume();
//    }


    private void setOperationModeListener(){
        setOperationMode(agent.getDeviceStatus().getCurrentStatus());


        agent.getDeviceStatus().addDeviceStatusListener(this::setOperationMode);
    }

    //TODO: Implement the logic to update the service mode
    private void setOperationMode(DeviceOperationMode newStatus){
        //System.out.println("TVMController setOperationMode: " + newStatus);
        Platform.runLater(() -> {
            if( currentMode!=newStatus && newStatus == DeviceOperationMode.IN_SERVICE){
                stackPane.getChildren().clear();
                stackPane.getChildren().add(home);
            }else if(currentMode!=newStatus &&  newStatus == DeviceOperationMode.EMERGENCY) {
                FXMLLoader loader= ViewFactory.getSpecialModeScreen();
                loader.setControllerFactory(c -> new SpecialModeController(borderPane, stackPane, agent, stationData, StationMode.EMERGENCY));
                try {
                    stackPane.getChildren().clear();
                    stackPane.getChildren().add(loader.load());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else if(currentMode!=newStatus &&  newStatus == DeviceOperationMode.STATION_CLOSE) {
                FXMLLoader loader= ViewFactory.getSpecialModeScreen();
                loader.setControllerFactory(c -> new SpecialModeController(borderPane, stackPane, agent, stationData,StationMode.STATION_CLOSED));
                try {
                    stackPane.getChildren().clear();
                    stackPane.getChildren().add(loader.load());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else if(currentMode!=newStatus &&  newStatus == DeviceOperationMode.OUT_OF_SERVICE) {
                FXMLLoader loader= ViewFactory.getSpecialModeScreen();
                loader.setControllerFactory(c -> new SpecialModeController(borderPane, stackPane, agent, stationData,StationMode.OUT_OF_SERVICE));
                try {
                    stackPane.getChildren().clear();
                    stackPane.getChildren().add(loader.load());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(currentMode!=newStatus &&  newStatus == DeviceOperationMode.MAINTENANCE) {
                FXMLLoader loader= ViewFactory.getSpecialModeScreen();
                loader.setControllerFactory(c -> new SpecialModeController(borderPane, stackPane, agent, stationData,StationMode.MAINTENANCE));
                try {
                    stackPane.getChildren().clear();
                    stackPane.getChildren().add(loader.load());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            currentMode = newStatus;

        });
    }



    public void onClickSjtButton(ActionEvent actionEvent) {
        borderPane.setBottom(null);

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
        borderPane.setBottom(null);
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
        borderPane.setBottom(null);
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
    }

    public void onClickBalanceUpdate(ActionEvent actionEvent) {
    }

    public void onClickCardInquiry(ActionEvent actionEvent) {

    }
}
