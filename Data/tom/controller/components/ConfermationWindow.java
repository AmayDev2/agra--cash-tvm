package com.amay.tom.controller.components;

import com.amay.tom.maintenance.service.component.MyOperation;
import com.amay.tom.maintenance.service.component.StatusWindowPopup;
import com.amay.tom.maintenance.service.component.model.StatusWindowModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class ConfermationWindow {
    @FXML
    private Text titleLabel;
    @FXML
    private Button noButton;
    @FXML
    private Button yesButton;

    private String text;

    private final StatusWindowPopup statusWindowPopup;

    private final MyOperation myOperation;

    public ConfermationWindow(MyOperation myOperation, StatusWindowModel text, StatusWindowPopup statusWindowPopup) {
        this.text = text.getPermission();
        this.myOperation = myOperation;
        this.statusWindowPopup = statusWindowPopup;

    }

    @FXML
    void initialize() {
        titleLabel.setText(text);
        noButton.setOnAction(actionEvent -> statusWindowPopup.Close());
        yesButton.setOnAction(actionEvent -> myOperation.operate());
    }




}
