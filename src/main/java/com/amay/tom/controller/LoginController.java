package com.amay.tom.controller;


import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.exceptions.EmptyUsernameOrPasswordException;
import com.amay.tom.exceptions.UsernameNotFoundException;
import com.amay.tom.pdu.controller.command.PDUCommandDispatcher;
import com.amay.tom.pdu.controller.command.PassAgentCommand;
import com.amay.tom.repository.session.ShiftRepositoryImpl;
import com.amay.tom.repository.user.UserRepositoryImpl;
import com.amay.tom.service.siftservice.ShiftService;
import com.amay.tom.service.siftservice.ShiftServiceListener;
import com.amay.tom.service.siftservice.SiftService;
import com.amay.tom.service.siftservice.impl.ShiftServiceImpl;
import com.amay.tom.service.userauth.UserAuth;
import com.amay.tom.service.userauth.UserDetailsService;
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
    @FXML private VBox vbox;
    @FXML private Label equipmentId;
    @FXML private Label csn;

    @FXML private Label timeLabel;
    @FXML private Label dateLabel;
    @FXML private AnchorPane anchorPane;
    //    @FXML
//    private  Button loginButton;
    @FXML
    private ImageView logo;
//
//    @FXML
//    private Text messageLabel;

//    @FXML
//    private PasswordField passwordField;
//
//    @FXML
//    private TextField usernameField;

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
                                        agent.getConnection()))),new ShiftRepositoryImpl(agent.getConnection()));

        this.agent.setShiftService(this.shiftService);

        //TODO> Pass shiftService to maintenance
        PDUCommandDispatcher.INSTANCE.dispatch(new PassAgentCommand(this.agent));
    }

    private Stage mainStage;

    @FXML
    private void initialize() {

        setEquipmentDetails();

        // check for last shift completion in another thread so that UI is not blocked
        this.agent.getThreadPool().getFixedThreadPool().execute(()->
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

//                       checkForWorkingHours();
                        }
                )
        );

        boolean isWeekDay=this.agent.getBusinessRule().getToday().getDayType().equals("WEEKDAYS");
        this.updateDateTime(isWeekDay);
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
                            if (agent.getBusinessRule().isActiveWorkingHour()) {
                                loginButtonClicked();
                                // Stop the timeline once condition is met
                                timeline.stop();
                                timeline=null;
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
        mainStage = (Stage) anchorPane.getScene().getWindow();

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
