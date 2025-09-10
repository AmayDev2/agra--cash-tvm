package com.amay.tom.controller;//package com.amay.tom.controller;
//
//import com.amay.tom.ViewFactory;
//import com.amay.tom.agent.Agent;
//import com.amay.tom.grpc.scugrpc.ScuDataMapper;
//import com.amay.tom.grpc.scugrpc.ScuService;
//import com.amay.tom.model.QRTicket;
//import com.amay.tom.service.chield.ticketservice.ImplTicketService;
//import com.amay.tom.utils.env.EnvFile;
//import com.amay.tom.utils.time.TimeUtil;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextField;
//import javafx.scene.image.Image;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.text.Text;
//import org.amaytechnosystems.TicketRefundResponseV1;
//import org.amaytechnosystems.TicketRequestV1;
//import org.tinylog.Logger;
//
//import java.time.Instant;
//
//public class RefundTicketInputController {
//    @FXML
//    private TextField ticketNumberField;
//
//    @FXML
//    private Button searchButton;
//
//    @FXML
//    private Text errorText;
//
//    private BorderPane borderPane;
//    private Agent agent;
//    private ScuService scuService;
//
//    public RefundTicketInputController(BorderPane borderPane, Agent agent) {
//        this.borderPane = borderPane;
//        this.agent = agent;
//        scuService=agent.getScuService();
//
//    }
//
//    @FXML
//    private void initialize() {
//        Logger.info("Refund Ticket Input scene loaded");
//    }
//
//    public boolean isWithin30Minutes(long epochMilliseconds) {
//        Instant expiryTime = Instant.ofEpochMilli(epochMilliseconds).plusSeconds(EnvFile.getRefundTime() * 60);
//        //System.out.println("expireTime "+expiryTime);
//
//        Instant now = Instant.now();
//
//        //System.out.println("Now "+now );
//
//        return expiryTime.isAfter(now);
//    }
//
//
//    @FXML
//    private void onSearchClick() {
//        String ticketNumber = ticketNumberField.getText().trim();
//
//        String description = "Valid (within "+EnvFile.getRefundTime()+" minutes)";
//        boolean status=true;
//
//        if (ticketNumber.isEmpty()) {
//            errorText.setText("Please enter a ticket number");
//            return;
//        }
//
//        try {
//            TicketRequestV1 ticketRequestV1=ScuDataMapper.createTicketInfoRequestByNumber(ticketNumber);
//            QRTicket ticket= new QRTicket();
////            (String ticketNo, String initiateDateTime, String expiryTime, String from, String to, String type, String fareMode, String price, Image
////            qrCode)
//            TicketRefundResponseV1 ticketRefundResponseV1 = scuService.getTicketByNumber(ticketRequestV1);
//
//            //System.out.println(ticketRefundResponseV1.getResponseMetaData().getErrorCode());
//
//            if (!ticketRefundResponseV1.getResponseMetaData().getErrorCode().equals("200")) {
//               //System.out.println("Ticket Response is Null");
//                return;
//            }
//
//            //System.out.println("Ticket Status  "+ticketRefundResponseV1.getTicket().getTicketType()+" "
//                    +ticketRefundResponseV1.getTicket().getStatus()+" "+ticketRefundResponseV1.getTicket().getIsActive());
//
//            ticket.setTicketNo(ticketRefundResponseV1.getTicket().getTicketNumber());
//            ticket.setInitiateDateTime(ticketRefundResponseV1.getTicket().getTicketIssue());
//            ticket.setFareMode(ticketRefundResponseV1.getTicket().getPaymentMode());
//            ticket.setPrice("Rs. "+ticketRefundResponseV1.getTicket().getAmount());
//            ticket.setFrom(ticketRefundResponseV1.getTicket().getSourceStation());
//            ticket.setTo(ticketRefundResponseV1.getTicket().getDestinationStation());
//            ticket.setType(ticketRefundResponseV1.getTicket().getTicketType());
//
//
//            if(!TimeUtil.isWithin30Minutes(Long.parseLong(ticketRefundResponseV1.getTicket().getTicketIssue())) ){
//                status=false;
//                description="Time has exceeded 30 minutes";
//            }
//
//            if(!ticketRefundResponseV1.getTicket().getIsActive()){
//                status=false;
//                description="Currently not active";
//            }
//
//
//
//            // Load ticket details view
//            FXMLLoader fxmlLoader = ViewFactory.getRefundTicketDetailsView();
//            String finalDescription = description;
//            boolean finalStatus = status;
//            fxmlLoader.setControllerFactory(param -> new RefundTicketDetailsController(borderPane, agent, ticket, finalDescription, finalStatus,ticketRefundResponseV1.getTicket().getAmount()));
//            borderPane.setCenter(fxmlLoader.load());
//
//        } catch (Exception e) {
//            Logger.error("Error searching for ticket: {}", e.getMessage());
//            errorText.setText("Error searching for ticket");
//        }
//    }
//}