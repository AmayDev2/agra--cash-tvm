package com.amay.tvm.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.listener.PrintProgressListener;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.service.ticketprint.PrintTicketService;
import com.amay.tvm.backend.enums.LoggerTag;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.tinylog.Logger;

import java.util.ArrayList;

public class SessionCompletion {
    public Label totalAmount;
    public Label remainedAmount;

    @FXML private Label eventTitle;
    @FXML private ImageView event;

    private  String eventTitleText;
    private  String image;


//    private BorderPane borderPane;
    private StackPane stackPane;
    private GridPane gridPane;
    private PrintTicketService printTicketService;



    @FXML
    void initialize(){

        gridPane= (GridPane) this.stackPane.getChildren().getFirst();
        if(image!=null){
            event.setImage(new Image(image));
            eventTitle.setText(eventTitleText);
        }

    }

    public void printTicket(){
        new Thread(()-> Platform.runLater(()-> this.printTicketService.printTicket((x,y)-> Logger.tag(LoggerTag.APP).debug("Please wait, printing..."+x+"/"+y)))).start();
    }


    public SessionCompletion(StackPane stackPane, BorderPane borderPane,ArrayList<GeneratedTicket> generatedTicket,
                             PaymentResponse paymentResponse, Agent agent){
        this.stackPane=stackPane;
//        this.borderPane = borderPane;
        printTicketService = new PrintTicketService(generatedTicket, paymentResponse, agent);
        if(!paymentResponse.isSuccess()){
            eventTitleText="Transaction Failed";
            image="/images/tvm/failed.png";
        }
    }



    public void printReceipt(ActionEvent actionEvent) {
        actionEvent.consume();
    }

    public void skipPrintReceipt(ActionEvent actionEvent) {
        this.stackPane.getChildren().clear();
        this.stackPane.getChildren().add(gridPane);
//        this.borderPane.setCenter(this.stackPane);
        actionEvent.consume();
    }
}



/*
*
* package com.amay.tvm.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.listener.PrintProgressListener;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.service.ticketprint.PrintTicketService;
import com.amay.tvm.backend.enums.LoggerTag;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.tinylog.Logger;

import java.util.ArrayList;

public class SessionCompletion {
    public Label totalAmount;
    public Label remainedAmount;

    @FXML private Label eventTitle;
    @FXML private ImageView event;

    private  String eventTitleText;
    private  String image;


    private BorderPane borderPane;
    private StackPane stackPane;
    private GridPane gridPane;
    private PrintTicketService printTicketService;



    @FXML
    void initialize(){

        gridPane= (GridPane) this.stackPane.getChildren().getFirst();
        if(image!=null){
            event.setImage(new Image(image));
            eventTitle.setText(eventTitleText);
        }

    }

    public void printTicket(){
        new Thread(()-> {
            Platform.runLater(()-> this.printTicketService.printTicket((x,y)-> Logger.tag(LoggerTag.APP).debug("Please wait, printing..."+x+"/"+y)));
            revert();
        }).start();
    }


    public SessionCompletion(StackPane stackPane, BorderPane borderPane,ArrayList<GeneratedTicket> generatedTicket,
                             PaymentResponse paymentResponse, Agent agent){
        this.stackPane=stackPane;
        this.borderPane = borderPane;
        printTicketService = new PrintTicketService(generatedTicket, paymentResponse, agent);
        if(!paymentResponse.isSuccess()){
            eventTitleText="Transaction Failed";
            image="file://images/tvm/failed.png";
        }
    }

    PauseTransition pauseTransition;

    private void revert(){
        pauseTransition= new PauseTransition(javafx.util.Duration.seconds(5));
        pauseTransition.setOnFinished(event -> {
            close();
        });
        pauseTransition.play();

    }

    private void close(){
        this.stackPane.getChildren().clear();
        this.stackPane.getChildren().add(gridPane);
        this.borderPane.setCenter(this.stackPane);
    }



    public void printReceipt(ActionEvent actionEvent) {
        //System.out.println("Printing receipt...");
        actionEvent.consume();
    }

    public void skipPrintReceipt(ActionEvent actionEvent) {
        pauseTransition.jumpTo(pauseTransition.getTotalDuration());
        actionEvent.consume();
    }
}
*/
