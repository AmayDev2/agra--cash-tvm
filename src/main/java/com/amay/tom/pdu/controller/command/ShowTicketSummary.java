package com.amay.tom.pdu.controller.command;

import com.amay.tom.ViewFactory;
import com.amay.tom.pdu.controller.PDUController;
import com.amay.tom.pdu.controller.PaymentSummary;
import com.amay.tom.pdu.controller.command.PDUCommand;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

public class ShowTicketSummary implements PDUCommand {

    private GridPane  gridPane;
    public ShowTicketSummary(GridPane gridPane){
        this.gridPane=gridPane;
    }


    @Override
    public void execute(PDUController pduController) {
        FXMLLoader fxmlLoader=ViewFactory.getTicketSummary();
        fxmlLoader.setControllerFactory((x)->new PaymentSummary(this.gridPane));
        Platform.runLater(() -> pduController.showView(fxmlLoader));

    }
}
