package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.controllerInterface.controllerInt.ControllerAdapter;
import com.amay.tom.grpc.scugrpc.ScuDataMapper;
import com.amay.tom.grpc.scugrpc.ScuService;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.analysis.ATicketAnalysisDTO;
import com.amay.tom.model.analysis.AnalysisTicketMapper;
import com.amay.tom.model.tickets.QRTicketV2;
import com.amay.tom.service.analysis.Analysis;
import com.amay.tom.service.analysis.impl.ImplAnalysis;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.amaytechnosystems.TicketAnalysisResponseV1;
import org.amaytechnosystems.TicketRequestV1;
import org.tinylog.Logger;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AnalysisController {

    private final BorderPane borderPane;
    @FXML
    public ImageView qrScannerImage;

    @FXML
    public ImageView cardScannerImage;
    private Analysis analysis;

    @FXML
    private Text qrText;

    @FXML
    private Label analysisMessage;

    public static AnalysisController analysisController;

    @FXML
    private AnchorPane ancher;

    @FXML
    private Button cardAnaImage;

    @FXML
    private Text cardAnaText;

    @FXML
    private Button qrAnaImage;

    @FXML
    private Text qrAnaText;

    private final ScuService scuService, ccuService;

    private final Agent agent;
    public AnalysisController(Agent agent, BorderPane borderPane) {
        this.agent = agent;
        this.borderPane = borderPane;
        this.ccuService=agent.getCcuService();
        this.scuService=agent.getScuService();
    }

    @FXML
    void onClickCardScanner(ActionEvent event) {

    }

    @FXML
    void initialize(){
        analysisController=this;
        analysis= ImplAnalysis.getInstance();
        analysis.analysisQR(this.ancher);
        analysisMessage.setText("");
    }

    public  static AnalysisController getAnalysisController(){
        return analysisController;
    }


    public void qrDetails(QRTicketV2 qrTicket)  {

        if(qrTicket==null){
            //TODO: Show alert
            return;
        }
        AtomicBoolean isAdjusted = new AtomicBoolean(false);
        QRTicket ticket= new QRTicket();
        AtomicReference<ATicketAnalysisDTO> aTicketAnalysis= new AtomicReference<>();
        try{
            Platform.runLater(()->{
            TicketRequestV1 ticketRequestV1= ScuDataMapper.createTicketInfoRequestByNumber(qrTicket.getTicketId());
                TicketAnalysisResponseV1 ticketRefundResponseV1 =null;
                if(agent.getPeripheralMonitor().isCcu_connected()){
                ticketRefundResponseV1 = ccuService.getTicketAnalysisByNumber(ticketRequestV1);
                }else {
                ticketRefundResponseV1 = scuService.getTicketAnalysisByNumber(ticketRequestV1);
                }
                Logger.debug("Analysis Response Code : {}",ticketRefundResponseV1.getResponseMetaData().getErrorCode());

            if (!ticketRefundResponseV1.getResponseMetaData().getErrorCode().equals("200")) {
                Logger.error("Analysis Response Code : {}",ticketRefundResponseV1.getResponseMetaData().getErrorCode());
                analysisMessage.setText("Invalid Ticket");
                return;
            }
            analysisMessage.setText("");


            System.out.println("Ticket Status  "+ticketRefundResponseV1.getTicketAnalysis().getTicket().getProductId()+" "
                    +ticketRefundResponseV1.getTicketAnalysis().getTicket().getStatus()+" "+ticketRefundResponseV1.getTicketAnalysis().getTicket().getIsActive());

            ticket.setTicketNo(ticketRefundResponseV1.getTicketAnalysis().getTicket().getTicketId());
            ticket.setInitiateDateTime(ticketRefundResponseV1.getTicketAnalysis().getTicket().getTicketIssue());
            ticket.setFareMode(ticketRefundResponseV1.getTicketAnalysis().getTicket().getPaymentMode());
            ticket.setPrice("Rs. "+ticketRefundResponseV1.getTicketAnalysis().getTicket().getAmount());
            ticket.setFrom(ticketRefundResponseV1.getTicketAnalysis().getTicket().getSourceStation());
            ticket.setTo(ticketRefundResponseV1.getTicketAnalysis().getTicket().getDestinationStation());


            ticket.setType(Objects.requireNonNull(TicketType.getTicket(ticketRefundResponseV1.getTicketAnalysis().getTicket().getProductId())).getTicketTypeName());


            isAdjusted.set(ticketRefundResponseV1.getTicketAnalysis().getTicket().getIsAdjusted());
                System.out.println("Ticket Adjusted Status "+isAdjusted);

            aTicketAnalysis.set(AnalysisTicketMapper.toDTO(ticketRefundResponseV1.getTicketAnalysis()));

            });


        }catch (RuntimeException ex){
            analysisMessage.setText("Operation failed");
            return;
        }

        FXMLLoader fxmlLoader=ViewFactory.getTicketAnalysisDetails();
        fxmlLoader.setControllerFactory(param -> new TicketAnalysisDetailsController(this.agent,borderPane,isAdjusted));

        Platform.runLater(() -> {
            try {
                Parent parent= fxmlLoader.load();
                TicketAnalysisDetailsController ticketAnalysisDetailsController=fxmlLoader.getController();
                ticketAnalysisDetailsController.setTicketDetails(qrTicket,aTicketAnalysis.get());
                ControllerAdapter.INSTANCE.setChildInCenterAnchorPane(parent);
            }catch (Exception e) {
                analysisMessage.setText("Operation Failed");
            }
        });

    }
}
