package com.amay.tom.controller;


import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.exceptions.EmptyUsernameOrPasswordException;
import com.amay.tom.exceptions.UsernameNotFoundException;
import com.amay.tom.grpc.monotoring.AppCloseCommand;
import com.amay.tom.grpc.monotoring.ShutdownCommand;
import com.amay.tom.pdu.controller.command.PDUCommandDispatcher;
import com.amay.tom.pdu.controller.command.PassAgentCommand;
import com.amay.tom.repository.session.ShiftRepositoryImpl;
import com.amay.tom.repository.user.UserRepositoryImpl;
import com.amay.tom.service.events.Remote;
import com.amay.tom.service.events.commands.EOSCommand;
import com.amay.tom.service.siftservice.ShiftService;
import com.amay.tom.service.siftservice.ShiftServiceListener;
import com.amay.tom.service.siftservice.SiftService;
import com.amay.tom.service.siftservice.impl.ShiftServiceImpl;
import com.amay.tom.service.userauth.UserAuth;
import com.amay.tom.service.userauth.UserDetailsService;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.backend.service.CashInventoryService;
import com.amay.tvm.backend.service.FinanceOperationService;
import com.amay.tvm.ups.UPS;
import com.amay.tvm.ups.exception.UPSCommunicationException;
import com.amay.tvm.ups.model.UPSResponse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LoginController {
    @FXML private Label csn1;
    @FXML private VBox vbox;
    @FXML private Label equipmentId;
    @FXML private Label csn;

    @FXML private Label timeLabel;
    @FXML private Label dateLabel;
    @FXML private AnchorPane anchorPane;
    @FXML
    private ImageView logo;

    private SiftService siftService;
    private int timePeriod;
    private final Agent agent;
    private final ShiftService shiftService;

    public LoginController(Agent agent) {
        this.agent = agent;
        this.shiftService=new ShiftServiceImpl(agent,
                new UserAuth(
                        new UserDetailsService(
                                new UserRepositoryImpl(
                                        agent.getConnection()))),new ShiftRepositoryImpl(agent.getConnection()),
                                            new CashInventoryService(agent.getAmountSnapShotRepository(),agent.getTvmConfig()),
                                                new FinanceOperationService(agent.getFinanceOperationRepository(), agent.getTvmConfig()));
        this.agent.setShiftService(this.shiftService);

        //TODO> Pass shiftService to maintenance
        PDUCommandDispatcher.INSTANCE.dispatch(new PassAgentCommand(this.agent));
    }

    @FXML
    private void initialize() {

        setEquipmentDetails();

        // check for last shift completion in another thread so that UI is not blocked
        markPendingShiftCompleted(); // 1st
        markPendingShiftCompleted(); // 2nd

        boolean isWeekDay=this.agent.getBusinessRule().getToday().getDayType().equals("WEEKDAYS");
        this.updateDateTime(isWeekDay);
    }

    private void markPendingShiftCompleted() {
        this.agent.getThreadPool().getSingleThread().execute(()->
                        Platform.runLater(() ->{
                                    this.shiftService.checkLastShiftCompletion().ifPresentOrElse(
                                            shift -> {
                                                // Trigger your event or logic here
                                                this.shiftService.markLastShiftAsCompleted(shift);
//                                    this.shiftService.printEOSReport(shift);

                                            },
                                            () -> {
                                                Logger.info("No last shift found or it is already completed.");
                                            }
                                    );

                                }
                        )
        );
    }

    private void setEquipmentDetails() {
        if (SystemConfig.getInstance().getCurrentEquipment() != null) {
                equipmentId.setText(SystemConfig.getInstance().getCurrentEquipment().getEquipmentName());
                csn.setText(SystemConfig.getInstance().getCurrentStation().getStationName());
        } else {
            Logger.warn("Current equipment is not set in SystemConfig.");
        }
    }

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private javafx.animation.Timeline timeline;

    private void updateDateTime(boolean isWeekDay) {
         timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), event -> {
                    // Use system default timezone
                    ZonedDateTime nowZoned = ZonedDateTime.now(ZoneId.systemDefault());
                    LocalDateTime now = nowZoned.toLocalDateTime();
                    Platform.runLater(() -> {
                        timeLabel.setText(timeFormatter.format(now));
                        dateLabel.setText(dateFormatter.format(now));

                        if (now.getSecond() %5==0) {
                            // Update the clock icon or other features if needed
                            if (agent.getBusinessRule().isActiveWorkingHour() ) {
                                if( DeviceOperationMode.IN_SERVICE.equals(agent.getDeviceStatus().getCurrentStatus())
                                        && agent.getPeripheralMonitor().isUPSUP()
                                ) {
                                    loginButtonClicked();
                                    // Stop the timeline once condition is met
                                    timeline.stop();
                                    timeline = null;
                                }else {

                                    Logger.tag(LoggerTag.APP).info("Device not in IN_SERVICE mode {}",agent.getDeviceStatus().getCurrentStatus());
                                    csn1.setText("TVM is not under operational mode");
                                    //TODO: ON UPS ACTIVE-> SHUTDOWN TVM FOR SAFE HAND
                                    if(!agent.getPeripheralMonitor().isUPSUP()){
//                                        new Remote(new AppCloseCommand( agent.getApplicationService())).pressButton();
                                        new Remote(new ShutdownCommand( agent.getApplicationService())).pressButton();
                                    }
                                }
                            }
                        }
                    });
                })
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }


    private void checkForWorkingHours() {
        loginButtonClicked();
    }

    //    @FXML
    void loginButtonClicked() {
        Stage mainStage = (Stage) anchorPane.getScene().getWindow();

        this.shiftService.setMainStage(mainStage);
        String userName= null;//usernameField.getText();
        String userPass= null;//passwordField.getText();


        userName= "TVM"+SystemConfig.getInstance().getCurrentEquipment().getEquipmentId();
        userPass="tvm_user";


        try {
            if(!agent.getBusinessRule().isActiveWorkingHour())throw new RuntimeException("Not under Working Hours");

            FXMLLoader fxmlLoader = this.shiftService.startShift(userName, userPass);
            this.agent.setShiftService(this.shiftService);
//        this.agent.getApplicationService().setShiftService(this.shiftService);
            ShiftServiceListener shiftServiceListener = new ShiftServiceListener(this.shiftService);
            this.agent.setInternalListener(shiftServiceListener);
            this.agent.getApplicationService().addListener(shiftServiceListener);
            mainStage.getScene().setRoot(fxmlLoader.load());
        } catch (Exception e) {
            // Optional: Check if it's a wrapped UsernameNotFoundException
            e.printStackTrace();

        }



    }

    public void showHomeScene(Agent agent) {
    }




}
