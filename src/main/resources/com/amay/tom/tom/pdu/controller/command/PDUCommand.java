package com.amay.tom.pdu.controller.command;

import com.amay.tom.pdu.controller.PDUController;

public interface PDUCommand {
    void execute(PDUController pduController);
}
