package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.controller.Controller;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.pdu.controller.command.CollectTicketCommand;
import com.amay.tom.pdu.controller.command.PDUCommandDispatcher;
import com.amay.tom.service.ticketprint.PrintTicketService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class OperationCompleteController {
    @FXML private  ImageView statusSuccess;
    @FXML private ImageView status;
    @FXML Label statusText;

//    private BufferedImage ticketImage;

    private PrintTicketService printTicketService;
    private final String message;
//    private BorderPane borderPane;
//    private final Agent agent;


    public OperationCompleteController(String message, ArrayList<GeneratedTicket> generatedTicket, PaymentResponse paymentResponse, Agent agent, BorderPane borderPane) {
        this.message=message;
//        this.borderPane = borderPane;
//        this.agent = agent;
        printTicketService = new PrintTicketService(generatedTicket, paymentResponse,agent);

    }

    @FXML
    void initialize() {
        statusText.setText("Processing...");
        com.amay.tom.controller.Controller.getController().disableButtons();
        pauseAndPrint(()->{
            new Thread(() -> {
                printTicketService.printTicket((x,y)->statusText.setText("Please wait, printing..."+x+"/"+y));
                Platform.runLater(()->{
                    status.setImage(statusSuccess.getImage());
                    statusText.setText(message);

               });
                Controller.getController().enableButtons();
                new Thread(()->PDUCommandDispatcher.INSTANCE.dispatch(new CollectTicketCommand())).start();
            } ).start();
        });
//        pauseAndThen(() ->  {
//            Controller.getController().qrTicket(new ActionEvent());
//        });

    }


    public void pauseAndPrint(Runnable action) {
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(evt -> {
            action.run();
        });
        pause.play();
    }


    public void pauseAndThen(Runnable action) {
        PauseTransition pause = new PauseTransition(Duration.seconds(10));
        pause.setOnFinished(evt -> {
            action.run();
        });
        pause.play();
    }

    public void onClickPaidFreeTicket(ActionEvent actionEvent) {
        printTicketService.printPaymentReceipt();
//        if(EnvFile.getPrinterCheck() && PeripheralMonitor.getPrinterStatus() ){
//            Logger.info("Printing Ticket");
//            Platform.runLater(() -> ImplPrintTicket.printImage(ticketImage));
//        }else {
//            Logger.warn("Printer not connected {}", EnvFile.getThermalPrinterModel());
//        }

    }

    public void setData(BufferedImage ticketImage){
//        this.ticketImage=ticketImage;

        }



}
