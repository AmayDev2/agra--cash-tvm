package com.amay.tom.pdu.controller.command;

import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.StationMode;
import com.amay.tom.repository.StationData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class AbnormalStationModeController {

    @FXML private Label messageLabel;
    @FXML  private  Label label;
    @FXML  private ImageView imageView;

    private String header;
    private String message;
    private String imagePath;

    @FXML void initialize() {
        imageView.setImage(new Image("file:"+imagePath));
        label.setText(header);
        messageLabel.setText(message);
    }

    public AbnormalStationModeController(StationMode statinMode) {
        this.header =  statinMode.getLabel();
        this.message=statinMode.getMessage();
        this.imagePath=statinMode.getPath();
    }

}

