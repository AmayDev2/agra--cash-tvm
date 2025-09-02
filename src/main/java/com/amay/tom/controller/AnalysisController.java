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
import com.amay.tom.repository.StationData;
import com.amay.tom.service.analysis.Analysis;
import com.amay.tom.service.analysis.impl.ImplAnalysis;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.amaytechnosystems.AOperator;
import org.amaytechnosystems.ATicket;
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
    @FXML
    public TextField ticketId;
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

    private QRTicketV2 qrTicket;
//    AtomicReference<ATicketAnalysisDTO> aTicketAnalysis;
//    AtomicBoolean isAdjusted;
    QRTicket ticket;

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
        ticketId.setText("");
    }

    public  static AnalysisController getAnalysisController(){
        return analysisController;
    }


    public void qrDetails(QRTicketV2 qrTicket)  {

        if(qrTicket==null){
            //TODO: Show alert
            return;
        }
        ticketId.setText(qrTicket.getTicketId());
        this.qrTicket=qrTicket;
    }

    public void onClickAnalysis() {
        AtomicBoolean isAdjusted = new AtomicBoolean(false);
        QRTicket ticket= new QRTicket();
        AtomicReference<ATicketAnalysisDTO> aTicketAnalysis= new AtomicReference<>();
        if(ticketId.getText().isEmpty()){
            analysisMessage.setText("Enter TicketId");
            return;
        }
        try{
            Platform.runLater(()->{
                TicketRequestV1 ticketRequestV1= ScuDataMapper.createTicketInfoRequestByNumber(ticketId.getText());
                Logger.debug("Analysis : TicketId : "+ticketId.getText());
                TicketAnalysisResponseV1 ticketRefundResponseV1 =null;
                if(agent.getPeripheralMonitor().isCcu_connected()){
                    ticketRefundResponseV1 = ccuService.getTicketAnalysisByNumber(ticketRequestV1);
                }else {
                    ticketRefundResponseV1 = scuService.getTicketAnalysisByNumber(ticketRequestV1);
                }
                Logger.debug("Analysis Response Code : {}",ticketRefundResponseV1.getResponseMetaData().getErrorCode());
                if(qrTicket==null){
                    this.qrTicket=new QRTicketV2();
                    ATicket aTicket = ticketRefundResponseV1.getTicketAnalysis().getTicket();
                    AOperator aOperator = ticketRequestV1.getTicketData().getOperator();
                    qrTicket.setTicketId(aTicket.getTicketId());
                    qrTicket.setFareMode(aTicket.getPaymentMode());
                    qrTicket.setAmount((int)aTicket.getAmount());
                    qrTicket.setTicketType(TicketType.getTicket(aTicket.getProductId()));
                    qrTicket.setIssueAt(Long.parseLong(aTicket.getTicketIssue()));
                    qrTicket.setOperatorId(aOperator.getOperatorId());
                    qrTicket.setQuantity(aTicket.getQuantity());
                    qrTicket.setInStation(StationData.getInstance().getStation(aTicket.getSourceStation()));
                    qrTicket.setOutStation(StationData.getInstance().getStation(aTicket.getDestinationStation()));
                    qrTicket.setValidUntil(Long.parseLong(aTicket.getTicketExp()));
                }
                if (!ticketRefundResponseV1.getResponseMetaData().getErrorCode().equals("200")) {
                    String errorCode = ticketRefundResponseV1.getResponseMetaData().getErrorCode();
                    Logger.error("Analysis Response Code : {}",errorCode);
                    switch (errorCode) {
                        case "701":
                            analysisMessage.setText("Ticket not Found on server");
                            break;
                        case "702":
                            analysisMessage.setText("Ticket Already Exists on server");
                            break;
                        case "703":
                            analysisMessage.setText("Fraud Ticket Detected on server");
                            break;
                        case "721":
                            analysisMessage.setText("No History Found on server");
                            break;
                        case "722":
                            analysisMessage.setText("Fraud Ticket No History Found on server");
                            break;
                        default:
                            analysisMessage.setText("Unknown Error");
                            break;
                    }
                    ticketId.clear();
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
//                this.isAdjusted=isAdjusted;
//                this.aTicketAnalysis=aTicketAnalysis;
            });
        }catch (RuntimeException ex){
            ticketId.clear();
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
                ticketId.clear();
            }catch (Exception e) {
                if(analysisMessage.getText().isBlank())
                    analysisMessage.setText("Operation Failed");
            }
        });
    }
}
