package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.database.SQLiteConnection;
import com.amay.tom.model.siftdata.ShiftHeader;
import com.amay.tom.model.siftdata.User;
import com.amay.tom.repository.DBUserRepo;
import com.amay.tom.service.loginService.Authentication;
import com.amay.tom.service.loginService.impl.ImplAuthentication;
import com.amay.tom.service.siftservice.SiftService;
import com.amay.tom.service.siftservice.impl.ImplSiftService;
import com.amay.tom.utils.env.EnvFile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.tinylog.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class PreLoginViewController {

    private static final long SHIFT_TIME = 3;

    @FXML
    public Text shiftStartTime;

    @FXML
    private Text shiftDuration;
    Authentication authentication;

    private SiftService shiftService;

    @FXML
    private AnchorPane anchor;

    @FXML
    private Text currentStatus;

    @FXML
    private Text deviceId;

    @FXML
    private Text operatorId;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text operatorName;

    @FXML
    private Button shiftEnd;

    @FXML
    private Text shiftId;

    @FXML
    private Button shiftNew;

    @FXML
    private Button shiftResume;

    @FXML
    void initialize() {
        authentication = new ImplAuthentication();
        shiftService= ImplSiftService.INSTANCE;
        try {
            User user = DBUserRepo.getInstance().readUserByUserId(SQLiteConnection.INSTANCE.getConnection(), ShiftHeader.getInstance().getOperatorId());
            operatorId.setText(user.getUserId());
            operatorName.setText(user.getUserId());
            deviceId.setText(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId());
            shiftId.setText(String.valueOf(ShiftHeader.getInstance().getShiftId()));
            shiftStartTime.setText(ShiftHeader.getInstance().getShiftStart());

        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    public void onClickResume(ActionEvent actionEvent){
        try {
            User user = DBUserRepo.getInstance().readUserByUserId(SQLiteConnection.INSTANCE.getConnection(), ShiftHeader.getInstance().getOperatorId());

            if (authentication.resumeShutDownShift(passwordField.getText())) {

                SystemConfig.getInstance().setCurrentUser(user);
//                shiftService.startShift(user);
                this.showHomeScene(actionEvent);
//                PeripheralMonitor peripheralMonitor = new PeripheralMonitor();
//                ImpDeviceStatusListener impDeviceStatusListener = new ImpDeviceStatusListener();
//                peripheralMonitor.addDeviceStatusListener(impDeviceStatusListener);
//                peripheralMonitor.startMonitoring();
                final int SHIFT_TIME = EnvFile.getShiftTimePeriod();

                // Schedule a task to get shift complete
                Logger.info("Shift time period: {}", SHIFT_TIME);


//                Executors.newSingleThreadScheduledExecutor().schedule(() -> {
//
//                    Logger.info("Shift is scheduled  for {} minutes", SHIFT_TIME);
//
//                    Platform.runLater(() -> {
//                        // Get the current stage
//                        Stage stage = (Stage) Stage.getWindows().stream()
//                                .filter(Window::isShowing)
//                                .findFirst()
//                                .orElse(null);
//
//                        if (stage != null) {
//                            try {
//                            shiftService.endOfShift(EOSType.TIME_OUT); // End the shift
//
//                                FXMLLoader fxmlLoader = ViewFactory.getLogin();
//                                Parent root = fxmlLoader.load();
//
//                                stage.getScene().setRoot(root);
//
//                                stage.show();
//
//                            } catch (IOException e) {
//                                Logger.error("Error loading login scene: {}", e.getMessage());
//                            }
//                        } else {
//                            Logger.error("Error: No active stage found");
//                        }
//                    });
//
//                    Logger.info("Shift completed");
//                }, SHIFT_TIME, TimeUnit.MINUTES);
            } else {
                currentStatus.setText("Invalid Password");
            }
        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void showHomeScene( ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = ViewFactory.getHome();

        try {
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            Logger.error("Error loading home scene: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public void onClickEOS(ActionEvent actionEvent) {
    }

    public void onClickNewShift(ActionEvent actionEvent) {

        try{
            FXMLLoader fxmlLoader = ViewFactory.getLoginScreen();
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) Stage.getWindows().stream()
                    .filter(Window::isShowing)
                    .findFirst()
                    .orElse(null);

            assert stage != null;
            stage.getScene().setRoot(root);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
