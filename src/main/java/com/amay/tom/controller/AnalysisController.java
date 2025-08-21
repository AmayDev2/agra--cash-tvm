package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.controllerInterface.controllerInt.ControllerAdapter;
import com.amay.tom.grpc.scugrpc.ScuDataMapper;
import com.amay.tom.grpc.scugrpc.ScuService;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.analysis.ATicketAnalysisDTO;
import com.amay.tom.model.analysis.AnalysisTicketMapper;
import com.amay.tom.model.tickets.QRTicketV2;
import com.amay.tom.service.analysis.Analysis;
import com.amay.tom.service.analysis.QrCodeEventListener;
import com.amay.tom.service.analysis.impl.ImplAnalysis;
import com.amay.tom.service.qrDataGenerator.impl.ImplQRDataGenerator;
import com.amay.tom.service.qrReaderServiceTest.QrReader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.amaytechnosystems.TicketAnalysisResponseV1;
import org.amaytechnosystems.TicketRequestV1;

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

    private Agent agent;
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
    }


    public  static AnalysisController getAnalysisController(){
        return analysisController;
    }



    @FXML
    void onClickQRScanner(ActionEvent event) {

//        analysis.analysisQR();


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
            Platform.runLater(()->{;
            TicketRequestV1 ticketRequestV1= ScuDataMapper.createTicketInfoRequestByNumber(qrTicket.getTicketId());
//            TicketRefundResponseV1 ticketRefundResponseV1 = ccuService.getTicketByNumber(ticketRequestV1);
//                TicketRequestV1 ticketRequestV1= ScuDataMapper.createTicketInfoRequestByNumber(ticketNumber);
                TicketAnalysisResponseV1 ticketRefundResponseV1 =null;
                if(agent.getPeripheralMonitor().isCcu_connected()){
                ticketRefundResponseV1 = ccuService.getTicketAnalysisByNumber(ticketRequestV1);
                }else {
                ticketRefundResponseV1 = scuService.getTicketAnalysisByNumber(ticketRequestV1);
                }

            if (!ticketRefundResponseV1.getResponseMetaData().getErrorCode().equals("200")) {
                Alert alert= new Alert(Alert.AlertType.ERROR);
                System.out.println("Ticket Response is Null");
                alert.setTitle("Info");
                alert.setHeaderText("Operation Failed");
                alert.setContentText("Ticket Not Found");
                alert.showAndWait();
                return;
            }


            System.out.println("Ticket Status  "+ticketRefundResponseV1.getTicketAnalysis().getTicket().getTicketType()+" "
                    +ticketRefundResponseV1.getTicketAnalysis().getTicket().getStatus()+" "+ticketRefundResponseV1.getTicketAnalysis().getTicket().getIsActive());

            ticket.setTicketNo(ticketRefundResponseV1.getTicketAnalysis().getTicket().getTicketNumber());
            ticket.setInitiateDateTime(ticketRefundResponseV1.getTicketAnalysis().getTicket().getTicketIssue());
            ticket.setFareMode(ticketRefundResponseV1.getTicketAnalysis().getTicket().getPaymentMode());
            ticket.setPrice("Rs. "+ticketRefundResponseV1.getTicketAnalysis().getTicket().getAmount());
            ticket.setFrom(ticketRefundResponseV1.getTicketAnalysis().getTicket().getSourceStation());
            ticket.setTo(ticketRefundResponseV1.getTicketAnalysis().getTicket().getDestinationStation());
            ticket.setType(ticketRefundResponseV1.getTicketAnalysis().getTicket().getTicketType());


            isAdjusted.set(ticketRefundResponseV1.getTicketAnalysis().getTicket().getIsAdjusted());
                System.out.println("Ticket Adjusted Status "+isAdjusted);

            aTicketAnalysis.set(AnalysisTicketMapper.toDTO(ticketRefundResponseV1.getTicketAnalysis()));

            });



        }catch (RuntimeException ex){
            Alert alert= new Alert(Alert.AlertType.ERROR);
            System.out.println("Ticket Response is Null");
            alert.setTitle("Info");
            alert.setHeaderText("Operation Failed");
            alert.setContentText("Ticket Could not be adjusted");
            alert.showAndWait();
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
                e.printStackTrace();
            }
        });

    }
}
