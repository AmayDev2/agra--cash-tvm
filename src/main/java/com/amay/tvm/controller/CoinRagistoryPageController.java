package com.amay.tvm.controller;

import com.amay.tvm.coin.service.HoppersRegistry;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CoinRagistoryPageController {

    @FXML private TextField qty1;
    @FXML private Label total1;
    @FXML private TextField qty2;
    @FXML private Label total2;
    @FXML private TextField qty3;
    @FXML private Label total3;

    @FXML
    void initialization() {
        //TODO: FROM DB
        total1.setText(HoppersRegistry.INSTANCE.getHopperQuantity("1"));
        total2.setText(HoppersRegistry.INSTANCE.getHopperQuantity("2"));
        total3.setText(HoppersRegistry.INSTANCE.getHopperQuantity("3"));

        // Add key listener to root (or text fields)
        qty1.getScene().addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPress);
        qty2.getScene().addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPress);
        qty3.getScene().addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPress);
    }

    @FXML
    public void updateHopperInfo() {
        try {
            //TODO:UPDATE IN DB
            int hop1 = Integer.parseInt(qty1.getText());
            int hop2 = Integer.parseInt(qty2.getText());
            int hop3 = Integer.parseInt(qty3.getText());


            HoppersRegistry.INSTANCE.updateHopperAdd(1, hop1);
            HoppersRegistry.INSTANCE.updateHopperAdd(2, hop2);
            HoppersRegistry.INSTANCE.updateHopperAdd(3, hop3);

            refreshTotals();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input in hopper quantity fields");
        }
    }

    private void refreshTotals() {
        total1.setText(HoppersRegistry.INSTANCE.getHopperQuantity("1"));
        total2.setText(HoppersRegistry.INSTANCE.getHopperQuantity("2"));
        total3.setText(HoppersRegistry.INSTANCE.getHopperQuantity("3"));

        // Optional: clear text fields
        qty1.clear();
        qty2.clear();
        qty3.clear();
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            // Reset hopper counts
            qty1.clear();
            qty2.clear();
            qty3.clear();

            HoppersRegistry.INSTANCE.resetHopper(1);
            HoppersRegistry.INSTANCE.resetHopper(2);
            HoppersRegistry.INSTANCE.resetHopper(3);

            refreshTotals();
        }
    }
}
