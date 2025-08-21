package com.amay.tom.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SuccessController {
    @FXML private Label statusText;
    private boolean isSuccess;
    private String status;

    public SuccessController(String replacedSuccessfully, boolean b) {
        this.status=replacedSuccessfully;
        this.isSuccess=b;
    }

    @FXML void initialize(){
        statusText.setText(status);
    }
}
