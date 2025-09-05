package com.amay.tom.pdu.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class PaymentSummary {

    @FXML private VBox summaryPage;

    private GridPane gridPane;

    @FXML void initialize(){
        if(null!=gridPane)summaryPage.getChildren().add(gridPane);
    }

    public PaymentSummary(GridPane gridPane){
        this.gridPane=gridPane;
    }

    void setGridPane(GridPane gridPane){
        this.gridPane=gridPane;
    }


}
