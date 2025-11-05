package com.amay.tom.pdu.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.controller.components.StatusBottomBarView;
import com.amay.tom.exceptions.EmptyUsernameOrPasswordException;
import com.amay.tom.exceptions.UsernameNotFoundException;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.session.ShiftMapper;
import com.amay.tom.model.station.Station;
import com.amay.tom.pdu.MaintenanceController;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tom.repository.session.ShiftRepository;
import com.amay.tom.repository.session.ShiftRepositoryImpl;
import com.amay.tom.repository.user.UserRepositoryImpl;
import com.amay.tom.service.devices.DeviceStatusListener;
import com.amay.tom.service.siftservice.ShiftService;
import com.amay.tom.service.siftservice.ShiftServiceListener;
import com.amay.tom.service.siftservice.SiftService;
import com.amay.tom.service.siftservice.impl.ShiftServiceImpl;
import com.amay.tom.service.userauth.UserAuth;
import com.amay.tom.service.userauth.UserDetailsService;
import com.amay.tom.utils.tasks.BuzzerTask;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.coin.CoinModuleInterface;
import com.amay.tvm.controller.CoinRagistoryPageController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.amaytechnosystems.ShiftStatus;
import org.tinylog.Logger;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MaintenanceLogin {
    @FXML
    private Button login;
    @FXML
    private HBox peripheralStatusHBox;


    @FXML
    private ImageView logo;

    @FXML
    private Text messageLabel;   //TODO: length of message to fix

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;
//
//    @FXML
//    protected Label dateLabel;
//
//    @FXML
//    protected Label timeLabel;
//
//    @FXML
//    private Label equipmentId;
//
//    @FXML
//    private Label csn;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private SiftService siftService;
    private int timePeriod;
    private final Agent agent;
    private final ShiftService shiftService;
    private Station currentStation = null;
    private final SceneManager sceneManager;
    private final BuzzerTask buzzerTask;

    public MaintenanceLogin(Agent agent, SceneManager sceneManager) {
        this.sceneManager=sceneManager;
        this.agent = agent;
        currentStation = SystemConfig.getInstance().getCurrentStation();
        this.shiftService=agent.getShiftService();
        this.buzzerTask=new BuzzerTask(this.agent.getThreadPool().getScheduler(), 30);
    }



    boolean fl;

    @FXML
    private void initialize() {
        usernameField.setText("UPMRC");

        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 5) {
                // revert to the previous valid text
                usernameField.setText(oldValue);
            }
        });

        if(!fl){
        this.agent.getPeripheralMonitor().addDeviceStatusListener(new ListenDoreEvent(this, this.buzzerTask));
        fl=true;}
        updateDateTime();
////        setPeripheralStatus();
//        // check for last shift completion in another thread so that UI is not blocked
//        this.agent.getThreadPool().getFixedThreadPool().execute(()->
//                Platform.runLater(() ->
//                        this.shiftService.checkLastShiftCompletion().ifPresentOrElse(
//                                shift -> {
//                                    // Trigger your event or logic here
//                                    this.shiftService.markLastShiftAsCompleted(shift);
//
//                                    Alert alert = new Alert(Alert.AlertType.WARNING);
//                                    alert.setTitle("Warning");
//                                    alert.setHeaderText("Last Shift was not Logged out correctly!");
//                                    alert.setContentText("Do you want to print the End of Shift Report of last shift?");
//
//                                    ButtonType continueButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
//
//
//                                    alert.getButtonTypes().setAll(continueButton);
//
//                                    Optional<ButtonType> result = alert.showAndWait();
//
//                                    if (result.isPresent() && result.get() == continueButton) {
//                                        Logger.info("Continue clicked");
//                                        this.shiftService.printEOSReport(shift);
//                                    } else {
//                                        Logger.info("Cancelled or closed");
//                                    }
//
//                                },
//                                () -> {
//                                    Logger.info("No last shift found or it is already completed.");
//                                }
//                        ))
//        );
    }

    @FXML
    void loginButtonClicked(ActionEvent event) {
        try {
            this.buzzerTask.cancelAndTurnOff();
        } catch (Exception e) {
            Logger.error("Error stopping buzzer: {}", e.getMessage());
        }

        try {
            FXMLLoader fxmlLoader = agent.getShiftService().startMaintenanceShift(usernameField.getText(), passwordField.getText(), sceneManager);
            if (fxmlLoader != null) {
                sceneManager.addToScene(fxmlLoader);
            }
            event.consume();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

        void updateDateTime() {
//        javafx.animation.Timeline timeline = new javafx.animation.Timeline(new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), event -> {
//            LocalDateTime now = LocalDateTime.now();
//            Platform.runLater(() -> {
//                timeLabel.setText(timeFormatter.format(now));
//                dateLabel.setText(dateFormatter.format(now));
//            });
//        }));
//        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE); // Run indefinitely
//        timeline.play(); // Start the timeline
//
//        equipmentId.setText(SystemConfig.getInstance().getCurrentEquipment().getEquipmentName());
//        csn.setText(currentStation.getStationName());
    }

//    private void setPeripheralStatus() {
//        FXMLLoader fxmlLoader = ViewFactory.getBottomNav();
//        fxmlLoader.setControllerFactory(x->new StatusBottomBarView(agent.getPeripheralMonitor(),agent.getVersions(),agent.getMasterConfigInfo()));
//        try {
//            peripheralStatusHBox.getChildren().clear();
//            peripheralStatusHBox.getChildren().add(fxmlLoader.load());
//        } catch (Exception e) {
//            Logger.error("Error loading peripheral status view: {}", e.getMessage());
//        }
//    }



    public void handleEnterPress(KeyEvent event) throws Exception {
        if(event.getCode()== KeyCode.ENTER){
            loginButtonClicked(new ActionEvent());
        }
    }

  static class ListenDoreEvent implements DeviceStatusListener {
     private final BuzzerTask task;
     private int[] mDeviceStatus;
     MaintenanceLogin controller;
        public ListenDoreEvent(MaintenanceLogin pduController,BuzzerTask task){
            this.task=task;
            this.controller=pduController;
        }


        @Override
        public void onDeviceStatusChanged(int[] deviceStatus) {
            int index ;
            for(index=0; null!=this.mDeviceStatus && index < deviceStatus.length; index++){
                if(this.mDeviceStatus[index] != deviceStatus[index])
                    break;
            }

            if(null==this.mDeviceStatus || index != deviceStatus.length){
                this.mDeviceStatus= deviceStatus;
                if(deviceStatus[0]==0) {
                    // shift focus to usernameField
                    Platform.runLater(() -> {
                        if (controller.usernameField != null) {
                            controller.usernameField.getScene().getWindow().requestFocus();
                            controller.usernameField.requestFocus();
//                            controller.login.fire();
                        }
                    });

                    this.task.start();

                }else{
//                    this.task.cancelAndTurnOff();
                }

            }
        }
    }

}
