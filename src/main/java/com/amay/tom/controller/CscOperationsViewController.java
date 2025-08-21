package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.controllerInterface.controllerInt.ControllerAdapter;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tom.service.chield.ReprintTicket;
import com.amay.tom.service.chield.ticketservice.ImplTicketService;
import com.amay.tom.service.cscoperations.CSCOperations;
import com.amay.tom.service.cscoperations.impl.ImplCSCOperations;
import com.amay.tom.service.qrDataGenerator.impl.ImplQRDataGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;

import java.io.IOException;

public class CscOperationsViewController {

    TaggedLogger logger = org.tinylog.Logger.tag("CscOperationsViewController");

    @FXML
    private AnchorPane anchorPane;
    private CSCOperations cscOperations;
    @FXML
    private Image actualImage;

    @FXML
    private Button conformButton;

    @FXML
    private Text currentStatus;

    @FXML
    private ImageView imageView;

    private final ReprintTicket reprintTicket;

    private final Agent agent;
    private TicketsRepository ticketsRepository;

    public CscOperationsViewController(Agent agent) {
        this.agent= agent;
        this.reprintTicket = new ImplTicketService(new ImplQRDataGenerator());
        ticketsRepository= agent.getTicketsRepository();
    }

    @FXML
    private TextField ticketNumberField;

    @FXML
    void onClickConform(ActionEvent event)  {
        String ticketNumber = ticketNumberField.getText();
        Logger.debug("Ticket number: {}", ticketNumber);

        TicketsDto ticketsDto=ticketsRepository.findById(ticketNumber);

        //TODO: map ticket by model

        try{
            FXMLLoader loader = ViewFactory.getReplacementDetailsView();
//            loader.setControllerFactory(x->new ReplacementTicketDetailsController(ticketsDto, borderPane));
            Pane root = loader.load();
            ControllerAdapter.INSTANCE.setChildInCenterAnchorPane(root);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        event.consume();
    }

    @FXML
    void initialize() {
        cscOperations = new ImplCSCOperations();

    }
}
