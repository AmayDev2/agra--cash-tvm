package com.amay.tom.pdu.controller.command;

import com.amay.tom.ViewFactory;
import com.amay.tom.pdu.controller.PDUController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

public class WelcomePageCommand implements PDUCommand{

    @Override
    public void execute(PDUController pduController) {
        FXMLLoader fxmlLoader=ViewFactory.getPDUWelcomePage();
        Platform.runLater(() -> pduController.showView(fxmlLoader));
    }
}
