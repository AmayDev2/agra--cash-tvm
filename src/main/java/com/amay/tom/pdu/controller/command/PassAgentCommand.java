package com.amay.tom.pdu.controller.command;

import com.amay.tom.agent.Agent;
import com.amay.tom.model.station.Station;
import com.amay.tom.pdu.controller.PDUController;
import javafx.application.Platform;

public class PassAgentCommand implements PDUCommand {
    private final Agent agent;

    public PassAgentCommand(Agent agent){
        this.agent=agent;
    }

    @Override
    public void execute(PDUController pduController) {
       pduController.setAgent(this.agent);
    }
}
