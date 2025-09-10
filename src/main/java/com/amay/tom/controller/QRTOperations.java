package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.TicketConfig;
import com.amay.tom.controller.RefundTicketDetailsController;
import com.amay.tom.controller.ReplacementTicketDetailsController;
import com.amay.tom.controllerInterface.controllerInt.ControllerAdapter;
import com.amay.tom.grpc.scugrpc.ScuDataMapper;
import com.amay.tom.grpc.scugrpc.ScuService;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.product.Product;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tom.service.analysis.TicketNumberFromQREventListener;
import com.amay.tom.service.chield.ReprintTicket;
import com.amay.tom.service.chield.ticketservice.ImplTicketService;
import com.amay.tom.service.qrDataGenerator.impl.ImplQRDataGenerator;
import com.amay.tom.service.qrReaderServiceTest.QrReader;
import com.amay.tom.utils.ticket.TicketUtil;
import com.amay.tom.utils.time.TimeUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.amaytechnosystems.TicketAnalysisResponseV1;
import org.amaytechnosystems.TicketRefundResponseV1;
import org.amaytechnosystems.TicketRequestV1;
import org.tinylog.Logger;

import java.io.IOException;

public class QRTOperations {

    @FXML
    private Text errorText;
    @FXML
    private AnchorPane anchor;
    @FXML
    private TextField inputTextField;
    @FXML
    private Button refundButton;
    @FXML
    private Button replacementButton;
//    @FXML
//    private Button cancelButton;
    private BorderPane borderPane;
    private Agent agent;

    private Alert alert;
    private ScuService scuService, ccuService;

    private final ReprintTicket reprintTicket;

    private TicketsRepository ticketsRepository;



    public QRTOperations(BorderPane borderPane, Agent agent) {
        this.borderPane = borderPane;
        this.agent = agent;
        this.scuService=agent.getScuService();
        this.ccuService=agent.getCcuService();
        this.reprintTicket = new ImplTicketService(new ImplQRDataGenerator());
        ticketsRepository= agent.getTicketsRepository();

    }

    @FXML
    private void initialize() {
        Logger.info("QRT Operations scene loaded");
        this.activeCaptureQR();
//        cancelButton.setDisable(!agent.getUserPrivilege().isQrTicketCancellation() || !agent.getEquipmentPrivilege().isQrTicketCancellation().get());
        replacementButton.setDisable(!agent.getUserPrivilege().isQrTicketReplacement() || !agent.getEquipmentPrivilege().isQrTicketReplacement().get());
        refundButton.setDisable(!agent.getUserPrivilege().isQrTicketRefund() || !agent.getEquipmentPrivilege().isQrTicketRefund().get());

        // Limit input to 20 characters
        inputTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 20) {
                inputTextField.setText(oldValue);
            }
        });
        alert = new Alert(Alert.AlertType.INFORMATION);

        agent.getEquipmentPrivilege().isQrTicketCancellation().addListener((observable, oldValue, newValue) -> {
//            cancelButton.setDisable(!agent.getUserPrivilege().isQrTicketCancellation() || !newValue);
        });

        agent.getEquipmentPrivilege().isQrTicketReplacement().addListener((observable, oldValue, newValue) -> {
            replacementButton.setDisable(!agent.getUserPrivilege().isQrTicketReplacement() || !newValue);
        });

        agent.getEquipmentPrivilege().isQrTicketRefund().addListener((observable, oldValue, newValue) -> {
            refundButton.setDisable(!agent.getUserPrivilege().isQrTicketRefund() || !newValue);
        });


    }

    private void activeCaptureQR() {

        QrReader qrReader = new QrReader(anchor);
        qrReader.addQrCodeListener(new TicketNumberFromQREventListener(new ImplQRDataGenerator(),inputTextField,errorText));
        qrReader.start();
    }

    public void onClickRefund(ActionEvent actionEvent) {
        try {
            onSearchClick(inputTextField.getText().toUpperCase().trim());
        } catch (Exception e) {
            Logger.error("Error in loading Refund Ticket Input View: {}", e.getMessage());
            e.printStackTrace();
        }
        actionEvent.consume();
    }

    public void onClickReplacement(ActionEvent actionEvent) {
        try {
            onClickConform(inputTextField.getText().toUpperCase().trim());
        } catch (Exception e) {
            Logger.error("Error in loading QRT Operations View: {}", e.getMessage());
            e.printStackTrace();
        }
        actionEvent.consume();
    }

    void onClickConform(String ticketNumber)  {

        if (ticketNumber.isEmpty()) {
            errorText.setText("Please enter a ticket number");
            return;
        }

        TicketsDto ticketsDto=ticketsRepository.findById(ticketNumber);

        if(ticketsDto==null){
            try {
                ticketsDto = getTicketByTicketNumber(ticketNumber);
            } catch (RuntimeException e) {
               Logger.error("Error fetching ticket by number: {}", e.getMessage());
                errorText.setText("Ticket Not Found");
                return;
            }
        }
        // check for today ticket
        if(!TicketUtil.isTodayTicket(ticketsDto.getIssueAt())){
            errorText.setText("Time has exceeded to replace.");
            return;
        }

        //check if ticket is active
        if(!ticketsDto.isActive()){
            errorText.setText("Ticket is irreplaceable");
            return;
        }

        //TODO: map ticket by model

        try{
            FXMLLoader loader = ViewFactory.getReplacementDetailsView();
            TicketsDto finalTicketsDto = ticketsDto;
            loader.setControllerFactory(x->new ReplacementTicketDetailsController(finalTicketsDto,borderPane,agent));
            Pane root = loader.load();
            ControllerAdapter.INSTANCE.setChildInCenterAnchorPane(root);
        } catch (IOException e) {
            //System.out.println(e.getMessage());
        }
    }



    //1- GET TICKET
    //2- CHECK IF TICKET IS Replaceable
        //1- IF JOURNEY IS NOT COMPLETED
        //2- IF TICKET IS  ACTIVE
    private TicketsDto getTicketByTicketNumber(String ticketNumber) throws RuntimeException {
        TicketsDto ticketDto=new TicketsDto() ;
        TicketRequestV1 ticketRequestV1 = ScuDataMapper.createTicketInfoRequestByNumber(ticketNumber);
        TicketAnalysisResponseV1 ticketRefundResponseV1;
        if (agent.getPeripheralMonitor().isCcu_connected()) {
            ticketRefundResponseV1 = ccuService.getTicketAnalysisByNumber(ticketRequestV1);
        } else {
            ticketRefundResponseV1 = scuService.getTicketAnalysisByNumber(ticketRequestV1);
        }

        if (!ticketRefundResponseV1.getResponseMetaData().getErrorCode().equals("200")) {
            errorText.setText("Ticket Not Found on " + (agent.getPeripheralMonitor().isCcu_connected() ? "CCU" : "SCU") + " server");
            throw new RuntimeException("Ticket Not Found on " + (agent.getPeripheralMonitor().isCcu_connected() ? "CCU" : "SCU") + " server");
        }

        ticketDto.setTicketId(ticketRefundResponseV1.getTicketAnalysis().getTicket().getTicketId());
        ticketDto.setIssueAt(Long.parseLong(ticketRefundResponseV1.getTicketAnalysis().getTicket().getTicketIssue()));
        ticketDto.setPaymentMode(ticketRefundResponseV1.getTicketAnalysis().getTicket().getPaymentMode());
        ticketDto.setAmount(ticketRefundResponseV1.getTicketAnalysis().getTicket().getAmount());
        ticketDto.setInStation(ticketRefundResponseV1.getTicketAnalysis().getTicket().getSourceStation());
        ticketDto.setOutStation(ticketRefundResponseV1.getTicketAnalysis().getTicket().getDestinationStation());
        ticketDto.setTicketType(ticketRefundResponseV1.getTicketAnalysis().getTicket().getProductId());
        ticketDto.setQrData(ticketRefundResponseV1.getTicketAnalysis().getTicket().getQrData());
        ticketDto.setActive(ticketRefundResponseV1.getTicketAnalysis().getTicket().getIsActive());
        ticketDto.setQuantity(ticketRefundResponseV1.getTicketAnalysis().getTicket().getQuantity());

        //System.out.println("Ticket DTO1234 : "+ticketDto.toString());
    return ticketDto;
    }

//    @FXML
    private void onSearchClick(String ticketNumber) {

        boolean status=true;


        try {
            if (ticketNumber.isEmpty()) {
                throw new RuntimeException("Please enter a ticket number");
            }


            TicketRequestV1 ticketRequestV1= ScuDataMapper.createTicketInfoRequestByNumber(ticketNumber);
            QRTicket ticket= new QRTicket();
            TicketRefundResponseV1 ticketRefundResponseV1;
            boolean isCCU;
            boolean isUsed;  // on the bases of status 1,2,3,4

            if(agent.getPeripheralMonitor().isCcu_connected()) {
                isCCU=true;
                ticketRefundResponseV1= ccuService.getTicketByNumber(ticketRequestV1);
            }else{
                isCCU = false;
                ticketRefundResponseV1 = scuService.getTicketByNumber(ticketRequestV1);
            }

            Logger.debug(ticketRefundResponseV1.getResponseMetaData().getErrorCode());

            if (!ticketRefundResponseV1.getResponseMetaData().getErrorCode().equals("200")) {
                errorText.setText("Ticket Not Found on "+ (isCCU ? "CCU" : "SCU") + " server");
                throw new RuntimeException("Ticket Not Found on "+ (isCCU ? "CCU" : "SCU") + " server");
            }

            int maxRefundTime = TicketType.getTicket(ticketRefundResponseV1.getTicket().getProductId()).getProduct().getRefundAfterSale();
            String description = maxRefundTime==0
                    ? "Valid until the end of the Business day"
                    : "Valid for " + maxRefundTime + " minutes from issuance";

            Logger.debug("Ticket Status  "+ticketRefundResponseV1.getTicket().getProductId()+" "
                    +ticketRefundResponseV1.getTicket().getStatus()+" "+ticketRefundResponseV1.getTicket().getIsActive());

            ticket.setTicketNo(ticketRefundResponseV1.getTicket().getTicketId());
            ticket.setInitiateDateTime(ticketRefundResponseV1.getTicket().getTicketIssue());
            ticket.setFareMode(ticketRefundResponseV1.getTicket().getPaymentMode());
            ticket.setPrice("Rs. "+ticketRefundResponseV1.getTicket().getAmount());
            ticket.setFrom(ticketRefundResponseV1.getTicket().getSourceStation());
            ticket.setTo(ticketRefundResponseV1.getTicket().getDestinationStation());
            ticket.setType(ticketRefundResponseV1.getTicket().getProductId());
            isUsed=Integer.parseInt(ticketRefundResponseV1.getTicket().getStatus())>0;


            if(!TimeUtil.isWithinRefundWindow(Long.parseLong(ticketRefundResponseV1.getTicket().getTicketIssue())) ){
                status=false;
                description="Time has exceeded to refund.";
            }

            if(!ticketRefundResponseV1.getTicket().getIsActive()){
                status=false;
                description="Ticket is Inactive ";

            }

            if(isUsed){
                status=false;
                description="Ticket Used";
            }

            if(TicketType.getTicket(ticketRefundResponseV1.getTicket().getProductId())==null
                    ||!TicketType.getTicket(ticketRefundResponseV1.getTicket().getProductId()).isRefundable()){
                status=false;
                description="Not Refundable";
            }

            if(!ticketRefundResponseV1.getTicket().getSourceStation().equals(this.agent.getSystemConfig().getCurrentStation().getStationId()))
            {
                status=false;
                description="Ticket generated at different Station";
            }


            // Load ticket details view
            FXMLLoader fxmlLoader = ViewFactory.getRefundTicketDetailsView();
            String finalDescription = description;
            boolean finalStatus = status;
            fxmlLoader.setControllerFactory(param -> new RefundTicketDetailsController(borderPane, agent, ticket, finalDescription, finalStatus,ticketRefundResponseV1.getTicket().getAmount(),isCCU));
            borderPane.setCenter(fxmlLoader.load());

        } catch (Exception e) {
            Logger.error("Error searching for ticket: {}", e.getMessage());
            errorText.setText(e.getMessage());
        }
    }

}
