package com.amay.tom.service.qrService2;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.controller.Controller;
import com.amay.tom.controller.PaymentController;
import com.amay.tom.model.tickets.RequestedTicket;
import com.amay.tom.model.tickets.RequestedTicketOrder;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class QRControllerService {

    private final Agent agent;
    private BorderPane borderPane;
    private Controller controller;

    public QRControllerService(Agent agent, BorderPane borderPane, Controller controller) {
        this.agent = agent;
        this.borderPane=borderPane;
        this.controller=controller;

    }
    public Parent createTicketRequest(ObservableList<RequestedTicket> observableList, BorderPane borderPane) throws IOException {
        this.borderPane= borderPane;
        RequestedTicket[] requestedTicketArray = new RequestedTicket[observableList.size()];

        int ind=0;
        for(RequestedTicket requestedTicket:observableList){
            requestedTicketArray[ind++] = requestedTicket;
        }

        RequestedTicketOrder requestedTicketOrder = new RequestedTicketOrder(requestedTicketArray, agent.getShiftIdGeneratorService().getOrderIdGeneratorService().generateOrderId());
        // caching last order id
        this.agent.getSystemConfig().setLastOrderId(requestedTicketOrder.orderId());

        FXMLLoader fxmlLoader = ViewFactory.getPayment();

        fxmlLoader.setControllerFactory(c -> new PaymentController(agent));
        Parent root = fxmlLoader.load();


        PaymentController paymentController = fxmlLoader.getController();
        paymentController.setParentNode(this.controller);
        paymentController.setParentNode(this.borderPane.getCenter());
        paymentController.setRequestedTicketOrder(requestedTicketOrder);
        observableList.clear();
        return root;
    }
}
