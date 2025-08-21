package com.amay.tom.controller;

import com.amay.tom.config.SystemConfig;
import com.amay.tom.model.QRTicket;
import com.amay.tom.utils.env.EnvFile;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class AgaraTicketController {

    @FXML
    private Text  ticketName;
    @FXML
    private Text dateTimeText;

    @FXML
    private Text paymentTypeText;

    @FXML
    private Text salepointText;

    @FXML
    private Text ticketTypeText;

    @FXML
    private Text quantityText;

    @FXML
    private Text fromText;

    @FXML
    private Text toText;

    @FXML
    private Text priceText;

    @FXML
    private Text ticketNumberText;

    @FXML
    private Text validUptoText;

    @FXML
    private ImageView logoImage;

    @FXML
    private ImageView qrImage;

//    private final QRTicket qrTicket;

//    public AgaraTicketController(QRTicket qrTicket) {
//        this.qrTicket = qrTicket;
//        System.out.println("TicketController initialized with QRTicket");
//    }

    @FXML
    private void initialize() {
        ticketName.setText(EnvFile.getTicketName());
        logoImage.setImage(new Image("file:src/main/resources/images/indoor-bhopal-logo.png" ));
    }

    public void updateTicketData(QRTicket qrTicket) {
        if (qrTicket == null) {
            System.out.println("QRTicket is null. Skipping data population.");
            return;
        }
        System.out.println("Populating ticket data...");
        ticketNumberText.setText(qrTicket.getTicketNo());
        salepointText.setText(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId());
        paymentTypeText.setText(qrTicket.getFareMode());
        priceText.setText("₹ " + qrTicket.getPrice() + "/-");
        quantityText.setText(String.valueOf(qrTicket.getQty()));
        ticketTypeText.setText(qrTicket.getType());
        fromText.setText(qrTicket.getFrom());
        toText.setText(qrTicket.getTo());
//        validUptoText.setText(qrTicket.getExpiryTime());
        dateTimeText.setText(qrTicket.getInitiateDateTime());

        if (qrTicket.getQrCode() != null) {
            qrImage.setImage(qrTicket.getQrCode());
        }
    }
}
