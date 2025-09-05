package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.TicketConfig;
import com.amay.tom.controllerInterface.controllerInt.ControllerAdapter;
import com.amay.tom.grpc.scugrpc.ScuDataMapper;
import com.amay.tom.grpc.scugrpc.ScuService;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tom.service.analysis.QrCodeEventListener;
import com.amay.tom.service.analysis.TicketNumberFromQREventListener;
import com.amay.tom.service.chield.ReprintTicket;
import com.amay.tom.service.chield.ticketservice.ImplTicketService;
import com.amay.tom.service.qrDataGenerator.impl.ImplQRDataGenerator;
import com.amay.tom.service.qrReaderServiceTest.QrReader;
import com.amay.tom.utils.env.EnvFile;
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
import javafx.scene.layout.VBox;
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
            onSearchClick(inputTextField.getText().trim());
        } catch (Exception e) {
            Logger.error("Error in loading Refund Ticket Input View: {}", e.getMessage());
            e.printStackTrace();
        }
        actionEvent.consume();
    }

    public void onClickReplacement(ActionEvent actionEvent) {
        try {
            onClickConform(inputTextField.getText().trim());
        } catch (Exception e) {
            Logger.error("Error in loading QRT Operations View: {}", e.getMessage());
            e.printStackTrace();
        }
        actionEvent.consume();
    }

    void onClickConform(String ticketNumber)  {

        TicketsDto ticketsDto=ticketsRepository.findById(ticketNumber);

        //TODO: map ticket by model

        try{
            FXMLLoader loader = ViewFactory.getReplacementDetailsView();
            loader.setControllerFactory(x->new ReplacementTicketDetailsController(ticketsDto,borderPane));
            Pane root = loader.load();
            ControllerAdapter.INSTANCE.setChildInCenterAnchorPane(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }



//    @FXML
    private void onSearchClick(String ticketNumber) {


        String description = (TicketConfig.INSTANT.getProductTypeDefDTO().getMaxRefundTime() == 0)
                ? "Valid until the end of the day"
                : "Valid for " + TicketConfig.INSTANT.getProductTypeDefDTO().getMaxRefundTime() + " minutes from issuance";

        boolean status=true;

        if (ticketNumber.isEmpty()) {
//            errorText.setText("Please enter a ticket number");
            alert.setTitle("Info");
            alert.setHeaderText("Operation Failed");
            alert.setContentText("Please enter a ticket number");
            alert.showAndWait();
            return;
        }

        try {
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
//                TicketAnalysisResponseV1 ticketRefundResponseV1 = scuService.getTicketAnalysisByNumber(ticketRequestV1);
            }

            System.out.println(ticketRefundResponseV1.getResponseMetaData().getErrorCode());

            if (!ticketRefundResponseV1.getResponseMetaData().getErrorCode().equals("200")) {
                errorText.setText("Ticket Not Found");
//                System.out.println("Ticket Response is Null");
//                alert.setTitle("Info");
//                alert.setHeaderText("Operation Failed");
//                alert.setContentText("Ticket Not Found");
//                alert.showAndWait();
                return;
            }

            System.out.println("Ticket Status  "+ticketRefundResponseV1.getTicket().getTicketType()+" "
                    +ticketRefundResponseV1.getTicket().getStatus()+" "+ticketRefundResponseV1.getTicket().getIsActive());

            ticket.setTicketNo(ticketRefundResponseV1.getTicket().getTicketNumber());
            ticket.setInitiateDateTime(ticketRefundResponseV1.getTicket().getTicketIssue());
            ticket.setFareMode(ticketRefundResponseV1.getTicket().getPaymentMode());
            ticket.setPrice("Rs. "+ticketRefundResponseV1.getTicket().getAmount());
            ticket.setFrom(ticketRefundResponseV1.getTicket().getSourceStation());
            ticket.setTo(ticketRefundResponseV1.getTicket().getDestinationStation());
            ticket.setType(ticketRefundResponseV1.getTicket().getTicketType());
            isUsed=Integer.parseInt(ticketRefundResponseV1.getTicket().getStatus())>0;


            if(!TimeUtil.isWithinRefundWindow(Long.parseLong(ticketRefundResponseV1.getTicket().getTicketIssue())) ){
                status=false;
                description="Time has exceeded to refund.";
            }

            if(!ticketRefundResponseV1.getTicket().getIsActive()){
                status=false;
                description="Currently not active";
            }

            if(isUsed){
                status=false;
                description="Ticket Used";
            }


            // Load ticket details view
            FXMLLoader fxmlLoader = ViewFactory.getRefundTicketDetailsView();
            String finalDescription = description;
            boolean finalStatus = status;
            fxmlLoader.setControllerFactory(param -> new RefundTicketDetailsController(borderPane, agent, ticket, finalDescription, finalStatus,ticketRefundResponseV1.getTicket().getAmount(),isCCU));
            borderPane.setCenter(fxmlLoader.load());

        } catch (Exception e) {
            Logger.error("Error searching for ticket: {}", e.getMessage());
            alert.setTitle("Info");
            alert.setHeaderText("Operation Failed");
            alert.setContentText("Could not find  Ticket");
            alert.showAndWait();
        }
    }

//    public void onClickCancel(ActionEvent actionEvent) {
//        try {
//            FXMLLoader fxmlLoader = ViewFactory.getCancelView();
//            fxmlLoader.setControllerFactory(param -> new CancelViewController(agent));
//            borderPane.setCenter(fxmlLoader.load());
//        } catch (Exception e) {
//            Logger.error("Error in loading QRT Operations View: {}", e.getMessage());
//            e.printStackTrace();
//        }
//    }
}
