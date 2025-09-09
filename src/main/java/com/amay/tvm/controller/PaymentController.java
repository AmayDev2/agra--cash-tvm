package com.amay.tvm.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.enums.PaymentMethod;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.station.Station;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.model.tickets.*;
import com.amay.tom.repository.FareLine3;
import com.amay.tom.repository.StationData;
import com.amay.tom.service.payment2.AbstractPaymentMethod;
import com.amay.tom.service.payment2.PaymentFactory;
import com.amay.tom.service.payment2.absimpl.CashPaymentMethod;
import com.amay.tom.service.payment2.absimpl.UPIPaymentMethod;
import com.amay.tom.service.qrService2.AbstractQRTicketGenerator;
import com.amay.tom.service.qrService2.QRTicketFactory;
import com.amay.tom.service.qrService2.QRTicketService;
import com.amay.tom.service.qrService2.TicketInfo;

import com.amay.utils.TicketUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.tinylog.Logger;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PaymentController {

    @FXML private Label minTicketSize;
    @FXML private Label maxTicketSize;
    @FXML private Button btnConfirm;
    @FXML private Button btnAddTicket;
    @FXML private Button bkspc;
    @FXML private TextField display;
    @FXML private GridPane numpad;
    @FXML private Button btn0;
    @FXML private Button btn3;
    @FXML private Button btn2;
    @FXML private Button btn1;
    @FXML private Button btn6;
    @FXML private Button btn5;
    @FXML private Button btn4;
    @FXML private Button btn9;
    @FXML private Button btn8;
    @FXML private Button btn7;
    @FXML private Button btnSubmit;
    @FXML private Button btnBack;
    // FXML UI Components
    @FXML private Label labelFrom;
    @FXML private Label labelTo;
    @FXML private Label labelType;
    @FXML private Label labelCount;
    @FXML private Label labelFare;

    // Payment Method Toggle Buttons - Changed to ToggleButton
    @FXML private ToggleButton onClickCard;
    @FXML private ToggleButton onClickUPI;
    @FXML private ToggleButton onClickCash;

    // Toggle Group for mutual exclusion
    private ToggleGroup paymentToggleGroup;

    // Core application components
    private StackPane stackPane;
    private BorderPane borderPane;
    private Agent agent;
    private StationData stationData;

    // Ticket information
    private Station selectedDestination;
    private TicketType ticketType;
    private int quantity;
    private int fare;
    private PaymentMethod paymentMethod;
    private RequestedTicket requestedTicket;

    // Payment processing
    private AbstractPaymentMethod selectedPayment = new UPIPaymentMethod(); // Default
    private String orderId;
    private int minTicket;
    private int maxTicket;

    /**
     * Main constructor with full parameters
     */
    public PaymentController(StackPane stackPane, BorderPane borderPane, Agent agent,
                             StationData stationData, Station selectedDestination,
                             TicketType ticketType ,int fare
    ) {
        this.stackPane = stackPane;
        this.borderPane = borderPane;
        this.agent = agent;
        this.stationData = stationData;
        this.selectedDestination = selectedDestination;
        this.ticketType = ticketType;
//        this.quantity = quantity;
        this.fare = fare;

        if (SystemConfig.getInstance().getCurrentStation() != null && selectedDestination != null) {
            this.requestedTicket = new RequestedTicket(
                    SystemConfig.getInstance().getCurrentStation(),
                    selectedDestination,
                    ticketType,
                    TicketUtils.getMinTicket(this.ticketType)
            );
        }
    }

    private void buttonsDisability(boolean isDisable){
        Platform.runLater(()-> {
            btnBack.setDisable(isDisable);
            btnSubmit.setDisable(isDisable);
        });
    }

    /**
     * Simple constructor for navigation
     */
    public PaymentController(StackPane stackPane, BorderPane borderPane) {
        this.stackPane = stackPane;
        this.borderPane = borderPane;
    }

    /**
     * Initialize UI components and setup ToggleGroup
     */
    @FXML
    void initialize() {
        try {
            // Initialize UI labels
            if (SystemConfig.getInstance().getCurrentStation() != null && selectedDestination != null) {

                this.quantity=TicketUtils.getMinTicket(ticketType);

                this.labelCount.setText(String.valueOf(this.quantity));
                this.labelFare.setText("₹ " + (fare*quantity));
                this.labelFrom.setText(SystemConfig.getInstance().getCurrentStation().getStationName());
                this.labelTo.setText(this.selectedDestination.getStationName());
                this.labelType.setText(this.ticketType.getTicketTypeName());
                //Initialize the numpad display with min ticket count value
                this.display.setText(String.valueOf(quantity));
                this.minTicketSize.setText("Min : "+TicketUtils.getMinTicket(ticketType));
                this.maxTicketSize.setText("Max : "+TicketUtils.getMaxTicket(ticketType));

            }



            //define min and max ticket for the ticket type selected
            minTicket = TicketUtils.getMinTicket(ticketType);
            maxTicket = TicketUtils.getMaxTicket(ticketType);

            // Setup ToggleGroup for mutual exclusion
            setupToggleGroup();

            this.paymentMethod = null;
            Logger.info("PaymentController initialized successfully");

        } catch (Exception e) {
            Logger.error("Error initializing PaymentController: {}", e.getMessage());
        }
    }

    /**
     * Setup ToggleGroup for payment method buttons
     */
    private void setupToggleGroup() {
        try {
            paymentToggleGroup = new ToggleGroup();

            // Add all toggle buttons to the group
            if (onClickCard != null) {
                onClickCard.setToggleGroup(paymentToggleGroup);
            }
            if (onClickUPI != null) {
                onClickUPI.setToggleGroup(paymentToggleGroup);
            }
            if (onClickCash != null) {
                onClickCash.setToggleGroup(paymentToggleGroup);
            }

            // Add listener to handle selection changes
            paymentToggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
                if (newToggle != null) {
                    ToggleButton selectedButton = (ToggleButton) newToggle;
                    Logger.info("Payment method toggle changed to: {}", selectedButton.getId());
                }
            });

            Logger.info("ToggleGroup setup completed");

        } catch (Exception e) {
            Logger.error("Error setting up toggle group: {}", e.getMessage());
        }
    }

    /**
     * Handle Cash payment method selection
     */
    @FXML
    private void handleCash(ActionEvent actionEvent) {
        try {
            Logger.info("Cash payment method selected");

            // ToggleButton automatically handles selection state
            if (onClickCash.isSelected()) {
                this.paymentMethod = PaymentMethod.CASH;
                this.payType(this.paymentMethod);
            } else {
                // If deselected, clear payment method
                this.paymentMethod = null;
                this.selectedPayment = new UPIPaymentMethod(); // Reset to default
            }

        } catch (Exception e) {
            Logger.error("Error handling cash selection: {}", e.getMessage());
        } finally {
            actionEvent.consume();
        }
    }

    /**
     * Handle UPI payment method selection
     */
    @FXML
    private void handleUPI(ActionEvent actionEvent) {
        try {
            Logger.info("UPI payment method selected");

            if (onClickUPI.isSelected()) {
                this.paymentMethod = PaymentMethod.UPI;
                this.payType(this.paymentMethod);
            } else {
                this.paymentMethod = null;
                this.selectedPayment = new UPIPaymentMethod(); // Reset to default
            }

        } catch (Exception e) {
            Logger.error("Error handling UPI selection: {}", e.getMessage());
        } finally {
            actionEvent.consume();
        }
    }

    /**
     * Handle Card payment method selection
     */
    @FXML
    private void handleCard(ActionEvent actionEvent) {
        try {
            Logger.info("Card payment method selected");

            if (onClickCard.isSelected()) {
                this.paymentMethod = PaymentMethod.CARD;
                this.payType(this.paymentMethod);
            } else {
                this.paymentMethod = null;
                this.selectedPayment = new UPIPaymentMethod(); // Reset to default
            }

        } catch (Exception e) {
            Logger.error("Error handling card selection: {}", e.getMessage());
        } finally {
            actionEvent.consume();
        }
    }

    /**
     * Configure payment method implementation
     */
    private void payType(PaymentMethod paymentMethod) {
        try {
            this.selectedPayment = switch (paymentMethod) {
                case CASH -> {
                    Logger.info("Configuring Cash payment method");
                    yield new CashPaymentMethod();
                }
                case CARD -> {
                    Logger.info("Configuring Card payment method");
                    yield new UPIPaymentMethod(); // Use actual CardPaymentMethod if available
                }
                case UPI -> {
                    Logger.info("Configuring UPI payment method");
                    yield new UPIPaymentMethod();
                }
                case NETBANKING -> {
                    Logger.info("Configuring Net Banking payment method");
                    yield new UPIPaymentMethod(); // Fallback
                }
                default -> {
                    Logger.warn("Unknown payment method, defaulting to UPI");
                    yield new UPIPaymentMethod();
                }
            };
            Logger.info("Payment method configured: {}", this.selectedPayment.getClass().getSimpleName());
        } catch (Exception e) {
            Logger.error("Error configuring payment method: {}", e.getMessage());
            this.selectedPayment = new UPIPaymentMethod(); // Safe fallback
        }
    }

    /**
     * Get currently selected payment method from ToggleGroup
     */
    private PaymentMethod getSelectedPaymentMethod() {
        if (paymentToggleGroup.getSelectedToggle() == null) {
            return null;
        }

        ToggleButton selectedButton = (ToggleButton) paymentToggleGroup.getSelectedToggle();

        if (selectedButton == onClickCash) {
            return PaymentMethod.CASH;
        } else if (selectedButton == onClickUPI) {
            return PaymentMethod.UPI;
        } else if (selectedButton == onClickCard) {
            return PaymentMethod.CARD;
        }

        return null;
    }

    @FXML
    private void confirmTicket(){
        if (SystemConfig.getInstance().getCurrentStation() != null && selectedDestination != null) {
            this.requestedTicket = new RequestedTicket(
                    SystemConfig.getInstance().getCurrentStation(),
                    selectedDestination,
                    ticketType,
                    quantity
            );
        }
        labelCount.setText(display.getText());
        labelFare.setText("₹ " + (fare*quantity));
    }

    /**
     * Confirm selection and process payment
     */
    @FXML
    private void confirmSelection(ActionEvent actionEvent) {
        try {
            // Get current selection from ToggleGroup
            PaymentMethod currentPaymentMethod =  getSelectedPaymentMethod();

            if (currentPaymentMethod == null) {
                Logger.error("No payment method selected");
                // TODO: Show user error message
                return;
            }

            // Update payment method if it changed
            if (this.paymentMethod != currentPaymentMethod) {
                this.paymentMethod = currentPaymentMethod;
                this.payType(this.paymentMethod);
            }

            if (this.selectedPayment == null) {
                Logger.error("Payment method not properly configured");
                this.payType(this.paymentMethod);
            }

            if (this.requestedTicket == null) {
                Logger.error("No ticket request available");
                return;
            }
//            buttonsDisability(true);
//
            Logger.info("Confirming payment selection: {}", this.paymentMethod);
            this.createTicketRequest(requestedTicket);

        } catch (Exception e) {
            Logger.error("Unexpected error during confirmation: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            actionEvent.consume();
        }
    }
    /**
     * Navigate back to home page
     */
    @FXML
    private void navigateToHomePage(ActionEvent actionEvent) {
        try {
            Logger.info("Navigating back to home page");

            // Clear any selections when going back
            if (paymentToggleGroup != null) {
                paymentToggleGroup.selectToggle(null);
            }

            int count = this.stackPane.getChildren().size();
            Platform.runLater(() -> {
                if (count > 0) {
                    this.stackPane.getChildren().remove(count - 1);
                    this.borderPane.setCenter(this.stackPane);
                }
            });
        } catch (Exception e) {
            Logger.error("Error navigating to home page: {}", e.getMessage());
        } finally {
            actionEvent.consume();
        }
    }

    /**
     * Programmatically select a payment method (utility method)
     */
    public void selectPaymentMethod(PaymentMethod method) {
        try {
            switch (method) {
                case CASH -> {
                    if (onClickCash != null) {
                        onClickCash.setSelected(true);
                    }
                }
                case UPI -> {
                    if (onClickUPI != null) {
                        onClickUPI.setSelected(true);
                    }
                }
                case CARD -> {
                    if (onClickCard != null) {
                        onClickCard.setSelected(true);
                    }
                }
            }
            this.paymentMethod = method;
            this.payType(method);
        } catch (Exception e) {
            Logger.error("Error programmatically selecting payment method: {}", e.getMessage());
        }
    }

    /**
     * Clear all payment method selections
     */
    public void clearPaymentSelection() {
        try {
            if (paymentToggleGroup != null) {
                paymentToggleGroup.selectToggle(null);
            }
            this.paymentMethod = null;
            this.selectedPayment = new UPIPaymentMethod(); // Reset to default
            Logger.info("Payment selection cleared");
        } catch (Exception e) {
            Logger.error("Error clearing payment selection: {}", e.getMessage());
        }
    }

    // ... [Keep all the remaining methods unchanged: createTicketRequest, getProperTickets, getFare, pay, processTicketsAndNavigate]

    /**
     * Create ticket request with validation
     */
    public void createTicketRequest(RequestedTicket requestedTicket) throws IOException {
        try {
            if (requestedTicket == null) {
                throw new IllegalArgumentException("Requested ticket cannot be null");
            }

            RequestedTicket[] requestedTicketArray = new RequestedTicket[1];
            requestedTicketArray[0] = requestedTicket;

            String generatedOrderId = agent.getShiftIdGeneratorService()
                    .getOrderIdGeneratorService()
                    .generateOrderId();

            RequestedTicketOrder requestedTicketOrder = new RequestedTicketOrder(
                    requestedTicketArray,
                    generatedOrderId
            );

            // Cache last order id
            this.agent.getSystemConfig().setLastOrderId(requestedTicketOrder.orderId());
            Logger.info("Creating ticket request with order id: {}", requestedTicketOrder.orderId());

            this.pay(requestedTicketOrder);

        } catch (Exception e) {
            Logger.error("Error creating ticket request: {}", e.getMessage());
            throw new IOException("Failed to create ticket request", e);
        }
    }

    /**
     * Generate proper tickets with validation
     */
    private List<ProperTicket> getProperTickets(RequestedTicket requestedTicket,
                                                long issuedAt, long validUntil, int fare) {
        if (requestedTicket == null) {
            throw new IllegalArgumentException("Requested ticket cannot be null");
        }

        int quantity = requestedTicket.quantity();
        Logger.info("Requested ticket quantity: {}", quantity);

        TicketType ticketType = requestedTicket.ticketType();
        Station source = requestedTicket.source();
        Station destination = requestedTicket.destination();

        List<ProperTicket> properTickets = new ArrayList<>(quantity);

        try {
            if (ticketType.equals(TicketType.GROUP)) {
                properTickets.add(ProperTicket.builder()
                        .source(source)
                        .destination(destination)
                        .ticketType(ticketType)
                        .quantity(quantity)
                        .price(fare * quantity)
                        .issuedAt(issuedAt)
                        .validUntil(validUntil)
                        .build());

            } else if (ticketType.equals(TicketType.RETURN)) {
                for (int i = 0; i < quantity; i++) {
                    properTickets.add(ProperTicket.builder()
                            .source(source)
                            .destination(destination)
                            .ticketType(ticketType)
                            .quantity(1)
                            .price(fare * 2) // Return ticket is 2x fare
                            .issuedAt(issuedAt)
                            .validUntil(validUntil)
                            .build());
                }
            } else {
                // Single ticket
                for (int i = 0; i < quantity; i++) {
                    properTickets.add(ProperTicket.builder()
                            .source(source)
                            .destination(destination)
                            .ticketType(ticketType)
                            .quantity(1)
                            .price(fare)
                            .issuedAt(issuedAt)
                            .validUntil(validUntil)
                            .build());
                }
            }
        } catch (Exception e) {
            Logger.error("Error generating proper tickets: {}", e.getMessage());
            throw new RuntimeException("Failed to generate proper tickets", e);
        }

        return properTickets;
    }

    /**
     * Calculate fare with error handling
     */
    private int getFare(RequestedTicket requestedTicket) {
        try {
            if (requestedTicket == null || requestedTicket.source() == null || requestedTicket.destination() == null) {
                throw new IllegalArgumentException("Invalid ticket request for fare calculation");
            }

            int sourceId = Integer.parseInt(requestedTicket.source().getStationId()) - 1;
            int destId = Integer.parseInt(requestedTicket.destination().getStationId()) - 1;

            int ticketPrice = FareLine3.distanceMatrix[sourceId][destId];

            Logger.info("Ticket price for {} to {}: {}",
                    requestedTicket.source().getStationName(),
                    requestedTicket.destination().getStationName(),
                    ticketPrice);

            return ticketPrice;

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Logger.error("Error calculating fare: {}", e.getMessage());
            throw new RuntimeException("Failed to calculate fare", e);
        }
    }

    private void showWaiting() {

//        // Create spinner
//        ProgressIndicator spinner = new ProgressIndicator();
//        spinner.setStyle(
//                "-fx-progress-color: cyan;" +  // updated color
//                        "-fx-scale-x: 8;" +
//                        "-fx-scale-y: 8;"
//        );
//
//        // Create text label
//        Label loadingText = new Label("Generating Tickets...");
//        loadingText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: gold;"); // complementary color
//
//        // Put both inside a StackPane
//        StackPane spinnerWithText = new StackPane(spinner, loadingText);
//        StackPane.setAlignment(loadingText, Pos.CENTER);
//
//        // Optional: semi-transparent background
//        spinnerWithText.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
//
//        // Add overlay
//        Platform.runLater(()->this.stackPane.getChildren().add(spinnerWithText));
        FXMLLoader fxmlLoader = ViewFactory.getTxnProcess();

        Platform.runLater(() -> {
            try {
                this.stackPane.getChildren().add(fxmlLoader.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }



    /**
     * Process payment with comprehensive error handling
     */
    private void pay(RequestedTicketOrder requestedTicketOrder) {
        try {
            if (requestedTicketOrder == null) {
                throw new IllegalArgumentException("Requested ticket order cannot be null");
            }

            if (this.selectedPayment == null) {
                Logger.error("No payment method configured, using default UPI");
                this.selectedPayment = new UPIPaymentMethod();
            }

            this.orderId = requestedTicketOrder.orderId();
            Logger.info("Processing payment for order: {}", this.orderId);

            List<ProperTicket> properTickets = new ArrayList<>();
            AtomicInteger totalFare = new AtomicInteger(fare*requestedTicket.quantity());

            for (RequestedTicket requestedTicket : requestedTicketOrder.requestedTicket()) {
                long issuedAt = Instant.now().toEpochMilli();
                long validUntil = 0;//issuedAt + (24 * 60 * 60 * 1000); // Valid for 24 hours

                int fareS2D = this.getFare(requestedTicket);
                List<ProperTicket> subProperTickets = this.getProperTickets(requestedTicket, issuedAt, validUntil, fareS2D);

                subProperTickets.forEach(properTicket -> {
                    Logger.info("Sub Proper ticket: {} - Price: {}",
                            properTicket.getTicketType(), properTicket.getPrice());
//                    totalFare.addAndGet(properTicket.getPrice());
                    Logger.info("Updated fare: {}", totalFare.get());
                });

                properTickets.addAll(subProperTickets);
            }

            Logger.info("Total fare to be paid: {}", totalFare.get());

            if (totalFare.get() <= 0) {
                Logger.error("Total fare mismatched: {}", totalFare.get());
                throw new RuntimeException("Total is 0 or less, cannot proceed with payment");
            }

            new Thread(()-> {
                // Process payment using the configured payment method
                PaymentResponse paymentResponse = (PaymentResponse) PaymentFactory
                        .getPaymentMedia(selectedPayment)
                        .pay(totalFare.get(), requestedTicketOrder.orderId(), new Object[]{this.stackPane,agent.getTransactionRepository()});

                if(!paymentResponse.isSuccess()){
                    buttonsDisability(false);
//                    Logger.info("Payment response: {}", paymentResponse);
//                    Platform.runLater(()-> {
//                        Alert alert = new Alert(Alert.AlertType.ERROR);
//                        alert.setTitle("Payment Failed");
//                        alert.setHeaderText(null);
//                        alert.setContentText("Payment failed. Please try again.");
//                        alert.showAndWait();
//                    });
                    ProperTicketOrder properTicketOrder = new ProperTicketOrder(
                            properTickets.toArray(new ProperTicket[0]),
                            totalFare.get(),
                            requestedTicketOrder.orderId()
                    );

                    // Generate and process tickets
                    this.paymentFailedAndNavigate(properTicketOrder, paymentResponse);
                }else{
                   showWaiting();
                    Logger.info("Payment response: {}", paymentResponse);
                    ProperTicketOrder properTicketOrder = new ProperTicketOrder(
                            properTickets.toArray(new ProperTicket[0]),
                            totalFare.get(),
                            requestedTicketOrder.orderId()
                    );

                    // Generate and process tickets
                    this.processTicketsAndNavigate(properTicketOrder, paymentResponse);
                }
            }).start();

        } catch (Exception e) {
            Logger.error("Error processing payment: {}", e.getMessage());
            e.printStackTrace();
            // TODO: Show error to user
        }
    }

    /**
     * Process tickets and navigate to completion screen
     */
    private void processTicketsAndNavigate(ProperTicketOrder properTicketOrder, PaymentResponse paymentResponse) {
        try {
            QRTicketService qrTicketService = QRTicketFactory.getQRService(new AbstractQRTicketGenerator(), agent);

            TicketInfo ticketInfo = new TicketInfo();
            ticketInfo.setPreGeneratadTicket(new PreGeneratadTicket()
                    .setProperTicketOrder(properTicketOrder)
                    .setPaymentResponse(paymentResponse));
            ticketInfo.setPAmount(paymentResponse.getAmount());

            ArrayList<GeneratedTicket> generatedTickets = qrTicketService.processTicket(
                    this.orderId,
                    paymentResponse.getTransactionId(),
                    ticketInfo
            );

            generatedTickets.forEach(ticket -> {
                Logger.info("Generated ticket: {}", ticket);
            });

            // Navigate to completion screen
            FXMLLoader fxmlLoader = ViewFactory.getSessionCompletionView();

            SessionCompletion sessionCompletion = new SessionCompletion(
                    this.stackPane,
                    this.borderPane,
                    generatedTickets,
                    paymentResponse,
                    agent
            );

            Platform.runLater(()-> {
                        fxmlLoader.setControllerFactory(param -> sessionCompletion);
                        sessionCompletion.printTicket();

                try {
                    this.stackPane.getChildren().add(fxmlLoader.load());
                } catch (IOException e) {
                    e.getMessage();
                }
                this.borderPane.setCenter(this.stackPane);
                    });

            Logger.info("Successfully navigated to completion screen");

        } catch (RuntimeException e) {
            Logger.error("Error processing tickets and navigation: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void paymentFailedAndNavigate(ProperTicketOrder properTicketOrder, PaymentResponse paymentResponse) {
        try {
            QRTicketService qrTicketService = QRTicketFactory.getQRService(new AbstractQRTicketGenerator(), agent);

            TicketInfo ticketInfo = new TicketInfo();
            ticketInfo.setPreGeneratadTicket(new PreGeneratadTicket()
                    .setProperTicketOrder(properTicketOrder)
                    .setPaymentResponse(paymentResponse));
            ticketInfo.setPAmount(paymentResponse.getAmount());

            ArrayList<GeneratedTicket> generatedTickets= new ArrayList<>();

            // Navigate to completion screen
            FXMLLoader fxmlLoader = ViewFactory.getSessionCompletionView();

            SessionCompletion sessionCompletion = new SessionCompletion(
                    this.stackPane,
                    this.borderPane,
                    generatedTickets,
                    paymentResponse,
                    agent
            );

            Platform.runLater(()-> {
                fxmlLoader.setControllerFactory(param -> sessionCompletion);
                sessionCompletion.printTicket();

                try {
                    this.stackPane.getChildren().add(fxmlLoader.load());
                } catch (IOException e) {
                    e.getMessage();
                }
                this.borderPane.setCenter(this.stackPane);
            });

            Logger.info("Successfully navigated to completion screen");

        } catch (RuntimeException e) {
            Logger.error("Error processing tickets and navigation: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleNumberClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String currentText = display.getText();
        String newText = currentText + clickedButton.getText();
        int newTicketCount = Integer.parseInt(newText);
        if (newTicketCount <= maxTicket) {
            display.setText(currentText + clickedButton.getText());
            quantity=Integer.parseInt(display.getText());
        }
    }

    public void handleDeleteClick() {
        int size = display.getText().length();
        if(size>1) {
            display.setText(display.getText().substring(0, size - 1));
            quantity=Integer.parseInt(display.getText());
        }else if(size>0){
            display.setText("");
            quantity=minTicket;
        }
    }

    public void handleDecrement() {
        if(quantity>minTicket){
            quantity--;
            display.setText(String.valueOf(quantity));
        }
    }

    public void handleIncrement() {
        if(quantity<maxTicket){
            quantity++;
            display.setText(String.valueOf(quantity));
        }
    }
}
