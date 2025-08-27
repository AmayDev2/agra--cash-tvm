package com.amay.tom.pdu.controller.command;

import com.amay.tom.model.station.Station;
import com.amay.tom.pdu.controller.PDUController;
import javafx.application.Platform;

public class HeaderCommand implements PDUCommand{
    private final Station station;

    public HeaderCommand(Station station){
        this.station=station;
    }

    @Override
    public void execute(PDUController pduController) {
        Platform.runLater(() -> pduController.setStation(this.station));
    }
}
