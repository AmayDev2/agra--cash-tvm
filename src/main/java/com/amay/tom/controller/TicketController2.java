package com.amay.tom.controller;

import com.amay.tom.model.QRTicket;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class TicketController2 {

    @FXML
    private Text ticketID;
    @FXML
    private Text deviceID;
    @FXML
    private Text fare;
    @FXML
    private Text ticketQnt;
    @FXML
    private Text ticketType;
    @FXML
    private Text destination;
    @FXML
    private Text source;
    @FXML
    private Text expDateTime;
    @FXML
    private Text issueDateTime;
    @FXML
    private ImageView qrData;
    @FXML
    private ImageView logo;
    private final QRTicket qrTicket;

    public TicketController2(QRTicket qrTicket) {
        this.qrTicket = qrTicket;
        //System.out.println("TicketController2.TicketController2");
    }

    @FXML
    private void initialize() {
        //System.out.println("TicketController2.initialize");
        logo.setImage(new Image("file:E:\\Amay Technosystems\\Tom\\amaylogo.png"));


        //**********************************************
        //System.out.println("TicketController2.setData");
        ticketID.setText(ticketID.getText().split(":")[0]+": "+qrTicket.getTicketNo());
        deviceID.setText(deviceID.getText().split(":")[0]+": "+qrTicket.getFareMode());
        fare.setText("₹ "+qrTicket.getPrice()+"/-");
        ticketQnt.setText(String.valueOf(qrTicket.getQty()));
        ticketType.setText(qrTicket.getType());
        destination.setText(qrTicket.getTo());
        source.setText(qrTicket.getFrom());
        expDateTime.setText(qrTicket.getExpiryTime());
        issueDateTime.setText(qrTicket.getInitiateDateTime());
        qrData.setImage(qrTicket.getQrCode());

    }

    public void setTicketDetails(QRTicket qrTicket){
        //System.out.println("TicketController2.setData");
        ticketID.setText(ticketID.getText().split(":")[0]+": "+qrTicket.getTicketNo());
        deviceID.setText(deviceID.getText().split(":")[0]+": "+qrTicket.getFareMode());
        fare.setText("₹ "+qrTicket.getPrice()+"/-");
        ticketQnt.setText(String.valueOf(qrTicket.getQty()));
        ticketType.setText(qrTicket.getType());
        destination.setText(qrTicket.getTo());
        source.setText(qrTicket.getFrom());
        expDateTime.setText(qrTicket.getExpiryTime());
        issueDateTime.setText(qrTicket.getInitiateDateTime());

    }
}
