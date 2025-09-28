package com.amay.tvm.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.listener.PrintProgressListener;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.service.ticketprint.PrintTicketService;
import com.amay.tvm.backend.enums.LoggerTag;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.h2.util.Task;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class SessionCompletion {
    public Label totalAmount;
    public Label remainedAmount;
    @FXML private Button skipPrintBtn;
    @FXML private Label msgDisp;
    private String messageToShow;

    @FXML private Label eventTitle;
    @FXML private ImageView event;

    private  String eventTitleText;
    private  String image;
    private final ExecutorService executorService;


//    private BorderPane borderPane;
    private final StackPane stackPane;
    private GridPane gridPane;
    private final PrintTicketService printTicketService;



    @FXML
    void initialize(){

        skipPrintBtn.setDisable(true);
        msgDisp.setText("Please wait, printing...");
        gridPane= (GridPane) this.stackPane.getChildren().getFirst();
        if(image!=null){
            event.setImage(new Image(image));
            eventTitle.setText(eventTitleText);
            msgDisp.setText(messageToShow);
        }

        // Scale transition (pulse effect)
        ScaleTransition scale = new ScaleTransition(Duration.seconds(1.2), event);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.3);
        scale.setToY(1.3);
        scale.setAutoReverse(true);
        scale.setCycleCount(20);
        scale.play();


    }

    public void printTicket(){
        executorService.submit(new Task() {
            @Override
            public void call() throws Exception {
                printTicketService.printTicket((x,y)-> Logger.tag(LoggerTag.APP).debug("Please wait, printing..."+x+"/"+y));
                Platform.runLater(() -> {
                    msgDisp.setText(""); // Clear text
                    String msg = messageToShow;
                    Timeline timeline = new Timeline();
                    for (int i = 0; i < msg.length(); i++) {
                        final int index = i;
                        timeline.getKeyFrames().add(
                                new KeyFrame(Duration.millis(40 * i),
                                        e -> msgDisp.setText(msg.substring(0, index + 1))
                                )
                        );
                    }
                    timeline.setOnFinished(e -> {
                        FadeTransition fade = new FadeTransition(Duration.seconds(1), msgDisp);
                        fade.setFromValue(0.5);
                        fade.setToValue(1.0);
                        fade.play();
                    });
                    timeline.play();
                });
                revert();
            }
        });
    }


    public SessionCompletion(StackPane stackPane, BorderPane borderPane,ArrayList<GeneratedTicket> generatedTicket,
                             PaymentResponse paymentResponse, Agent agent){
        this.stackPane=stackPane;
        this.executorService=agent.getThreadPool().getFixedThreadPool();
        printTicketService = new PrintTicketService(generatedTicket, paymentResponse, agent);
        if(paymentResponse.getDenomination()>0){
            messageToShow="Collect pay-receipt,for remaining change!!!";
        }else{
            messageToShow= """
                        Travel light, dream big, arrive safely!
                                Collect your ticket(s)!!!
                    """;
        }
        if(!paymentResponse.isSuccess()){
            eventTitleText="Transaction Canceled";
            image="/images/tvm/failed.png";
            messageToShow="Ticket printing failed. Kindly retry your transaction.";
        }
    }


    private PauseTransition pauseTransition;

    private void revert(){
        skipPrintBtn.setDisable(false);
        pauseTransition= new PauseTransition(javafx.util.Duration.seconds(10));
        pauseTransition.setOnFinished(event -> {
            close();
        });
        pauseTransition.play();

    }

    private void close(){
        this.stackPane.getChildren().clear();
        this.stackPane.getChildren().add(gridPane);
    }



    public void skipPrintReceipt(ActionEvent actionEvent) {
        pauseTransition.jumpTo(pauseTransition.getTotalDuration());
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
