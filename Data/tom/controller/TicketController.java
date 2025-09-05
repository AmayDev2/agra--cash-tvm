package com.amay.tom.controller;

import com.amay.tom.model.QRTicket;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class TicketController {

    @FXML
    private Text expireTime;

    @FXML
    private Text expireTimeT;

    @FXML
    private Text fareMode;

    @FXML
    private Text fareModeT;

    @FXML
    private Text from;

    @FXML
    private Text fromT;

    @FXML
    private Text issueTime;

    @FXML
    private Text issueTimeT;

    @FXML
    private Text price;

    @FXML
    private Text priceT;

    @FXML
    private ImageView qrCode;

    @FXML
    private VBox ticketFormat;

    @FXML
    private Text ticketNo;

    @FXML
    private Text ticketNoT;

    @FXML
    private Text to;

    @FXML
    private Text toT;

    @FXML
    private Text type;

    @FXML
    private Text typeT;

    @FXML
    private Text qty;

    @FXML
    void initialize() {
        qrCode.setImage(new Image("file:amayalogo.png"));

    }

//    public static VBox getTicketDetails() {
//        return ticketFormat;
//    }

    public void setTicketDetails(QRTicket qrTicket) {
           ticketNo.setText(qrTicket.getTicketNo());
//           ticketNoT.setText(qrTicket.getTicketNo());

            issueTime.setText(qrTicket.getInitiateDateTime());
//            issueTimeT.setText(qrTicket.getInitiateDateTime());

            expireTime.setText(qrTicket.getExpiryTime());
//            expireTimeT.setText(qrTicket.getExpiryTime());

            from.setText(qrTicket.getFrom());
//            fromT.setText(qrTicket.getFrom());

            to.setText(qrTicket.getTo());
//            toT.setText(qrTicket.getTo());

            type.setText(qrTicket.getType());
//            typeT.setText(qrTicket.getType());

            fareMode.setText(qrTicket.getFareMode());
//            fareModeT.setText(qrTicket.getFareMode());

            price.setText(qrTicket.getPrice());
//            priceT.setText(qrTicket.getPrice());

            qty.setText(String.valueOf(qrTicket.getQty()));


            qrCode.setImage(qrTicket.getQrCode());

    }


}
