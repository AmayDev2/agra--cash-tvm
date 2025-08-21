package com.amay.tvm.controller;

import com.amay.tvm.bnr.BNRIntegration;
import com.jxfs.events.JxfsException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class CashInsertProcessingController {
    @FXML private FlowPane flowPaneInsertedNotes;
    @FXML private  Label remainedAmountText;
    @FXML private FlowPane flowPane;
    private int insertedAmountVal=0;
    private List<Integer> listOfNotes;

    public void setInsertedAmount(int insertedAmount) {
        listOfNotes.add(insertedAmount);
        this.insertedAmountVal=insertedAmount;
        int sumOfTotalInserted=listOfNotes.stream().mapToInt(Integer::intValue).sum();
        this.insertedAmount.setText(sumOfTotalInserted+"/-");
        int remainedAmount=this.amountToPay-sumOfTotalInserted;
        if(remainedAmount<0){
            remainedAmountText.setText("Please Collect the change of : ");
        }
        this.remainedAmount.setText(Math.abs(remainedAmount)+"/-");
        setAcceptedNotesStylish(listOfNotes);
    }

    public void setOpsMessage(String message) {
        this.opsMessage.setText(message);
    }

    private void setAcceptedNotesStylish(List<Integer> list) {
        Platform.runLater(() -> {
            flowPaneInsertedNotes.getChildren().clear(); // clear existing

            for (Integer note : list) {
                // Create a VBox for each note to allow more styling (number + optional icon)
                VBox noteCard = new VBox();
                noteCard.setAlignment(Pos.CENTER);
                noteCard.setSpacing(5);
                noteCard.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #00BCD4, #0097A7);" + // cyan gradient
                                "-fx-text-fill: white;" +
                                "-fx-padding: 5 10 5 10;"+
                                "-fx-background-radius: 2;" +
                                "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.3), 4, 0, 0, 2);"
                );

                // Label for the number
                Label number = new Label(String.valueOf(note));
                number.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

                // Optional: icon or small indicator
                // Label icon = new Label("\u2713"); // checkmark
                // icon.setStyle("-fx-text-fill: yellow; -fx-font-size: 14px;");

                noteCard.getChildren().addAll(number); // add icon if needed

                flowPaneInsertedNotes.getChildren().add(noteCard);
            }
        });
    }


    public void setAcceptableNote(List<Integer> list) {
        Platform.runLater(() -> {
            flowPane.getChildren().clear(); // clear existing chips

            for (Integer note : list) {
                Label chip = new Label(String.valueOf(note));
                chip.setStyle(
                        "-fx-background-color: #4CAF50;" +  // green background for acceptable
                                "-fx-text-fill: white;" +
                                "-fx-padding: 5 10 5 10;" +
                                "-fx-background-radius: 4;" +      // rounded corners
                                "-fx-font-weight: bold;"
                );

                flowPane.getChildren().add(chip);
            }

        });
    }



    @FXML
    private Label insertedAmount;
    @FXML
    private Label totalAmount;
    @FXML
    private Label remainedAmount;


    @FXML
    private Label opsMessage;

    private boolean isSuccess;
    private boolean remainingAmount;

    private int amountToPay=40;
    public  CashInsertProcessingController(){

    }

    public  CashInsertProcessingController(int amount){
        super();
        this.amountToPay=amount;
        this.insertedAmountVal=0;
        this.listOfNotes=new ArrayList<>();
    }


    private void pay(){

    }


    @FXML
    private void initialize(){

        this.totalAmount.setText(this.amountToPay+"/-");
        this.remainedAmount.setText(this.amountToPay+"/-");
        this.insertedAmount.setText("00/-");

    }

    public void skipPrintReceipt(ActionEvent actionEvent)  {
        isSuccess=false;
        try {
            BNRIntegration.cancel();
        } catch (JxfsException e) {
            e.printStackTrace();
        }
        actionEvent.consume();
    }

    public void compareAndSet(int totalAcceptedAmount, int change) {
        setInsertedAmount(totalAcceptedAmount-listOfNotes.stream().mapToInt(Integer::intValue).sum());
        if(change>0){
            this.remainedAmount.setText(change+"/-");
        }else{
            this.remainedAmount.setText("00/-");
        }

    }
}
