package com.amay.tom.pdu.controller.command;
import com.amay.tom.pdu.controller.PDUController;
import com.amay.tom.pdu.controller.command.PDUCommand;
import org.tinylog.Logger;
public enum PDUCommandDispatcher {

    INSTANCE;

    private PDUController controller;

    // Set the controller (must be called after loading the PDU screen)
    public void setController(PDUController controller) {
        this.controller = controller;
    }

    // Dispatch a command to the controller
    public void dispatch(PDUCommand command) {
        if (this.controller != null) {
            command.execute(this.controller);
        } else {
            Logger.warn("PDUController not available. Command skipped.");
        }
    }

    public boolean isAvailable() {
        return this.controller != null;
    }

    public PDUController getController() {
        return this.controller;
    }
}
