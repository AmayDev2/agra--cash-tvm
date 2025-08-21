package com.amay.tom.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.controllerInterface.controllerInt.ControllerAdapter;
import com.amay.tom.enums.PaymentMethod;
import com.amay.tom.exceptions.NotValidTicketToCalculateFare;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.MetroTicket;
import com.amay.tom.model.Station;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.model.tickets.*;
import com.amay.tom.pdu.controller.command.PDUCommandDispatcher;
import com.amay.tom.pdu.controller.command.ShowTicketSummary;
import com.amay.tom.pdu.controller.command.WelcomePageCommand;
import com.amay.tom.repository.FareLine3;
import com.amay.tom.service.chield.TicketService;
import com.amay.tom.service.chield.ticketservice.ImplTicketService;
//import com.amay.tom.service.payment.IPayment;
import com.amay.tom.service.payment.PaymentControllerListener;
import com.amay.tom.service.payment2.AbstractPaymentMethod;
import com.amay.tom.service.payment2.PaymentFactory;
import com.amay.tom.service.payment2.absimpl.CashPaymentMethod;
import com.amay.tom.service.payment2.absimpl.UPIPaymentMethod;
import com.amay.tom.service.qrDataGenerator.impl.ImplQRDataGenerator;
import com.amay.tom.service.qrService2.*;
import com.amay.tom.utils.GridPaneCloner;
import com.amay.tom.utils.env.EnvFile;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.tinylog.Logger;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.amay.tom.ViewFactory.getFreeTicketPrint;

public class PaymentController implements PaymentControllerListener {
    int totalFare = 0;
    @FXML
    private StackPane stackPane;
    @FXML
    private RowConstraints cashReceivedAmount;
    @FXML
    private RowConstraints showChangeAmount;
    @FXML
    private Button upi;
    @FXML
    private Button xyz;
    private TicketService ticketService;
    private PaymentMethod paymentMethod = PaymentMethod.CASH;
    @FXML
    private ColumnConstraints amountCol;
    @FXML
    private GridPane calculationGrid;
    @FXML
    private Button card;
    @FXML
    private Button cash;
    @FXML
    private TextField cashReceived;
    @FXML
    private VBox centerVBox;
    @FXML
    private Text changeAmount;
    @FXML
    private BorderPane borderPane;
    @FXML
    private ColumnConstraints destinationCol;
    @FXML
    private RowConstraints header;
    @FXML
    private ColumnConstraints originCol;
    @FXML
    private Button payAmountButton;
    @FXML
    private ColumnConstraints quantCol;
    @FXML
    private ColumnConstraints ticketTypeCol;
    @FXML
    private GridPane ticketsGrid;
//    private IPayment payment;
    private final Agent agent;
    private int receivedCash = 0;
    private MetroTicket[] metroTickets;
    private int payFor = 0;   // SJT,RJT,PAID
    private String orderId;
    private TicketInfo[] ticketInfos;
    private Alert alert;
    private int changedCash = 0;
    private Controller controller;
    private RequestedTicketOrder requestedTicketOrder;
    private int totalFareCalculated = 0;

    public PaymentController(Agent agent) {
        Logger.debug("PaymentController created");
        this.agent = agent;

    }

/*    private void toggleRowVisibility(boolean visibility) {
        boolean currentVisibility = calculationGrid.getChildren().stream().filter(node -> GridPane.getRowIndex(node) <= 1) // assuming row 0 is to be toggled
                .allMatch(Node::isVisible);

        calculationGrid.getChildren().stream().filter(node -> GridPane.getRowIndex(node) == 0) // target the first row
                .forEach(node -> node.setVisible(!currentVisibility)); // toggle visibility
    }*/

    @FXML
    void Card(ActionEvent event) {
        paymentMethod = PaymentMethod.CARD;
//        this.toggleRowVisibility(false);
        cashReceived.setDisable(true);
        cashReceived.setVisible(false);
        event.consume();
    }

    @FXML
    void Cash(ActionEvent event) {
        paymentMethod = PaymentMethod.CASH;
//        this.toggleRowVisibility(true);
        cashReceived.setDisable(false);
        cashReceived.setVisible(true);
        event.consume();

    }

    @FXML
    void Other(ActionEvent event) {
        paymentMethod = PaymentMethod.NETBANKING;
//        this.toggleRowVisibility(false);
        cashReceived.setDisable(true);
        cashReceived.setVisible(false);
        event.consume();

    }

    @FXML
    void UPI(ActionEvent event) {
        paymentMethod = PaymentMethod.UPI;
        //        this.toggleRowVisibility(false);
        cashReceived.setDisable(true);
        cashReceived.setVisible(false);
        event.consume();

    }

/*    public void proceedToPayment(ActionEvent actionEvent) {
    }*/

    private void showGridPane() {
        agent.getThreadPool().getFixedThreadPool().execute(
                () -> {
                    GridPane gridCopy = GridPaneCloner.deepCopy(ticketsGrid);
                    PDUCommandDispatcher.INSTANCE.dispatch(new ShowTicketSummary(gridCopy));
                }
        );
    }

    @FXML
    private void initialize() {
        ticketService = new ImplTicketService(new ImplQRDataGenerator());
        cash.isVisible();
        alert = new Alert(Alert.AlertType.ERROR);
    }

    //PAID
    public void setRequestedTicketOrderPaid(RequestedTicketOrder requestedTicketOrder) {

        Logger.info("Requested ticket order set: {} {}", requestedTicketOrder, requestedTicketOrder.requestedTicket().length);
        RequestedTicket[] requestedTickets = requestedTicketOrder.requestedTicket();
        payFor = 3;
        this.requestedTicketOrder = requestedTicketOrder;
        int noOfTickets = requestedTickets.length;
        this.orderId = requestedTicketOrder.orderId();

        for (var r : requestedTickets) {
            Logger.info("Requested ticket: {}", r);
        }

        int totalTickets = 0;


        for (int i = 0; i < noOfTickets; i++) {
            RequestedTicket requestedTicket = requestedTickets[i];

            Text source = new Text(requestedTicket.source().getStationName());
            Text destination = new Text(requestedTicket.destination().getStationName());
            Text ticketType = new Text(requestedTicket.ticketType().getTicketTypeName());
            Text quantity = new Text(String.valueOf(requestedTicket.quantity()));

            int amount = (requestedTicket.ticketType().equals(TicketType.RETURN) ? 2 : 1) * requestedTicket.quantity() * FareLine3.distanceMatrix[0][FareLine3.distanceMatrix.length - 1];
//            amount=(int)(agent.getBusinessRule().getFareMultiplayer()*amount);

            Text fare = new Text(String.valueOf(amount));

            // Center align all text nodes
            GridPane.setHalignment(source, HPos.CENTER);
            GridPane.setHalignment(destination, HPos.CENTER);
            GridPane.setHalignment(ticketType, HPos.CENTER);
            GridPane.setHalignment(quantity, HPos.CENTER);
            GridPane.setHalignment(fare, HPos.CENTER);

            ticketsGrid.add(source, 0, i + 1);
            ticketsGrid.add(destination, 1, i + 1);
            ticketsGrid.add(ticketType, 2, i + 1);
            ticketsGrid.add(quantity, 3, i + 1);
            ticketsGrid.add(fare, 4, i + 1);

            totalFare += amount;
            totalTickets += requestedTicket.quantity();

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(header.getMinHeight());
            rowConstraints.setPrefHeight(header.getPrefHeight());
            rowConstraints.setValignment(header.getValignment());
            rowConstraints.setVgrow(header.getVgrow());
            ticketsGrid.getRowConstraints().add(rowConstraints);
        }

        // Add separator row above and below ticket entries
        BorderPane separator1 = new BorderPane();
        separator1.setPrefHeight(2);
        separator1.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        ticketsGrid.add(separator1, 0, 0, 5, 1);

        BorderPane separator2 = new BorderPane();
        separator2.setPrefHeight(2);
        separator2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        ticketsGrid.add(separator2, 0, requestedTickets.length + 1, 5, 1);

        // Row constraints for admin and total
        for (int i = 0; i < 1; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(header.getMinHeight());
            rowConstraints.setPrefHeight(header.getPrefHeight());
            rowConstraints.setValignment(header.getValignment());
            rowConstraints.setVgrow(header.getVgrow());
            ticketsGrid.getRowConstraints().add(rowConstraints);
        }

        // Administrative Charge Row
        Text adminLabel = new Text("Administrative Charge");

        Text adminAmount = new Text(EnvFile.getAdministrativeCharge());

        GridPane.setHalignment(adminLabel, HPos.CENTER);
//        GridPane.setHalignment(adminQuantity, HPos.CENTER);
        GridPane.setHalignment(adminAmount, HPos.CENTER);

        ticketsGrid.add(adminLabel, 0, requestedTickets.length + 1);
        ticketsGrid.add(new Text(), 1, requestedTickets.length + 1);
        ticketsGrid.add(new Text(), 2, requestedTickets.length + 1);
        ticketsGrid.add(new Text(), 3, requestedTickets.length + 1);
//        ticketsGrid.add(adminQuantity, 3, requestedTickets.length + 1);
        ticketsGrid.add(adminAmount, 4, requestedTickets.length + 1);

        // Total Fare Row
        Text totalLabel = new Text("Total Amount");
        Text totalQty = new Text(String.valueOf(totalTickets));
        Text totalValue = new Text("₹ " + totalFare);

        GridPane.setHalignment(totalLabel, HPos.CENTER);
        GridPane.setHalignment(totalQty, HPos.CENTER);
        GridPane.setHalignment(totalValue, HPos.CENTER);

        ticketsGrid.add(totalLabel, 0, requestedTickets.length + 2);
        ticketsGrid.add(new Text(), 1, requestedTickets.length + 2);
        ticketsGrid.add(new Text(), 2, requestedTickets.length + 2);
        ticketsGrid.add(new Text(), 3, requestedTickets.length + 2);
        ticketsGrid.add(totalValue, 4, requestedTickets.length + 2);

        this.totalFareCalculated = totalFare;

        this.setCashReceivedListener(totalFare);
        this.showGridPane();
    }
    // NORMAL
    public void setRequestedTicketOrder(RequestedTicketOrder requestedTicketOrder) {

        Logger.info("Requested ticket order set: {} {}", requestedTicketOrder, requestedTicketOrder.requestedTicket().length);
        RequestedTicket[] requestedTickets = requestedTicketOrder.requestedTicket();
        payFor = 3;
        this.requestedTicketOrder = requestedTicketOrder;
        int noOfTickets = requestedTickets.length;
        this.orderId = requestedTicketOrder.orderId();

        for (var r : requestedTickets) {
            Logger.info("Requested ticket: {}", r);
        }

        int totalTickets = 0;

        for (int i = 0; i < noOfTickets; i++) {
            RequestedTicket requestedTicket = requestedTickets[i];

            Text source = new Text(requestedTicket.source().getStationName());
            Text destination = new Text(requestedTicket.destination().getStationName());
            Text ticketType = new Text(requestedTicket.ticketType().getTicketTypeName());
            Text quantity = new Text(String.valueOf(requestedTicket.quantity()));

            int amount = (requestedTicket.ticketType().equals(TicketType.RETURN) ? 2 : 1) * requestedTicket.quantity() * FareLine3.distanceMatrix[Integer.parseInt(requestedTicket.source().getStationId()) - 1][Integer.parseInt(requestedTicket.destination().getStationId()) - 1];
            amount=(int)(agent.getBusinessRule().getFareMultiplayer()*amount);

            Text fare = new Text(String.valueOf(amount));

            // Center align all text nodes
            GridPane.setHalignment(source, HPos.CENTER);
            GridPane.setHalignment(destination, HPos.CENTER);
            GridPane.setHalignment(ticketType, HPos.CENTER);
            GridPane.setHalignment(quantity, HPos.CENTER);
            GridPane.setHalignment(fare, HPos.CENTER);

            ticketsGrid.add(source, 0, i + 1);
            ticketsGrid.add(destination, 1, i + 1);
            ticketsGrid.add(ticketType, 2, i + 1);
            ticketsGrid.add(quantity, 3, i + 1);
            ticketsGrid.add(fare, 4, i + 1);

            totalFare += amount;
            totalTickets += requestedTicket.quantity();

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(header.getMinHeight());
            rowConstraints.setPrefHeight(header.getPrefHeight());
            rowConstraints.setValignment(header.getValignment());
            rowConstraints.setVgrow(header.getVgrow());
            ticketsGrid.getRowConstraints().add(rowConstraints);
        }

        // Add separator row above and below ticket entries
        BorderPane separator1 = new BorderPane();
        separator1.setPrefHeight(2);
        separator1.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        ticketsGrid.add(separator1, 0, 0, 5, 1);

        BorderPane separator2 = new BorderPane();
        separator2.setPrefHeight(2);
        separator2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        ticketsGrid.add(separator2, 0, requestedTickets.length + 1, 5, 1);

        // Row constraints for admin and total
        for (int i = 0; i < 1; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(header.getMinHeight());
            rowConstraints.setPrefHeight(header.getPrefHeight());
            rowConstraints.setValignment(header.getValignment());
            rowConstraints.setVgrow(header.getVgrow());
            ticketsGrid.getRowConstraints().add(rowConstraints);
        }

        // Administrative Charge Row
        Text adminLabel = new Text("Administrative Charge");

        Text adminAmount = new Text(EnvFile.getAdministrativeCharge());

        GridPane.setHalignment(adminLabel, HPos.CENTER);
//        GridPane.setHalignment(adminQuantity, HPos.CENTER);
        GridPane.setHalignment(adminAmount, HPos.CENTER);

        ticketsGrid.add(adminLabel, 0, requestedTickets.length + 1);
        ticketsGrid.add(new Text(), 1, requestedTickets.length + 1);
        ticketsGrid.add(new Text(), 2, requestedTickets.length + 1);
        ticketsGrid.add(new Text(), 3, requestedTickets.length + 1);
//        ticketsGrid.add(adminQuantity, 3, requestedTickets.length + 1);
        ticketsGrid.add(adminAmount, 4, requestedTickets.length + 1);

        // Total Fare Row
        Text totalLabel = new Text("Total Amount");
        Text totalQty = new Text(String.valueOf(totalTickets));
        Text totalValue = new Text("₹ " + totalFare);

        GridPane.setHalignment(totalLabel, HPos.CENTER);
        GridPane.setHalignment(totalQty, HPos.CENTER);
        GridPane.setHalignment(totalValue, HPos.CENTER);

        ticketsGrid.add(totalLabel, 0, requestedTickets.length + 2);
        ticketsGrid.add(new Text(), 1, requestedTickets.length + 2);
        ticketsGrid.add(new Text(), 2, requestedTickets.length + 2);
        ticketsGrid.add(new Text(), 3, requestedTickets.length + 2);
        ticketsGrid.add(totalValue, 4, requestedTickets.length + 2);

        this.totalFareCalculated = totalFare;

        this.setCashReceivedListener(totalFare);
        this.showGridPane();
    }
    // FREE
    public void setRequestedTicketOrderFree(RequestedTicketOrder requestedTicketOrder){
        this.cash.setVisible(false);
        this.card.setVisible(false);
        this.upi.setVisible(false);
        this.xyz.setVisible(false);
        this.cashReceived.setText("0");
        this.payAmountButton.setText("Proceed");

        // Hide all nodes in rows 0 and 1
        for (Node node : calculationGrid.getChildren()) {
            Integer row = GridPane.getRowIndex(node);
            if (row == null) row = 0;
            if (row == 0 || row == 1) {
                node.setVisible(false);
            }
        }

        Logger.info("Requested ticket order set: {} {}", requestedTicketOrder, requestedTicketOrder.requestedTicket().length);
        RequestedTicket[] requestedTickets = requestedTicketOrder.requestedTicket();
        payFor = 3;
        this.requestedTicketOrder = requestedTicketOrder;
        int noOfTickets = requestedTickets.length;
        this.orderId = requestedTicketOrder.orderId();

        for (var r : requestedTickets) {
            Logger.info("Requested ticket: {}", r);
        }

        int totalTickets = 0;

        for (int i = 0; i < noOfTickets; i++) {
            RequestedTicket requestedTicket = requestedTickets[i];

            Text source = new Text(requestedTicket.source().getStationName());
            Text destination = new Text(requestedTicket.destination().getStationName());
            Text ticketType = new Text(requestedTicket.ticketType().getTicketTypeName());
            Text quantity = new Text(String.valueOf(requestedTicket.quantity()));

            int amount = TicketType.FREE.getFareMultiplayer()
                    * requestedTicket.quantity()
                    * FareLine3.distanceMatrix[Integer.parseInt(requestedTicket.source().getStationId()) - 1]
                    [Integer.parseInt(requestedTicket.destination().getStationId()) - 1];

            Text fare = new Text(String.valueOf(amount));
            // Center align all text nodes
            GridPane.setHalignment(source, HPos.CENTER);
            GridPane.setHalignment(destination, HPos.CENTER);
            GridPane.setHalignment(ticketType, HPos.CENTER);
            GridPane.setHalignment(quantity, HPos.CENTER);
            GridPane.setHalignment(fare, HPos.CENTER);

            ticketsGrid.add(source, 0, i + 1);
            ticketsGrid.add(destination, 1, i + 1);
            ticketsGrid.add(ticketType, 2, i + 1);
            ticketsGrid.add(quantity, 3, i + 1);
            ticketsGrid.add(fare, 4, i + 1);

            totalFare += amount;
            totalTickets += requestedTicket.quantity();

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(header.getMinHeight());
            rowConstraints.setPrefHeight(header.getPrefHeight());
            rowConstraints.setValignment(header.getValignment());
            rowConstraints.setVgrow(header.getVgrow());
            ticketsGrid.getRowConstraints().add(rowConstraints);
        }

        // Add separator row above and below ticket entries
        BorderPane separator1 = new BorderPane();
        separator1.setPrefHeight(2);
        separator1.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        ticketsGrid.add(separator1, 0, 0, 5, 1);

        BorderPane separator2 = new BorderPane();
        separator2.setPrefHeight(2);
        separator2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        ticketsGrid.add(separator2, 0, requestedTickets.length + 1, 5, 1);

        // Row constraints for admin and total
        for (int i = 0; i < 1; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(header.getMinHeight());
            rowConstraints.setPrefHeight(header.getPrefHeight());
            rowConstraints.setValignment(header.getValignment());
            rowConstraints.setVgrow(header.getVgrow());
            ticketsGrid.getRowConstraints().add(rowConstraints);
        }

        // Administrative Charge Row
        Text adminLabel = new Text("Administrative Charge");

        Text adminAmount = new Text(EnvFile.getAdministrativeCharge());

        GridPane.setHalignment(adminLabel, HPos.CENTER);
//        GridPane.setHalignment(adminQuantity, HPos.CENTER);
        GridPane.setHalignment(adminAmount, HPos.CENTER);

        ticketsGrid.add(adminLabel, 0, requestedTickets.length + 1);
        ticketsGrid.add(new Text(), 1, requestedTickets.length + 1);
        ticketsGrid.add(new Text(), 2, requestedTickets.length + 1);
        ticketsGrid.add(new Text(), 3, requestedTickets.length + 1);
//        ticketsGrid.add(adminQuantity, 3, requestedTickets.length + 1);
        ticketsGrid.add(adminAmount, 4, requestedTickets.length + 1);

        // Total Fare Row
        Text totalLabel = new Text("Total Amount");
        Text totalQty = new Text(String.valueOf(totalTickets));
        Text totalValue = new Text("₹ " + totalFare);

        GridPane.setHalignment(totalLabel, HPos.CENTER);
        GridPane.setHalignment(totalQty, HPos.CENTER);
        GridPane.setHalignment(totalValue, HPos.CENTER);

        ticketsGrid.add(totalLabel, 0, requestedTickets.length + 2);
        ticketsGrid.add(new Text(), 1, requestedTickets.length + 2);
        ticketsGrid.add(new Text(), 2, requestedTickets.length + 2);
        ticketsGrid.add(new Text(), 3, requestedTickets.length + 2);
        ticketsGrid.add(totalValue, 4, requestedTickets.length + 2);

        this.totalFareCalculated = totalFare;

        this.setCashReceivedListener(totalFare);
        this.showGridPane();



    }

    @Deprecated
    public void setTicketsGrid(MetroTicket[] metroTickets, String orderId) {

        this.metroTickets = metroTickets;
        int noOfTickets = metroTickets.length;
        this.orderId = orderId;

        for (int i = 0; i < metroTickets.length; i++) {
            MetroTicket metroTicket = metroTickets[i];

            Text source = new Text(metroTicket.getSourceName());
            Text destination = new Text(metroTicket.getDestinationName());
            Text ticketType = new Text(metroTicket.getTicketType().getTicketTypeName());
            Text quantity = new Text(String.valueOf(metroTicket.getTicketQuantity()));
            Text fare = new Text(String.valueOf(metroTicket.getFare()));

            // Apply style classes
//            source.getStyleClass().add("grid-text-large");
//            destination.getStyleClass().add("grid-text-large");
//            ticketType.getStyleClass().add("grid-text-large");
//            quantity.getStyleClass().add("grid-text-large");
//            fare.getStyleClass().add("grid-text-large");

            // Center align all text nodes
            GridPane.setHalignment(source, HPos.CENTER);
            GridPane.setHalignment(destination, HPos.CENTER);
            GridPane.setHalignment(ticketType, HPos.CENTER);
            GridPane.setHalignment(quantity, HPos.CENTER);
            GridPane.setHalignment(fare, HPos.CENTER);

            ticketsGrid.add(source, 0, i + 1);
            ticketsGrid.add(destination, 1, i + 1);
            ticketsGrid.add(ticketType, 2, i + 1);
            ticketsGrid.add(quantity, 3, i + 1);
            ticketsGrid.add(fare, 4, i + 1);

            totalFare += metroTicket.getFare();

            // Match existing row constraints
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(header.getMinHeight());
            rowConstraints.setPrefHeight(header.getPrefHeight());
            rowConstraints.setValignment(header.getValignment());
            rowConstraints.setVgrow(header.getVgrow());
            ticketsGrid.getRowConstraints().add(rowConstraints);
        }

        // Add separator before data
        BorderPane separator1 = new BorderPane();
        separator1.setPrefHeight(2);
        separator1.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        ticketsGrid.add(separator1, 0, 0, 5, 1);

        // Add separator after data
        BorderPane separator2 = new BorderPane();
        separator2.setPrefHeight(2);
        separator2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        ticketsGrid.add(separator2, 0, metroTickets.length + 1, 5, 1);

        // Total Fare row
        Text totalFareLabel = new Text("Total Fare");
        Text totalQuantity = new Text(String.valueOf(metroTickets.length));
        Text totalFareValue = new Text("₹ " + totalFare);

        totalFareLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        totalFareValue.setFont(Font.font("Arial", FontWeight.BOLD, 12));

//        totalFareLabel.getStyleClass().add("grid-text-bold");
//        totalQuantity.getStyleClass().add("grid-text-large");
//        totalFareValue.getStyleClass().add("grid-text-bold");

        // Center align all
        GridPane.setHalignment(totalFareLabel, HPos.CENTER);
        GridPane.setHalignment(totalQuantity, HPos.CENTER);
        GridPane.setHalignment(totalFareValue, HPos.CENTER);

        ticketsGrid.add(totalFareLabel, 0, metroTickets.length + 1);
        ticketsGrid.add(totalQuantity, 3, metroTickets.length + 1);
        ticketsGrid.add(totalFareValue, 4, metroTickets.length + 1);

        this.totalFareCalculated = totalFare;
        this.setCashReceivedListener(totalFare);
        this.showGridPane();
    }
    //ADJUSTMENT
    public void setTicketsPantylinerGrid(TicketInfo[] metroTickets, String orderId) {
        payFor = 1;
        this.ticketInfos = metroTickets;
        int noOfTickets = metroTickets.length;
        this.orderId = orderId;
        this.requestedTicketOrder= new RequestedTicketOrder(null,agent.getShiftIdGeneratorService().getOrderIdGeneratorService().generateOrderId());

        for (int i = 0; i < noOfTickets; i++) {
            TicketInfo metroTicket = metroTickets[i];

            Text source = new Text(metroTicket.getQrTicketV2().getInStation().getStationName());
            Text destination = new Text(metroTicket.getQrTicketV2().getOutStation().getStationName());
            Text ticketType = new Text(metroTicket.getQrTicketV2().getTicketType().getTicketTypeName());
            Text quantity = new Text(String.valueOf(metroTicket.getQrTicketV2().getQuantity()));
            Text fare = new Text(String.valueOf(metroTicket.getPAmount()));


            // Center alignment
            GridPane.setHalignment(source, HPos.CENTER);
            GridPane.setHalignment(destination, HPos.CENTER);
            GridPane.setHalignment(ticketType, HPos.CENTER);
            GridPane.setHalignment(quantity, HPos.CENTER);
            GridPane.setHalignment(fare, HPos.CENTER);

            ticketsGrid.add(source, 0, i + 1);
            ticketsGrid.add(destination, 1, i + 1);
            ticketsGrid.add(ticketType, 2, i + 1);
            ticketsGrid.add(quantity, 3, i + 1);
            ticketsGrid.add(fare, 4, i + 1);

            totalFare += metroTicket.getPAmount();

            // Set row constraints
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(header.getMinHeight());
            rowConstraints.setPrefHeight(header.getPrefHeight());
            rowConstraints.setValignment(header.getValignment());
            rowConstraints.setVgrow(header.getVgrow());
            ticketsGrid.getRowConstraints().add(rowConstraints);
        }

        // Optional: Separators
        BorderPane separator1 = new BorderPane();
        separator1.setPrefHeight(2);
        separator1.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        BorderPane separator2 = new BorderPane();
        separator2.setPrefHeight(2);
        separator2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        // Uncomment if separators needed
        // ticketsGrid.add(separator1, 0, 0, 5, 1);
        // ticketsGrid.add(separator2, 0, noOfTickets + 1, 5, 1);

        // Row constraints for extra rows
        for (int i = 0; i < 1; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(header.getMinHeight());
            rowConstraints.setPrefHeight(header.getPrefHeight());
            rowConstraints.setValignment(header.getValignment());
            rowConstraints.setVgrow(header.getVgrow());
            ticketsGrid.getRowConstraints().add(rowConstraints);
        }

        // Administrative Charge
        Text adminLabel = new Text("Administrative Charge");
        Text adminQty = new Text("0");
        Text adminAmt = new Text("0");

        adminLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        adminAmt.setFont(Font.font("Arial", FontWeight.BOLD, 12));


        GridPane.setHalignment(adminLabel, HPos.CENTER);
        GridPane.setHalignment(adminQty, HPos.CENTER);
        GridPane.setHalignment(adminAmt, HPos.CENTER);

        ticketsGrid.add(adminLabel, 0, noOfTickets + 1);
        ticketsGrid.add(new Text(), 1, noOfTickets + 1);
        ticketsGrid.add(new Text(), 2, noOfTickets + 1);
        ticketsGrid.add(adminQty, 3, noOfTickets + 1);
        ticketsGrid.add(adminAmt, 4, noOfTickets + 1);

        // Total Amount
        Text totalLabel = new Text("Total Amount");
        Text totalQty = new Text(String.valueOf(noOfTickets));
        Text totalVal = new Text("₹ " + totalFare);

        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        totalVal.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        GridPane.setHalignment(totalLabel, HPos.CENTER);
        GridPane.setHalignment(totalQty, HPos.CENTER);
        GridPane.setHalignment(totalVal, HPos.CENTER);

        ticketsGrid.add(totalLabel, 0, noOfTickets + 2);
        ticketsGrid.add(new Text(), 1, noOfTickets + 2);
        ticketsGrid.add(new Text(), 2, noOfTickets + 2);
        ticketsGrid.add(totalQty, 3, noOfTickets + 2);
        ticketsGrid.add(totalVal, 4, noOfTickets + 2);

        this.totalFareCalculated = totalFare;
        this.setCashReceivedListener(totalFare);
        this.showGridPane();
    }

    @FXML
    void onAmountUpdate(InputMethodEvent event) {
    }

    @FXML
    void onBalanceUpdate(ActionEvent event) {
        System.out.println("Balance updated");

    }

    @FXML
    void onClickPay(ActionEvent event) {

        if (Integer.parseInt(cashReceived.getText()) < totalFare) {
            alert.setContentText("Insufficient cash received");
            alert.showAndWait();
            throw new RuntimeException("Insufficient cash received");
        }

        // 🔧 Create overlay at runtime
        VBox overlayContent = new VBox(15);
        overlayContent.setAlignment(Pos.CENTER);
        overlayContent.setPrefSize(stackPane.getWidth(), stackPane.getHeight());

        Label label = new Label("🖨 Printing Tickets...");
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

        ProgressIndicator spinner = new ProgressIndicator();

        overlayContent.getChildren().addAll(label, spinner);

        AnchorPane overlayPane = new AnchorPane(overlayContent);
        overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        AnchorPane.setTopAnchor(overlayContent, 0.0);
        AnchorPane.setBottomAnchor(overlayContent, 0.0);
        AnchorPane.setLeftAnchor(overlayContent, 0.0);
        AnchorPane.setRightAnchor(overlayContent, 0.0);

        // Add to stackPane
        stackPane.getChildren().add(overlayPane);

        // Simulate printing delay
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            stackPane.getChildren().remove(overlayPane);
            // TODO: Final transaction logic here
//
//
//        if(FareMedium.QR.getFareMediumTotal()-FareMedium.QR.getFareMediumSale() -metroTickets.length <=0){
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Error");
//            alert.setHeaderText(null);
//            alert.setContentText("Insufficient Stock");
//            alert.show();
//            throw new RuntimeException("Insufficient Stock");
//        }
//
//        if(!PrinterStatus.getPrinterStatus()) { TODO: check for printer
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Error");
//            alert.setHeaderText(null);
//            alert.setContentText("Printer not connected");
//            alert.show();
//            throw new RuntimeException("Printer not connected");
//        }

            if (payFor == 1) {
                AbstractPaymentMethod selectedPayment = switch (paymentMethod) {
                    case CASH -> new CashPaymentMethod();
                    case CARD -> new UPIPaymentMethod();
                    case UPI -> new UPIPaymentMethod();
                    case NETBANKING -> new UPIPaymentMethod();
                };

                PaymentResponse paymentResponse = (PaymentResponse) PaymentFactory.getPaymentMedia(selectedPayment).pay(totalFareCalculated, this.requestedTicketOrder.orderId(), null);

                this.ticketInfos[0].setPreGeneratadTicket(new PreGeneratadTicket().setPaymentResponse(paymentResponse));

                QRTicketService qrTicketService = QRTicketFactory.getQRService(new AbstractQRTicketAdjustment(), agent);

                ArrayList<GeneratedTicket> generatedTicket = qrTicketService.processTicket(this.orderId, paymentResponse.getTransactionId(), this.ticketInfos[0]);

                FXMLLoader fxmlLoader2 = getFreeTicketPrint();

                fxmlLoader2.setControllerFactory((x) -> new FreeTicketPrintController(generatedTicket, paymentResponse, agent));

                try {
                    ControllerAdapter.INSTANCE.setChildInCenterAnchorPane(fxmlLoader2.load());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            } else  {
                AbstractPaymentMethod selectedPayment = switch (paymentMethod) {
                    case CASH -> new CashPaymentMethod();
                    case CARD -> new UPIPaymentMethod();
                    case UPI -> new UPIPaymentMethod();
                    case NETBANKING -> new UPIPaymentMethod();
                };


                List<ProperTicket> properTickets = new ArrayList<>(List.of());
                AtomicInteger totalFare = new AtomicInteger();

                for (RequestedTicket requestedTicket : requestedTicketOrder.requestedTicket()) {
                    long issuedAt = Instant.now().toEpochMilli();
                    long validUntil = (long) requestedTicket.ticketType().getTicketTime() * 60 * 1000 + issuedAt;
                    int fareS2D = this.getFare(requestedTicket); //getting fare from fare matrix of single ticket
                    List<ProperTicket> subProperTickets = this.getProperTickets(requestedTicket, issuedAt, validUntil, fareS2D);
                    subProperTickets.forEach(properTicket -> {
                        Logger.info("Sub Proper ticket: {} {}", properTicket.getTicketType(), properTicket.getPrice());
                        totalFare.addAndGet(properTicket.getPrice());
                        Logger.info("updated fare {}", totalFare.get());
                    });
                    properTickets.addAll(subProperTickets);
                }
                Logger.info("Total fare to be pay: {}", totalFare);

                if (totalFare.get() != totalFareCalculated) {
                    Logger.error("Total fare mismatched");
                    throw new RuntimeException("Total fare mismatched");
                }

                PaymentResponse paymentResponse = (PaymentResponse) PaymentFactory.getPaymentMedia(selectedPayment).pay(totalFareCalculated, requestedTicketOrder.orderId(), null);

                Logger.info("Payment response: {}", paymentResponse);

                ProperTicketOrder properTicketOrder = new ProperTicketOrder(properTickets.toArray(new ProperTicket[0]), totalFare.get(), requestedTicketOrder.orderId());

                QRTicketService qrTicketService = QRTicketFactory.getQRService(new AbstractQRTicketGenerator(), agent);

                TicketInfo ticketInfo = new TicketInfo();

                //TODO: Add proper ticket order and payment response to ticket info
                ticketInfo.setPreGeneratadTicket(new PreGeneratadTicket().setProperTicketOrder(properTicketOrder).setPaymentResponse(paymentResponse));
                ticketInfo.setPAmount(paymentResponse.getAmount());

                ArrayList<GeneratedTicket> generatedTicket = qrTicketService.processTicket(this.orderId, paymentResponse.getTransactionId(), ticketInfo);

                generatedTicket.forEach(generatedTicket1 -> {
                    Logger.info("Generated ticket: {}", generatedTicket1);
                });

                FXMLLoader fxmlLoader2 = getFreeTicketPrint();

                fxmlLoader2.setControllerFactory((x) -> new FreeTicketPrintController(generatedTicket, paymentResponse, agent));

                try {
                    ControllerAdapter.INSTANCE.setChildInCenterAnchorPane(fxmlLoader2.load());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }


           /* } else {

                payment = switch (paymentMethod) {
                    case CASH -> new CashPay(this, ticketService, agent.getScuService());
                    case CARD -> new QRPayment(this, ticketService, agent.getScuService());
                    case UPI -> new QRPayment(this, ticketService, agent.getScuService());
                    case NETBANKING -> new QRPayment(this, ticketService, agent.getScuService());
                };

                BufferedImage bufferedImage = payment.pay(cashReceived.isDisabled() ? totalFare : Integer.parseInt(cashReceived.getText()), metroTickets, orderId);

                boolean isFreeTicket = metroTickets[0].getTicketType().equals(TicketType.FREE);


                try {

                    FXMLLoader fxmlLoader2 = getFreeTicketPrint();

                    // Load the FXML file and get the Parent node
                    Parent root = fxmlLoader2.load();

                    FreeTicketPrintController controller = fxmlLoader2.getController();
                    controller.setData(bufferedImage);

                    //TODO:Print the ticket

                    if (isFreeTicket) {

                        ((VBox) borderPane.getParent().getParent()).getChildren().setAll(root);

                    } else {
                        ControllerAdapter.INSTANCE.setChildInCenterAnchorPane(root);
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }*/

        }});
        pause.play();
        event.consume();

    }



    private List<ProperTicket> getProperTickets(RequestedTicket requestedTicket, long issuedAt, long validUntil, int fare) {
        int quantity = requestedTicket.quantity();
        Logger.info("Requested ticket quantity: {}", requestedTicket.quantity());
        TicketType ticketType = requestedTicket.ticketType();
        Station source = requestedTicket.source();
        Station destination = requestedTicket.destination();

        List<ProperTicket> properTickets = new ArrayList<>(quantity);

        if (ticketType.equals(TicketType.GROUP)) {
            properTickets.add(ProperTicket.builder().source(source).destination(destination).ticketType(ticketType).quantity(quantity).price(fare * quantity).issuedAt(issuedAt).validUntil(validUntil).build());
        } else if (ticketType.equals(TicketType.RETURN)) {
            for (int i = 0; i < quantity; i++) {
                properTickets.add(ProperTicket.builder().source(source).destination(destination).ticketType(ticketType).quantity(1).price(fare)//TODO: fare for return ticket ,it will be 2*fare of single ticket for now,need to change
                        .issuedAt(issuedAt).validUntil(validUntil).build());
            }
        } else {
            for (int i = 0; i < quantity; i++) {
                properTickets.add(ProperTicket.builder().source(source).destination(destination).ticketType(ticketType).quantity(1).price(fare).issuedAt(issuedAt).validUntil(validUntil).build());
            }
        }

        return properTickets;
    }

    private void setCashReceivedListener(int finalTotalFare) {
        cashReceived.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                receivedCash = Integer.parseInt(newValue);
                changedCash = (Integer.parseInt(newValue) - finalTotalFare);

                changeAmount.setText(String.valueOf((Integer.parseInt(newValue) - finalTotalFare)));
                if (Integer.parseInt(newValue) < finalTotalFare) throw new NumberFormatException();

                if (cashReceived.getStyle().equals("-fx-border-color: red")) cashReceived.setStyle("");

            } catch (NumberFormatException e) {
                cashReceived.setStyle("-fx-border-color: red");
                changeAmount.setText("0");
            }
        });
    }

    public void setParent(BorderPane borderPane) {
        this.borderPane = borderPane;
    }

    @Override
    public boolean waitForPayment() {
        ControllerAdapter.INSTANCE.addWaitForPayment();
        return true;
    }

    @Override
    public void setParentNode(Controller controller) {
        this.controller = controller;
    }

    @FXML
    private void onClickCancel(ActionEvent actionEvent) {
        try {
            // Clear any pending payment state
//            if (payment != null) {
//                payment = null;
//            }

            // Reset payment method
            paymentMethod = PaymentMethod.CASH;

            // Clear the cash received field
            if (cashReceived != null) {
                cashReceived.clear();
                cashReceived.setStyle("");
            }

            // Clear the change amount
            if (changeAmount != null) {
                changeAmount.setText("0");
            }

            // Reset total fare
            totalFareCalculated = 0;


//            this.controller.qrTicket(null);
            if( previousNode != null) {
                PDUCommandDispatcher.INSTANCE.dispatch(new WelcomePageCommand());
                // Navigate back to the previous node
                ControllerAdapter.INSTANCE.setChildInCenterAnchorPane((Parent) previousNode);
            } else {
                throw new RuntimeException("Previous node is null, cannot navigate back to home screen");}

            // Log the navigation
            Logger.info("Navigated back to home screen from payment screen");

        } catch (RuntimeException | IOException  e) {
            Logger.error("Error navigating back to home screen: {}", e);
            // Show error alert to user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not return to previous screen");
            alert.setContentText("Please try again or contact support if the problem persists.");
            alert.showAndWait();
        }

        actionEvent.consume();
    }

    private Node previousNode;


    public void setParentNode(Node center) {
        this.previousNode=center;
    }




@Deprecated
    // Fare calculation on a basis of matrix
    private int getFare(RequestedTicket requestedTicket) {

        int FARE_MULTIPLAYER=requestedTicket.ticketType().getFareMultiplayer();
        int source= Integer.parseInt(requestedTicket.destination().getStationId());
        int destination=Integer.parseInt(requestedTicket.source().getStationId());
//        Logger.info("Fare Multiplayer {} source {} destination {}",requestedTicket.ticketType().getFareMultiplayer(),source,destination);
        final int ticketPrice= switch (requestedTicket.ticketType()) {
            case SINGLE,RETURN,GROUP,TEST,FREE ->
            (int)(agent.getBusinessRule().getFareMultiplayer()
            *FareLine3.distanceMatrix[ source - 1] [destination - 1]);
            case PAID ->FareLine3.distanceMatrix[0][FareLine3.distanceMatrix.length - 1]; // TODO: FATEMULTIPLAYER IS NOT appliwd
            default -> throw new NotValidTicketToCalculateFare("Not a valid ticket type");
        };
        // Multiply by default fare multiplayer
        Logger.info("Ticket price for {} {}: {}", requestedTicket.source().getStationName(), requestedTicket.destination().getStationName(), ticketPrice);
        return ticketPrice * FARE_MULTIPLAYER;
    }


    private int getFare(TicketType ticketType,int fareMultiplayer,int source,int destination,int  timeFareMultiplayer) {
        int FARE_MULTIPLAYER=fareMultiplayer;

        Logger.info("Fare Multiplayer {} source {} destination {}",FARE_MULTIPLAYER,source,destination);
        final int ticketPrice= switch (ticketType) {
            case SINGLE,RETURN,GROUP,TEST,FREE ->
                    (timeFareMultiplayer
                            *FareLine3.distanceMatrix[ source - 1] [destination - 1]);
            case PAID ->FareLine3.distanceMatrix[0][FareLine3.distanceMatrix.length - 1]; // TODO: FATEMULTIPLAYER IS NOT appliwd
            default -> throw new NotValidTicketToCalculateFare("Not a valid ticket type");
        };
        return ticketPrice * FARE_MULTIPLAYER;
    }


}
