package com.amay.tom.pdu.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.bnr.BNRIntegration;
import com.amay.tvm.bnr.BNRListener;
import com.amay.tvm.bnr.BNRListenerLoad;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.h2.util.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MoneyManagementBnrLoadController{
    private final Agent agent;
    private final SceneManager sceneManager;
    @FXML private FlowPane flowPaneInsertedNotes;

    private int insertedAmountVal = 0;
    private final List<Integer> listOfNotes;


    @FXML private  Button cancel;
    @FXML private  Button rollback;
    @FXML private  Button commit;

    public MoneyManagementBnrLoadController(Agent agent, SceneManager sceneManager) {
        this.agent = agent;
        this.sceneManager = sceneManager;
        listOfNotes= new ArrayList<>();
    }

    @FXML
    private void initialize(){
//        agent.getThreadPool().getScheduler().scheduleAtFixedRate(() -> {
//            Platform.runLater(() -> setInsertedAmount(new Random().nextInt(100)));
//            if (new Random().nextInt(100)<50) {
//                Platform.runLater(this::setZero); // Update UI safely
//            }
//        }, 0, 3, TimeUnit.SECONDS);
    }


    @FXML private void onBnrLoad(ActionEvent actionEvent) {
        agent.getThreadPool().getFixedThreadPool().submit(new Task() {
            @Override
            public void call() {BNRIntegration.bnrLoad(new BNRListenerLoad(MoneyManagementBnrLoadController.this,agent.getFinanceOperationRepository(),agent.getShiftMaintenance().getShiftId(),agent.getNoteAmountRepository()));}});
        resetButtons(true);
        actionEvent.consume();
    }

    @FXML private void onCommit(ActionEvent actionEvent) {
        BNRIntegration.bnrLoadCommit();
        actionEvent.consume();
    }

    @FXML private void onBackBnr(ActionEvent actionEvent) {
        onCancel(actionEvent);
        sceneManager.back();
        actionEvent.consume();
    }

    @FXML private void onRollback(ActionEvent actionEvent) {
        BNRIntegration.bnrLoadRollback();
        actionEvent.consume();
    }



    public void disableCancelButton() {
        resetButtons(false);
    }

    private void resetButtons(boolean stopAllowed){
        commit.setVisible(!stopAllowed);
        rollback.setVisible(!stopAllowed);
        cancel.setVisible(stopAllowed);
    }

    @FXML private void onCancel(ActionEvent actionEvent) {
        BNRIntegration.cancel();
        actionEvent.consume();
    }


    public void setInsertedAmount(int insertedAmount) {
        listOfNotes.add(insertedAmount);
        this.insertedAmountVal = insertedAmount;
        int sumOfTotalInserted = listOfNotes.stream().mapToInt(Integer::intValue).sum();
        setAcceptedNotesStylish(listOfNotes);
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
                                "-fx-padding: 5 10 5 10;" +
                                "-fx-background-radius: 2;" +
                                "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.3), 4, 0, 0, 2);"
                );

                // Label for the number
                Label number = new Label(String.valueOf(note));
                number.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

                noteCard.getChildren().addAll(number); // add icon if needed

                flowPaneInsertedNotes.getChildren().add(noteCard);
            }
        });
    }

    public void setZero() {
        listOfNotes.clear();
        flowPaneInsertedNotes.getChildren().clear();
    }
}
