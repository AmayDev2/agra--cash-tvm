package com.amay.tom.controller;

import com.amay.tom.service.siftservice.InternalListener;
import com.amay.tom.service.userauth.UserAuth;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class PauseEosSelectionViewController {



    @FXML
    private Text deviceId;

    @FXML
    private Text operatorId;

    @FXML
    private Text operatorName;

    @FXML
    private Button shiftEnd;

    @FXML
    private Text shiftId;

    @FXML
    private Button shiftPause;

    @FXML
    private Text currentStatus;

    private final InternalListener shiftServiceListener;
    UserAuth userAuth;

    public PauseEosSelectionViewController(InternalListener shiftServiceListener, UserAuth userAuth) {
        this.shiftServiceListener=shiftServiceListener;
        this.userAuth=userAuth;
    }



    @FXML
    void initialize() {
//        deviceId.setText();
//        shiftId.setText(String.valueOf(ShiftHeader.getInstance().getShiftId()));
//        operatorId.setText(SystemConfig.getInstance().getCurrentUser().getUserId());
//        operatorName.setText(userAuth.getCurrentUser().getUsername());
//        if(SystemConfig.getInstance().getDeviceCurrentStatus()!=null)
//        currentStatus.setText("IN SERVICE");
//        currentStatus.setStyle("-fx-fill: green");
    }

    @FXML
    void onClickEOS(ActionEvent event) {
        this.shiftServiceListener.EOShift();
    }

    @FXML
    void onClickPause(ActionEvent event) {
        this.shiftServiceListener.PauseShift();

    }
}
