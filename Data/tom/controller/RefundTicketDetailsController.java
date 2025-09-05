package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.grpc.scugrpc.ScuDataMapper;
import com.amay.tom.grpc.scugrpc.ScuService;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.refund.RefundDTO;
import com.amay.tom.repository.StationData;
import com.amay.tom.service.qrService2.*;
import com.amay.tom.service.qrService2.impl.RefundQRServiceImpl;
import com.amay.tom.utils.folder.NewFolder;
import com.amay.tom.utils.time.TimeUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.amaytechnosystems.TicketRefundRequestV1;
import org.amaytechnosystems.TicketRefundResponseV2;
import org.tinylog.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;

public class RefundTicketDetailsController {
    @FXML
    private Text paymentType;
    @FXML
    private Text description;
//    @FXML
//    private Text status;
    @FXML
    private Text ticketNumber;

    @FXML
    private Text source;
    @FXML
    private Text destination;
    @FXML
    private Text fare;
    @FXML
    private Text type;
    @FXML
    private Text dateTime;
    @FXML
    private Text validUntil;
    @FXML
    private ImageView qrCodeImage;
    @FXML
    private Button refundButton;
    
    private BorderPane borderPane;
    private Agent agent;
    private QRTicket ticket;
    private ScuService scuService,ccuService;
    private String mDescription;
    private boolean mStatus;
    private double mAmount;
    private boolean isCCU;
    
    public RefundTicketDetailsController(BorderPane borderPane,
                                         Agent agent,
                                         QRTicket ticket,
                                         String description,
                                         boolean status, double amount, boolean isCCU) {
        this.borderPane = borderPane;
        this.agent = agent;
        this.ticket = ticket;
        this.scuService=agent.getScuService();
        this.ccuService=agent.getCcuService();
        this.mStatus=status;
        this.mDescription=description;
        this.mAmount=amount;
        this.isCCU=isCCU;
    }
    
    @FXML
    private void initialize() {
        Logger.info("Refund Ticket Details scene loaded");
        displayTicketDetails();
        refundButton.setDisable(!mStatus);
    }
    
    private void displayTicketDetails() {
        ticketNumber.setText(ticket.getTicketNo());
        paymentType.setText(ticket.getFareMode());
        source.setText(StationData.getInstance().getStation(ticket.getFrom()).getStationName());
        destination.setText(StationData.getInstance().getStation(ticket.getTo()).getStationName());
        fare.setText(ticket.getPrice());
        type.setText(ticket.getType());
        dateTime.setText(TimeUtil.epochMilliToFormattedSystemTime(ticket.getInitiateDateTime(),null));
//        validUntil.setText(ticket.getExpiryTime());
        description.setText(mDescription);

        
        // Load QR code image
        try {
            BufferedImage qrBufferedImage = NewFolder.findTicket(ticket.getTicketNo());
            if (qrBufferedImage != null) {
                Image qrImage = javafx.embed.swing.SwingFXUtils.toFXImage(qrBufferedImage, null);
                qrCodeImage.setImage(qrImage);
            }
        } catch (Exception e) {
            Logger.error("Error loading QR code image: {}", e.getMessage());
        }
    }
    
    @FXML
    private void onRefundClick() {
        try {
            // Process refund
            boolean refundSuccess = processRefund();
            String status="Refunded Successfully";
            
            if (refundSuccess) {
//                showAlert(Alert.AlertType.INFORMATION, "Refund Successful",
//                         "The ticket has been successfully refunded.");

                // Print refund receipt
                this.printRefundReceipt();

                    FXMLLoader fxmlLoader = ViewFactory.getSuccessPage();
                    fxmlLoader.setControllerFactory((x)->new SuccessController(status, refundSuccess));
                    borderPane.setCenter(fxmlLoader.load());

            } else {

                showAlert(Alert.AlertType.ERROR, "Refund Failed", 
                         "Failed to process the refund. Please try again.");
            }
        } catch (Exception e) {
            Logger.error("Error processing refund: {}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "An error occurred while processing the refund.");
        }



    }
    
    private boolean processRefund() {

        TicketRefundRequestV1 ticketRequestV1= ScuDataMapper.createTicketRefundRequestByNumber(this.ticket.getTicketNo(),"CASH");
        TicketRefundResponseV2 ticketRefundResponseV2;

        if(isCCU) {
            ticketRefundResponseV2 = ccuService.refundTicket(ticketRequestV1);
        }else{  //TODO: Remove this, if this is for only CCU
            ticketRefundResponseV2 = scuService.refundTicket(ticketRequestV1);
        }

        System.out.println(ticketRefundResponseV2.getResponseMetaData().getErrorMessage());
        RefundDTO refundDTO =new RefundDTO();
        refundDTO.setRefundMode(ticketRefundResponseV2.getTicketRefundData().getRefundInfo().getRefundMode());
        refundDTO.setTicketNumber(ticketRefundResponseV2.getTicketRefundData().getRefundInfo().getTicketNumber());
        refundDTO.setAmount(mAmount);
        refundDTO.setShiftId(agent.getShift().getShiftId());
        refundDTO.setCreationDateTime(LocalDateTime.now());
        refundDTO.setUpdateDateTime(LocalDateTime.now());
        refundDTO.setOperatorId(agent.getShift().getOperatorId());
        refundDTO.setDeviceId(agent.getShift().getDeviceId());

        RefundQRService refundQRService= new RefundQRServiceImpl(agent);
        refundQRService.processRefund(refundDTO);
        
        return ticketRefundResponseV2.getResponseMetaData().getErrorCode().equals("200");
    }
    
    private void printRefundReceipt() {
        try {
            // TODO: Implement refund receipt printing
            // This should generate and print a receipt for the refund
            Logger.info("Printing refund receipt for ticket: {}", ticket.getTicketNo());
        } catch (Exception e) {
            Logger.error("Error printing refund receipt: {}", e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
} 