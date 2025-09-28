package com.amay.tom.pdu.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.controller.components.StatusBottomBarView;
import com.amay.tom.exceptions.EmptyUsernameOrPasswordException;
import com.amay.tom.exceptions.UsernameNotFoundException;
import com.amay.tom.model.station.Station;
import com.amay.tom.pdu.controller.service.SceneManager;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.time.LocalDateTime;
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
    private SceneManager sceneManager;
    private BuzzerTask buzzerTask;

    public MaintenanceLogin(Agent agent, SceneManager sceneManager) {
        this.sceneManager=sceneManager;
        this.agent = agent;
        currentStation = SystemConfig.getInstance().getCurrentStation();
        this.shiftService=agent.getShiftService();
        this.buzzerTask=new BuzzerTask(this.agent.getThreadPool().getScheduler(), 30);
    }



    private Stage mainStage;

    boolean fl;

    @FXML
    private void initialize() {
        if(!fl)
        this.agent.getPeripheralMonitor().addDeviceStatusListener(new ListenDoreEvent(this, this.buzzerTask));
        fl=true;
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

        this.buzzerTask.cancelAndTurnOff();
        Logger.tag(LoggerTag.APP).debug("Loading Hoppers Screen");
        FXMLLoader fxmlLoader=ViewFactory.getHopper();
        CoinRagistoryPageController coinRagistoryPageController=new CoinRagistoryPageController(sceneManager);
        fxmlLoader.setControllerFactory((x)->coinRagistoryPageController);
        this.sceneManager.addToScene(fxmlLoader);

        Logger.tag(LoggerTag.APP).debug("Loaded Hoppers Screen !!!");
        event.consume();

//        mainStage = (Stage) messageLabel.getScene().getWindow();
//        this.shiftService.setMainStage(mainStage);
//        String userName= usernameField.getText().toUpperCase();
//        String userPass=passwordField.getText();
//        userName=userName.trim();
//        userPass=userPass.trim();
//
//        try {
////            if(!agent.getBusinessRule().isActiveWorkingHour())throw new RuntimeException("Login allowed during Business Hours only");
//
//            if(userName.isEmpty() || userPass.isEmpty()){
//                Platform.runLater(()->messageLabel.setText("Provide User ID or Password"));
//                throw new EmptyUsernameOrPasswordException("Missing User ID or Password");
//            }
//
//            FXMLLoader fxmlLoader = this.shiftService.startShift(userName, userPass);
//            this.agent.setShiftService(this.shiftService);
////        this.agent.getApplicationService().setShiftService(this.shiftService);
//            ShiftServiceListener shiftServiceListener = new ShiftServiceListener(this.shiftService);
//            this.agent.setInternalListener(shiftServiceListener);
//            this.agent.getApplicationService().addListener(shiftServiceListener);
//            mainStage.getScene().setRoot(fxmlLoader.load());
//        } catch (Exception e) {
//            // Optional: Check if it's a wrapped UsernameNotFoundException
//            if (e.getCause() instanceof UsernameNotFoundException cause) {
//                Platform.runLater(() -> messageLabel.setText(cause.getMessage()));
//            } else {
//                Platform.runLater(() -> messageLabel.setText(e.getMessage()));
//            }
//        }finally {
//            event.consume();
//        }
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
                    this.task.cancelAndTurnOff();
                }

            }
        }
    }

}
