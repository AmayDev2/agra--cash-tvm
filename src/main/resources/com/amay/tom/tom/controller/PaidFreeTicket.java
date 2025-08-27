package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.controllerInterface.controllerInt.ControllerAdapter;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.equipment.entity.EquipmentPrivilege;
import com.amay.tom.model.tickets.RequestedTicket;
import com.amay.tom.model.tickets.RequestedTicketOrder;
import com.amay.tom.model.user.entity.UserPrivilege;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class PaidFreeTicket {

    @FXML
    private RadioButton paidExit;
    @FXML
    private RadioButton freeExit;
    @FXML
    private VBox paidFreeTicketView;

    @FXML
    private Button freeTicket;

    @FXML
    private Button issueButton;

    @FXML
    private RadioButton tailgating;
    


    @FXML
    private ToggleGroup toggleGroup2;



    UserPrivilege userPrivilege;
    EquipmentPrivilege equipmentPrivilege;
    private Agent agent;
    private BorderPane borderPane;

    public PaidFreeTicket(Agent agent, BorderPane borderPane) {
        this(agent.getUserPrivilege(), agent.getEquipmentPrivilege(),borderPane);
        this.agent = agent;
    }

    public PaidFreeTicket(UserPrivilege userPrivilege, EquipmentPrivilege equipmentPrivilege, BorderPane borderPane) {
        this.userPrivilege = userPrivilege;
        this.equipmentPrivilege = equipmentPrivilege;
        this.borderPane = borderPane;
    }


    @FXML
    void onClickIssue(ActionEvent event) {

        toggleGroup2.getToggles().forEach(toggle -> {
            RadioButton radioButton = (RadioButton) toggle;

            if (radioButton.isSelected() && radioButton.getText().equals(freeExit.getText())) {
                    RequestedTicket[] requestedTicketArray = new RequestedTicket[1];

                    requestedTicketArray[0] = new RequestedTicket(agent.getSystemConfig().getCurrentStation(),agent.getSystemConfig().getCurrentStation(),  TicketType.FREE,1);

                    RequestedTicketOrder requestedTicketOrder = new RequestedTicketOrder(requestedTicketArray, agent.getShiftIdGeneratorService().getOrderIdGeneratorService().generateOrderId());
                    // caching last order id
                    SystemConfig.setLastOrderId(requestedTicketOrder.orderId());

                    FXMLLoader fxmlLoader = ViewFactory.getPayment();
                    PaymentController paymentController = new PaymentController(agent);
                    paymentController.setBorderPane(borderPane);
                    paymentController.setParentNode(borderPane.getCenter());

                    fxmlLoader.setControllerFactory(c -> paymentController);
                    try {
                        ControllerAdapter.INSTANCE.setChildInCenterAnchorPane(fxmlLoader, null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    paymentController.setRequestedTicketOrderFree(requestedTicketOrder);


            } else if (radioButton.isSelected() && radioButton.getText().equals(paidExit.getText())) {
                RequestedTicket[] requestedTicketArray = new RequestedTicket[1];

                requestedTicketArray[0] = new RequestedTicket(agent.getSystemConfig().getCurrentStation(),agent.getSystemConfig().getCurrentStation(),  TicketType.PAID,1);

                RequestedTicketOrder requestedTicketOrder = new RequestedTicketOrder(requestedTicketArray, agent.getShiftIdGeneratorService().getOrderIdGeneratorService().generateOrderId());
                // caching last order id
                SystemConfig.setLastOrderId(requestedTicketOrder.orderId());

                FXMLLoader fxmlLoader = ViewFactory.getPayment();
                PaymentController paymentController = new PaymentController(agent);
                paymentController.setParentNode(borderPane.getCenter());

                fxmlLoader.setControllerFactory(c -> paymentController);
                try {
                    ControllerAdapter.INSTANCE.setChildInCenterAnchorPane(fxmlLoader, null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                paymentController.setRequestedTicketOrderPaid(requestedTicketOrder);

            }
        });
        event.consume();

    }

    public void initialize() {
//        tailgating.setVisible(false);
//        tailgating.setToggleGroup(toggleGroup2);
        paidExit.setToggleGroup(toggleGroup2);
        freeExit.setToggleGroup(toggleGroup2);
//        ticketService = new ImplTicketService();

//        tailgating.setDisable(!userPrivilege.isQrPaidTicket() || !equipmentPrivilege.isQrPaidTicket().get());
        paidExit.setDisable(!userPrivilege.isQrPaidTicket() || !equipmentPrivilege.isQrPaidTicket().get());
        freeExit.setDisable(!userPrivilege.isQrFreeTicket() || !equipmentPrivilege.isQrFreeTicket().get());

        equipmentPrivilege.isQrPaidTicket().addListener((observable, oldValue, newValue) -> {
//            tailgating.setSelected(!userPrivilege.isQrPaidTicket() &&  !newValue);
            paidExit.setSelected(!userPrivilege.isQrPaidTicket() &&  !newValue);
//            tailgating.setDisable(!userPrivilege.isQrPaidTicket() ||  !equipmentPrivilege.isQrPaidTicket().get() || !newValue);
            paidExit.setDisable(!userPrivilege.isQrPaidTicket() ||  !equipmentPrivilege.isQrPaidTicket().get() || !newValue);
        });

        equipmentPrivilege.isQrFreeTicket().addListener((observable, oldValue, newValue) -> {
            freeExit.setSelected(!userPrivilege.isQrFreeTicket() && !newValue);
            freeExit.setDisable(!userPrivilege.isQrFreeTicket() ||  !equipmentPrivilege.isQrFreeTicket().get() || !newValue);
        });
        
        this.activateTickets();
    }

    private void activateTickets() {
        paidExit.setDisable(!TicketType.FREE.getProduct().isActive());
        freeExit.setDisable(!TicketType.PAID.getProduct().isActive());
    }

}
