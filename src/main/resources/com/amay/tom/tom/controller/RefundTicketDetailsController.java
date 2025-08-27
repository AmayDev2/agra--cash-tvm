package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.exceptions.NotRefundableOnDifferentStationException;
import com.amay.tom.exceptions.TicketAlreadyRefunded;
import com.amay.tom.exceptions.TicketCouldNotRefunded;
import com.amay.tom.grpc.scugrpc.ScuDataMapper;
import com.amay.tom.grpc.scugrpc.ScuService;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.refund.RefundDTO;
import com.amay.tom.model.refund.RefundMapper;
import com.amay.tom.repository.StationData;
import com.amay.tom.service.qrService2.RefundQRService;
import com.amay.tom.service.qrService2.impl.RefundQRServiceImpl;
import com.amay.tom.utils.folder.NewFolder;
import com.amay.tom.utils.objects.RefundValidationResponse;
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
import java.time.LocalDateTime;
import java.util.UUID;

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
    private boolean pushedToCCU;
    private boolean pushedToSCU;
    
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
        type.setText(TicketType.getTicket(ticket.getType()).getTicketTypeName());
        dateTime.setText(TimeUtil.epochMilliToFormattedSystemTime(ticket.getInitiateDateTime(),null));
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
            RefundValidationResponse refundValidationResponse = processRefund();
            String status=refundValidationResponse.getMessage();
            
            if (refundValidationResponse.isValid()) {
                // Print refund receipt
                this.printRefundReceipt();
                    FXMLLoader fxmlLoader = ViewFactory.getSuccessPage();
                    fxmlLoader.setControllerFactory((x)->new SuccessController(status, refundValidationResponse.isValid()));
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

    public RefundDTO getRefundDetails(String ticketNumber) {
        try {
            return RefundMapper.toDTO(this.agent.getRefundTicketRepository().findByTicketNumber(ticketNumber));
        } catch (Exception e) {
            Logger.error("Error getting refund details: {}", e.getMessage());
            return null;
        }
    }
    
    private RefundValidationResponse  processRefund() {
            RefundValidationResponse refundValidationResponse=null;
            boolean isValid = false;
            String message ="";
        try {
            if (!this.ticket.getFrom().equals(this.agent.getSystemConfig().getCurrentStation().getStationId())) {
                throw new NotRefundableOnDifferentStationException("Ticket is not from current station: " + this.agent.getSystemConfig().getCurrentStation()+" "+this.ticket.getFrom());
            }

            RefundDTO refundDTO = getRefundDetails(this.ticket.getTicketNo());
            if (refundDTO != null) {
                throw new TicketAlreadyRefunded("Ticket with Number: " + this.ticket.getTicketNo() + " is already refunded.");
            }

            String refundId = UUID.randomUUID().toString();
            TicketRefundRequestV1 ticketRequestV1 = ScuDataMapper.createTicketRefundRequestByNumber(this.ticket.getTicketNo(), "CASH",(int)mAmount,this.ticket.getType(),refundId);
            TicketRefundResponseV2 ticketRefundResponseV2;

            if (isCCU) {
                ticketRefundResponseV2 = ccuService.refundTicket(ticketRequestV1);
                if (ticketRefundResponseV2.getResponseMetaData().getErrorCode().equals("200")) {
                    isValid = true;
                    pushedToCCU = true;
                    message = "Refund Successful";
                }
                try {
                    ticketRefundResponseV2 = scuService.refundTicket(ticketRequestV1);
                    if (ticketRefundResponseV2.getResponseMetaData().getErrorCode().equals("200")) {
                        isValid = true;
                        pushedToSCU = true;
                        message = "Refund Successful";
                    }
                } catch (Exception e) {
                    Logger.error("Error pushing refund data to to SCU");
                }
            }
            else {  //TODO: Remove this, if this is for only CCU
                ticketRefundResponseV2 = scuService.refundTicket(ticketRequestV1);
                if(ticketRefundResponseV2.getResponseMetaData().getErrorCode().equals("200")){
                    isValid=true;
                    pushedToSCU=true;
                    message="Refund Successful";
                }
            }
            if (!ticketRefundResponseV2.getResponseMetaData().getErrorCode().equals("200")) {
                // failed
//                throw new TicketCouldNotRefunded("Not Found Ticket with Number: " + ticket.getTicketNo());
//                agent.getRefundTicketRepository().updateRefundStatus(refundId,"FAIL");
                message="Refund failed";
            }
//            else{
//                isValid=true;
////                agent.getRefundTicketRepository().updateRefundStatus(refundId,"SUCCESS");
//                message="Refund Successful";
//            }

            //

            // Check if the ticket is already refunded
            refundValidationResponse=new RefundValidationResponse(isValid, message);
            // Set the station ID from the agent's system config
            refundValidationResponse.setRefundAmount(String.valueOf(mAmount));


            //Local save of refund details
            Logger.debug("Refund Response: {}", ticketRefundResponseV2);
            Logger.debug(ticketRefundResponseV2.getResponseMetaData().getErrorMessage());
            refundDTO = new RefundDTO();
            refundDTO.setRefundMode(ticketRefundResponseV2.getTicketRefundData().getRefundInfo().getRefundMode());
            String ticketNumber = ticketRefundResponseV2.getTicketRefundData().getRefundInfo().getTicketId();

            refundDTO.setTicketNumber(ticketNumber);
            refundDTO.setAmount(mAmount);
            refundDTO.setShiftId(agent.getShift().getShiftId());
            refundDTO.setCreationDateTime(LocalDateTime.now());
            refundDTO.setUpdateDateTime(LocalDateTime.now());
            refundDTO.setOperatorId(agent.getShift().getOperatorId());
            refundDTO.setDeviceId(agent.getShift().getDeviceId());
            refundDTO.setTicketType(ticket.getType());
            refundDTO.setRefundId(refundId);
            if(isValid){
                refundDTO.setStatus("SUCCESS");
            }else {
                refundDTO.setStatus("FAIL");
            }
            if(pushedToCCU){
                refundDTO.setCcu(true);
            }
            if (pushedToSCU){
                refundDTO.setScu(true);
            }
            RefundQRService refundQRService = new RefundQRServiceImpl(agent);
            refundQRService.processRefund(refundDTO);
        } catch (RuntimeException e) {
            if( e instanceof TicketAlreadyRefunded) {
                refundValidationResponse=new RefundValidationResponse(false, e.getMessage());
            } else if (e instanceof TicketCouldNotRefunded) {
                refundValidationResponse= new RefundValidationResponse(false, e.getMessage());
            } else if( e instanceof NotRefundableOnDifferentStationException) {
                refundValidationResponse = new RefundValidationResponse(false, e.getMessage());
            }
            else {
                Logger.error("Error processing refund: {}", e.getMessage());
                refundValidationResponse = new RefundValidationResponse(false, "An error occurred while processing the refund: " + e.getMessage());
            }

        }
        return refundValidationResponse;  // Return true if refund was successful
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