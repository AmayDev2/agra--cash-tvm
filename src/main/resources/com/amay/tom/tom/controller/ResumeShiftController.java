package com.amay.tom.controller;

import com.amay.tom.service.siftservice.ShiftService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.util.concurrent.atomic.AtomicLong;

public class ResumeShiftController {


    @FXML
    private Text error;

    private ShiftService shiftService;


    AtomicLong timeCounter = new AtomicLong();

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Text currentStatus;

    @FXML
    private ImageView imageView;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button resumeShiftButton;

    @FXML
    private Image actualImage;
    @FXML
    private Text time;
    public ResumeShiftController(ShiftService shiftService) {
        this.shiftService=shiftService;
    }


        @FXML
        public void initialize() {

        }



    {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.seconds(1),
                        event -> {
                            Platform.runLater(() -> {
                                // Increment the time counter by 1000 milliseconds
                                timeCounter.addAndGet(1000);
                                // Calculate hours, minutes, and seconds
                                long hours = timeCounter.get() / (60 * 60 * 1000);
                                long minutes = (timeCounter.get() / (60 * 1000)) % 60;
                                long seconds = (timeCounter.get() / 1000) % 60;
                                // Update the time label
                                time.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));


                            });
                        }
                )
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE); // Run indefinitely
        timeline.play(); // Start the timeline
    }

//    @FXML
//    void onClickResumeShift(ActionEvent event) {
//        try {
//            if (passwordField.getText().equals(DBUserRepo.getInstance().readUserByUserId(SQLiteConnection.INSTANCE.getConnection(),
//                    SystemConfig.getInstance().getCurrentUser().getUserId()).getPassword())) {
//                shiftService.resumeShift();
//            }
//        } catch (SQLException e) {
//            Logger.error("Error in resuming shift {}",e.getMessage());
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }finally{
//            SQLiteConnection.INSTANCE.releaseConnection(null);
//        }
//
//    }

    @FXML
    void onClickResumeShift(ActionEvent event) {
            String password= passwordField.getText().trim();
            if (!password.isEmpty()) {
                shiftService.resumeShift(password);
            }
    }


    public void setError(String message) {
        error.setText(message);
    }
}
