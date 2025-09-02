package com.amay.tom.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.controller.Controller;
import com.amay.tom.enums.FareMedium;
import com.amay.tom.grpc.scugrpc.ScuDataMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tinylog.Logger;


public class StocksAddViewController {

    @FXML
    private Button imprestMoneyClearButton;
    @FXML
    private Button imprestMoneySaveButton;

    @FXML
    private Text availableImprestMoney;
    @FXML
    private TextField imprestMoneyValue;

    @FXML
    private Text availableNCMC;

//    @FXML
//    private Text  availableQR;

    @FXML
    private GridPane gridPane1;

    @FXML
    private GridPane gridPane2;

    @FXML
    private GridPane gridPane3;

    @FXML
    private GridPane gridPane4;

    @FXML
    private TextField ncmcValue;
//
//    @FXML
//    private TextField qrValue;

    @FXML
    private VBox vbox1;

    @FXML
    private VBox vbox2;

    private final int MAX_STOCK = 1000;     //TODO:Configure the QR update and NCMC update to be less than 1000

    private Agent agent;
    private boolean isInitialization;

    public StocksAddViewController(Agent agent) {
        this.agent = agent;
    }
    public StocksAddViewController(Agent agent,boolean isInitialization) {
        this.agent = agent;
        this.isInitialization=isInitialization;
    }


    @FXML
    void initialize() {
            this.updateStock();
        Platform.runLater(()->{
            this.imprestMoneySaveButton.setVisible(isInitialization);
            this.imprestMoneyClearButton.setVisible(isInitialization);
        });

        imprestMoneyValue.setTextFormatter(new TextFormatter<>(change -> {
            if(change.isAdded()){
                String newAmount = change.getControlNewText();
                return (newAmount.matches("^\\d+$") && newAmount.length()<5 && !newAmount.startsWith("0")) ? change : null;
            } else return change;
        }));

        ncmcValue.setTextFormatter(new TextFormatter<>(change -> {
            if(change.isAdded()){
                String newAmount = change.getControlNewText();
                return (newAmount.matches("^\\d+$") && newAmount.length()<5 && !newAmount.startsWith("0")) ? change : null;
            } else return change;
        }));


    }

    public void updateStock() {
        availableNCMC.setText(String.valueOf(FareMedium.NCMC.getFareMediumTotal()));
//        availableQR.setText(String.valueOf(FareMedium.QR.getFareMediumTotal()));
        availableImprestMoney.setText(String.valueOf(FareMedium.IMPREST_MONEY.getFareMediumTotal()));
        agent.getScuService().pushTotalStock(ScuDataMapper.getStockRequest(agent.getShift().getShiftId(),
                agent.getSystemConfig().getCurrentEquipment().getEquipmentId(),
                FareMedium.NCMC.getFareMediumTotal(),FareMedium.IMPREST_MONEY.getFareMediumTotal()));
        agent.getShift().setImprest_money(availableImprestMoney.getText());
        agent.getShiftRepository().updateImprest(agent.getShift().getShiftId(), availableImprestMoney.getText());
    }



    @FXML
    void onClickNCMCEmpty(ActionEvent event) {

        FareMedium.NCMC.setFareMediumTotal(0);
        System.out.println("NCMC Empty Clicked"+FareMedium.NCMC.getFareMediumTotal());
        this.updateStock();

        Logger.info("NCMC Empty Clicked");
        com.amay.tom.controller.Controller.getController().updateStock();

    }

    @FXML
    void onClickNCMCUpdate(ActionEvent event) {
        try {
            if(Integer.parseInt(ncmcValue.getText())>0 && FareMedium.NCMC.getFareMediumTotal()+Integer.parseInt(ncmcValue.getText())<=MAX_STOCK  ) {
                FareMedium.NCMC.addFareMediumTotal(Integer.parseInt(ncmcValue.getText()));
                System.out.println("NCMC Update Clicked" + FareMedium.NCMC.getFareMediumTotal());
                this.updateStock();
                ncmcValue.setText("");

                Logger.info("NCMC Added: {}", FareMedium.NCMC.getFareMediumTotal());
                com.amay.tom.controller.Controller.getController().updateStock();
            }

        } catch (NumberFormatException e) {
            Logger.warn("Invalid input");
        }


    }

    @FXML
    void onClickQREmpty(ActionEvent event) {
        FareMedium.QR.setFareMediumTotal(0);
        updateStock();
        System.out.println("QR Empty Clicked" + FareMedium.QR.getFareMediumTotal());
        com.amay.tom.controller.Controller.getController().updateStock();

    }

//    @FXML
//    void onClickQRUpdate(ActionEvent event) {
//        try {
//
//            if(Integer.parseInt(qrValue.getText())>0 && FareMedium.QR.getFareMediumTotal()+Integer.parseInt(qrValue.getText())<=MAX_STOCK  ) {
//
//                FareMedium.QR.addFareMediumTotal(Integer.parseInt(qrValue.getText()));
//                this.updateStock();
//                qrValue.setText("");
//                System.out.println("QR Update Clicked" + FareMedium.QR.getFareMediumTotal());
//                Logger.info("QR Added: {}", FareMedium.QR.getFareMediumTotal());
//                Controller.getController().updateStock();
//            }
//        } catch (NumberFormatException e) {
//            Logger.warn("Invalid input");
//        }
//
//
//    }
//
//    @FXML
//    private void validatePositiveInteger() {
//        String text = ncmcValue.getText();
//        if (!text.matches("\\d*")) { // Check if input contains non-digit characters
//            ncmcValue.setText(text.replaceAll("[^\\d]", "")); // Remove non-digit characters
//        }
//
//        String text2 = qrValue.getText();
//        if (!text2.matches("\\d*")) { // Check if input contains non-digit characters
//            qrValue.setText(text.replaceAll("[^\\d]", "")); // Remove non-digit characters
//        }
//
//    }


    @FXML
    private void onClickImprestMoneySaveButton(ActionEvent actionEvent) {

        try {
            if(Integer.parseInt(imprestMoneyValue.getText())>0 && FareMedium.IMPREST_MONEY.getFareMediumTotal()+Integer.parseInt(imprestMoneyValue.getText())<=MAX_STOCK  ) {
                FareMedium.IMPREST_MONEY.addFareMediumTotal(Integer.parseInt(imprestMoneyValue.getText()));
                this.updateStock();
                imprestMoneyValue.setText("");
                System.out.println("Imprest Update Clicked " + FareMedium.IMPREST_MONEY.getFareMediumTotal());
                Logger.info("Imprest Added: {}", FareMedium.IMPREST_MONEY.getFareMediumTotal());
                com.amay.tom.controller.Controller.getController().updateStock();
            }
        } catch (NumberFormatException e) {
            Logger.warn("Invalid input");
        }
        actionEvent.consume();

    }

    @FXML
    private void onClickImprestMoneyClearButton(ActionEvent actionEvent) {

        FareMedium.IMPREST_MONEY.setFareMediumTotal(0);
        System.out.println("NCMC Empty Clicked"+FareMedium.IMPREST_MONEY.getFareMediumTotal());
        this.updateStock();
        Logger.info("IMPREST MONEY Empty Clicked");
        Controller.getController().updateStock();
    }
}
