package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.exceptions.EmptyUsernameOrPasswordException;
import com.amay.tom.exceptions.UsernameNotFoundException;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.util.Optional;

public class LoginController {
    @FXML
    private ImageView logo;

    @FXML
    private Text messageLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

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
    }

    private Stage mainStage;

    @FXML
    private void initialize() {
        // check for last shift completion in another thread so that UI is not blocked
        this.agent.getThreadPool().getFixedThreadPool().execute(()->
                Platform.runLater(() ->
                        this.shiftService.checkLastShiftCompletion().ifPresentOrElse(
                                shift -> {
                                    // Trigger your event or logic here
                                    this.shiftService.markLastShiftAsCompleted(shift);

                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("Shift Status");
                                    alert.setHeaderText("Last Shift Is Not Completed");
                                    alert.setContentText("Do you want to ?");

                                    ButtonType continueButton = new ButtonType("Continue", ButtonBar.ButtonData.OK_DONE);


                                    alert.getButtonTypes().setAll(continueButton);

                                    Optional<ButtonType> result = alert.showAndWait();

                                    if (result.isPresent() && result.get() == continueButton) {
                                        Logger.info("Continue clicked");
                                        this.shiftService.printEOSReport(shift);
                                    } else {
                                        Logger.info("Cancelled or closed");
                                    }

                                },
                                () -> {
                                    Logger.info("No last shift found or it is already completed.");
                                }
                        ))
        );
    }

    @FXML
    void loginButtonClicked(ActionEvent event) throws Exception {
        mainStage = (Stage) messageLabel.getScene().getWindow();
        mainStage.getScene().getStylesheets().add(getClass().getResource("/com/amay/tom/tvm/css/theme.css").toExternalForm());
        this.shiftService.setMainStage(mainStage);
        String userName= usernameField.getText();
        String userPass=passwordField.getText();
        userName=userName.trim();
        userPass=userPass.trim();

//        userName="tvm_user";
//        userPass="tvm_user";


        try {
            if(!agent.getBusinessRule().isActiveWorkingHour())throw new RuntimeException("Not under Working Hours");

            if(userName.isEmpty() || userPass.isEmpty()){
                Platform.runLater(()->messageLabel.setText("Provide Username or Password"));
                throw new EmptyUsernameOrPasswordException("Provide Username AND Password");
            }


            FXMLLoader fxmlLoader = this.shiftService.startShift(userName, userPass);
            this.agent.setShiftService(this.shiftService);
//        this.agent.getApplicationService().setShiftService(this.shiftService);
            ShiftServiceListener shiftServiceListener = new ShiftServiceListener(this.shiftService);
            this.agent.setInternalListener(shiftServiceListener);
            this.agent.getApplicationService().addListener(shiftServiceListener);
            mainStage.getScene().setRoot(fxmlLoader.load());
        } catch (Exception e) {
            // Optional: Check if it's a wrapped UsernameNotFoundException
            if (e.getCause() instanceof UsernameNotFoundException) {
                UsernameNotFoundException cause = (UsernameNotFoundException) e.getCause();
                Platform.runLater(() -> messageLabel.setText(cause.getMessage()));
            } else {
                Platform.runLater(() -> messageLabel.setText(e.getMessage()));
            }
            throw new RuntimeException(e);
        }finally {
            event.consume();
        }



    }

    public void showHomeScene(Agent agent) {
    }




}
