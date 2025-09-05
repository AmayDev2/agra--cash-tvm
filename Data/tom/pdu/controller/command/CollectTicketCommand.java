package com.amay.tom.pdu.controller.command;

import com.amay.tom.ViewFactory;
import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.pdu.controller.PDUController;
import javafx.application.Platform;

public class CollectTicketCommand implements PDUCommand{

    @Override
    public void execute(PDUController pduController) {
        Platform.runLater(() -> pduController.showView(ViewFactory.getCollectTicket()));

    }
}
