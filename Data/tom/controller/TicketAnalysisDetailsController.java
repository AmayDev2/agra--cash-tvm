package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.enums.AdjustmentType;
import com.amay.tom.enums.PassangerPossition;
import com.amay.tom.model.analysis.ATicketAnalysisDTO;
import com.amay.tom.model.tickets.QRTicketV2;
import com.amay.tom.repository.StationData;
import com.amay.tom.service.qrService2.TicketInfo;
import com.amay.tom.service.validation.Validation;
import com.amay.tom.service.validation.impl.QRValidation;
import com.amay.tom.utils.time.TimeUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import org.tinylog.Logger;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TicketAnalysisDetailsController {

    private static TicketAnalysisDetailsController ticketAnalysisDetailsController;
    private final BorderPane borderPane;

    @FXML
    private Text destination;

    @FXML
    private Text origin;

    @FXML
    private Text qrSaleDate;

    @FXML
    private Text qrValidity;

    @FXML
    private Text quantity;

    @FXML
    private Text status;

    @FXML
    private Text ticketCost;

    @FXML
    private Text ticketId;

    @FXML
    private Text ticketType;
    private final String COLON=": ";

    @FXML
    private GridPane penaltyGrid;

    @FXML
    private RowConstraints penaltyRow;

    @FXML
    private Button adjust;

    @FXML
    private Text penalty;

    @FXML
    private Text total;

    @FXML
    private ToggleGroup toggleGroup1;

    private Agent agent;
    private int PENALTY;
    private AtomicBoolean isAdjusted;

    public TicketAnalysisDetailsController(Agent agent, BorderPane borderPane, AtomicBoolean isAdjusted){
        this.agent= agent;
        this.borderPane=borderPane;
        this.isAdjusted=isAdjusted;
    }



    @FXML
    void initialize() {
//         toggleGroup1.getToggles().get(0).setSelected(true);
        // Add listener to toggle group
        unpaid.setSelected(true);
        toggleGroup1.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                RadioButton selected = (RadioButton) newToggle;
                String selectedValue = selected.getText();
                System.out.println("Selected: " + selectedValue);

                // You can trigger custom logic here
                if (selected == paid) {
                    Platform.runLater(()->setTicketDetails(this.qrTicket,this.aTicketAnalysisDTO));
                    System.out.println("Paid selected");
                } else if (selected == unpaid) {
                    System.out.println("Unpaid selected");
                    Platform.runLater(()->setTicketDetails(this.qrTicket,this.aTicketAnalysisDTO));
                }
            }
        });

        PENALTY=0;

//         toggleGroup1
        ticketAnalysisDetailsController = this;
        this.ticketId.setText("1234");
        this.origin.setText("Kathmandu");
        this.destination.setText("Pokhara");
        this.qrSaleDate.setText("2021-07-01");
        this.qrValidity.setText("2021-07-02");
        this.quantity.setText("1");
        this.status.setText("Active");
        this.ticketCost.setText("100");
        this.ticketType.setText("Single");
        setPenaltyGrid(false);
        adjust.setVisible(false);

//        adjust.setOnAction(event -> {
//            Logger.getGlobal().info("Adjust button clicked");
//        });


    }

    private void setPaidUnpaid(boolean isPaid) {
        if(isPaid){
            paid.setSelected(true);
            unpaid.setSelected(false);
        }
        else{
            paid.setSelected(false);
            unpaid.setSelected(true);
        }
    }

//    public static TicketAnalysisDetailsController getController() {
//        if(ticketAnalysisDetailsController==null)
//            ticketAnalysisDetailsController=new TicketAnalysisDetailsController();
//        return ticketAnalysisDetailsController;
//    }




    public void setTicketDetails(String ticketId, String origin, String destination, String qrSaleDate, String qrValidity, String quantity, String status, String ticketCost, String ticketType) {
        this.ticketId.setText(ticketId);
        this.origin.setText(origin);
        this.destination.setText(destination);
        this.qrSaleDate.setText(qrSaleDate);
        this.qrValidity.setText(qrValidity);
        this.quantity.setText(quantity);
        this.status.setText(status);
        this.ticketCost.setText(ticketCost);
        this.ticketType.setText(ticketType);
    }

    @FXML
    private RadioButton unpaid;

    @FXML
    private RadioButton paid;

    private QRTicketV2 qrTicket;

    private ATicketAnalysisDTO aTicketAnalysisDTO;

    private HashSet<AdjustmentType> adjustmentTypeList;

    public void setTicketDetails(QRTicketV2 qrTicket,ATicketAnalysisDTO aTicketAnalysisDTO) {
        this.qrTicket=qrTicket;
        this.aTicketAnalysisDTO=aTicketAnalysisDTO;
        this.adjustmentTypeList= new HashSet<>();

        final Validation validation=new QRValidation();
        System.out.println("Setting ticket details"+qrTicket.toString());
        this.ticketId.setText(COLON+qrTicket.getTicketId());
        this.origin.setText(COLON+qrTicket.getInStation().getStationName());
        this.destination.setText(COLON+qrTicket.getOutStation().getStationName());
        this.qrSaleDate.setText(COLON+TimeUtil.epochMilliToFormattedSystemTime(qrTicket.getIssueAt(),null));
//        this.qrValidity.setText(COLON+TimeUtil.epochMilliToFormattedSystemTime(qrTicket.getValidUntil(),null));
        this.qrValidity.setText(COLON+"120 minutes after entry");
        this.quantity.setText(COLON+String.valueOf(qrTicket.getQuantity()));

        String status="";
        int penalty=0;
        boolean isExpire=false;

        if(!this.aTicketAnalysisDTO.getTicketStatus().isActive() ){
            if(this.aTicketAnalysisDTO.getTicketStatus().isRefunded()){
                status="Deactivate ";
                status+="Refunded";
            }
            this.status.setStyle("-fx-fill: gray;");

        }else {

            if(Integer.parseInt(aTicketAnalysisDTO.getTicket().getStatus())==0
                    && aTicketAnalysisDTO.getTicket().getSourceStation().equals(SystemConfig.getInstance().getCurrentStation().getStationId())
                    && validation.entryValidation(aTicketAnalysisDTO.getTicket().getTicketIssue())
            ){
                isExpire=true;
                status="Entry Time Exceeded, "; //T1
                adjustmentTypeList.add(AdjustmentType.ENTRY_TIME);
                penalty= 0;
            }

        if(paid.isSelected()) {
            if (validation.entryExitMismatch(PassangerPossition.PAID, Integer.parseInt(aTicketAnalysisDTO.getTicket().getStatus())) > 0) { // set Status
                isExpire=true;
                status="Entry-Exit Mismatch, "; //M
                adjustmentTypeList.add(AdjustmentType.ENTRY_EXIT_MISMATCH);
                penalty= validation.getEntryExitPenalty();
            }

            if ( validation.overStrayValidation(aTicketAnalysisDTO)>0){
                isExpire=true;
                status+=" Over Stay"; //T2
                adjustmentTypeList.add(AdjustmentType.EXIT_TIME);
                penalty=Math.max(validation.overStrayValidation(aTicketAnalysisDTO),penalty);
            }

            if (validation.overTravelValidationFareBased(qrTicket,agent.getBusinessRule().getFareMultiplayer(qrTicket.getIssueAt()))>0){
                isExpire=true;
                status+=" Over Travel";//D
                adjustmentTypeList.add(AdjustmentType.OVER_TRAVEL);
                penalty=Math.max(validation.overTravelValidationFareBased(qrTicket,agent.getBusinessRule().getFareMultiplayer(qrTicket.getIssueAt())),penalty);
            }

        }else{

            if (validation.entryExitMismatch(PassangerPossition.UNPAID, Integer.parseInt(aTicketAnalysisDTO.getTicket().getStatus())) > 0) { //set status
                isExpire=true;
                status="Entry-Exit Mismatch, ";//M
                adjustmentTypeList.add(AdjustmentType.ENTRY_EXIT_MISMATCH);
                penalty= validation.getEntryExitPenalty();
            }

//            if (false && validation.overStayValidation(qrTicket)>0){
//                isExpire=true;
//                status+=" Over Stay";
//                penalty=Math.max(validation.overStayValidation(qrTicket),penalty);
//            }

            // If the person is in UPA ->Tailgating for exit
            if (validation.overTravelValidationFareBased(qrTicket,agent.getBusinessRule().getFareMultiplayer(qrTicket.getIssueAt()))>0){
                isExpire=true;
                status+=" Over Travel";//D
                adjustmentTypeList.add(AdjustmentType.OVER_TRAVEL);
                penalty=Math.max(validation.overTravelValidationFareBased(qrTicket,agent.getBusinessRule().getFareMultiplayer(qrTicket.getIssueAt())),penalty);
            }


        }

            if(isExpire) {
                PENALTY=penalty;
                this.status.setStyle("-fx-fill: red;");
                this.penalty.setText(COLON+"Rs. "+penalty);
                this.total.setText(COLON+"Rs. "+penalty);
                setPenaltyGrid(true);
                adjust.setVisible(true);
            }else if(!isExpire && adjust.isVisible()){
                 {
//                PENALTY=penalty;
//                this.status.setStyle("-fx-fill: red;");
//                this.penalty.setText(COLON+"Rs. "+penalty);
//                this.total.setText(COLON+"Rs. "+penalty);
                    setPenaltyGrid(false);
                    adjust.setVisible(false);
                }
            }
            if(aTicketAnalysisDTO.getTicketStatus().isAdjusted()){
                status="Adjusted";
                this.status.setStyle("-fx-fill: blue;");
                setPenaltyGrid(false);
                adjust.setVisible(false);
            }else if(!isExpire){
                if(Integer.parseInt(aTicketAnalysisDTO.getTicket().getStatus())==0){
                    status="Valid";
                }else if(Integer.parseInt(aTicketAnalysisDTO.getTicket().getStatus())%2==0 ){
                    status="EXIT DONE";
                }else{
                    status="ENTRY DONE";
                }
                this.status.setStyle("-fx-fill: green;");
            }
        }


//
//
////        boolean isExpire= TimeUtil.isExpired(qrTicket.getExpiryTime());
//        if(isExpire) {
//            PENALTY=penalty;
//            this.status.setStyle("-fx-fill: red;");
//            this.penalty.setText(COLON+"Rs. "+penalty);
//            this.total.setText(COLON+"Rs. "+penalty);
//            setPenaltyGrid(true);
//            adjust.setVisible(true);
//        }
//        if(isAdjusted.get()){
//            status="Adjusted";
//            this.status.setStyle("-fx-fill: blue;");
//            setPenaltyGrid(false);
//            adjust.setVisible(false);
//        }
//        else if(!isExpire){
//            status="Valid";
//            this.status.setStyle("-fx-fill: green;");
//            setPenaltyGrid(false);
//            adjust.setVisible(false);
//        }
        this.status.setText(status);
        this.ticketCost.setText(COLON+"Rs. "+qrTicket.getAmount());
        this.ticketType.setText(COLON+qrTicket.getTicketType().getTicketTypeName());
        Logger.info("Penalty "+PENALTY);
    }

    public void clearTicketDetails() {
        this.ticketId.setText("");
        this.origin.setText("");
        this.destination.setText("");
        this.qrSaleDate.setText("");
        this.qrValidity.setText("");
        this.quantity.setText("");
        this.status.setText("");
        this.ticketCost.setText("");
        this.ticketType.setText("");
    }

    private void setPenaltyGrid(boolean isPenalty) {
        Platform.runLater(()->this.penaltyGrid.setVisible(isPenalty));
    }

//    private MetroTicket getMetroTicketToAdjust(){
//        //get the ticket to adjust
//        return new ImplQRDataGenerator().decodeQRDataToTicket(this.qrTicket.getQrCodeData());
//
//    }


    @FXML
    private void onClickAdjust(ActionEvent actionEvent) {
        System.out.println("Adjust button clicked 1");
        try {
        FXMLLoader fxmlLoader = ViewFactory.getPayment();
        PaymentController paymentController=new PaymentController(this.agent);
        paymentController.setParentNode(borderPane.getCenter());
        fxmlLoader.setControllerFactory(param ->paymentController );
         Node node= fxmlLoader.load();
        TicketInfo ticketInfo=new TicketInfo(this.qrTicket);
         ticketInfo.setAdjustmentType(adjustmentTypeList);
        ticketInfo.setPAmount(PENALTY);
        ticketInfo.setTime(Instant.now().toEpochMilli());
        ticketInfo.setQrData(aTicketAnalysisDTO.getTicket().getQrData());
        ticketInfo.setArea(paid.isSelected()?"Paid":"Unpaid");

        ticketInfo.setDestination(StationData.getInstance().getStation(SystemConfig.getInstance().getCurrentStation().getStationId()));
        paymentController.setTicketsPantylinerGrid(new TicketInfo[]{ticketInfo}, agent.getShiftIdGeneratorService().getOrderIdGeneratorService().generateOrderId());
        borderPane.setCenter(node);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        actionEvent.consume();
    }


}
