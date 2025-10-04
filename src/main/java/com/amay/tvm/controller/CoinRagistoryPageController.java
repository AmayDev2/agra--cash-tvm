package com.amay.tvm.controller;

import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.coin.CoinModuleInterface;
import com.amay.tvm.coin.service.HoppersRegistry;
import com.amay.tvm.util.ThreadPool;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.tinylog.Logger;

public class CoinRagistoryPageController {

    @FXML private Button apply;
    @FXML private Button back;
    @FXML private TextField qty1;
    @FXML private Label total1;
    @FXML private TextField qty2;
    @FXML private Label total2;
    @FXML private TextField qty3;
    @FXML private Label total3;

    private final SceneManager sceneManager;


    public CoinRagistoryPageController(SceneManager sceneManager){
        this.sceneManager=sceneManager;
    }

    @FXML
    void initialize(){


        //TODO: FROM DB
        refreshTotals();

        back.addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPress);
        apply.addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPress);

    }

     void updateHopperInfo() {
        try {
            //TODO:UPDATE IN DB
            int hop1 = Integer.parseInt(qty1.getText());
            int hop2 = Integer.parseInt(qty2.getText());
            int hop3 = Integer.parseInt(qty3.getText());


            if(!qty1.getText().isBlank())HoppersRegistry.INSTANCE.updateHopperAdd(1, hop1);
            if(!qty1.getText().isBlank())HoppersRegistry.INSTANCE.updateHopperAdd(2, hop2);
            if(!qty1.getText().isBlank())HoppersRegistry.INSTANCE.updateHopperAdd(3, hop3);

            refreshTotals();
        } catch (NumberFormatException e) {
            Logger.tag(LoggerTag.APP).error("Invalid input in hopper quantity fields");
        }
    }

    private void refreshTotals() {
        try {
            total1.setText(HoppersRegistry.INSTANCE.getHopperQuantity("1"));
            total2.setText(HoppersRegistry.INSTANCE.getHopperQuantity("2"));
            total3.setText(HoppersRegistry.INSTANCE.getHopperQuantity("3"));

            // Optional: clear text fields
            qty1.clear();
            qty2.clear();
            qty3.clear();
        } catch (Exception e) {
            Logger.tag(LoggerTag.APP).error("Error refreshing hopper totals: {}", e.getMessage());
        }
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            back.fire();
        }
    }

    @FXML
    private void dumpHopper(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        int hopperId = Integer.parseInt(button.getId().replace("dumpHopper", ""));

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(500, 500); // optional: size
        spinner.setStyle("-fx-progress-color: blue;"); // optional: color
        sceneManager.addWaiting(spinner);
        ThreadPool threadPool= ThreadPool.getInstance();

        // Show spinner
        spinner.setVisible(true); // your ProgressIndicator in FXML

        // Run in background
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Long-running operation
                CoinModuleInterface.INSTANCE.dumpHopper(hopperId);
                return null;
            }

            @Override
            protected void succeeded() {
                // Back on UI thread
                spinner.setVisible(false);
                refreshTotals(); // updateToAdd UI after completion
                sceneManager.back();
            }

            @Override
            protected void failed() {
                spinner.setVisible(false);
                sceneManager.back();
                // Optionally show error
                Throwable ex = getException();
                ex.printStackTrace();
            }
        };

        // Start background thread
        threadPool.getFixedThreadPool().submit(task);

        actionEvent.consume();
    }


    @FXML private void back(ActionEvent actionEvent) {
        sceneManager.back();
        actionEvent.consume();
    }

    @FXML private void apply(ActionEvent actionEvent) {
        updateHopperInfo();
        actionEvent.consume();
    }
}
