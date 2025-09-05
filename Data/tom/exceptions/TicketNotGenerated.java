package com.amay.tom.exceptions;

import org.tinylog.Logger;

public class TicketNotGenerated extends RuntimeException{

    public TicketNotGenerated(String message) {

        super(message);

        Logger.error("Ticket not generated : " + message);
    }

    public TicketNotGenerated(String message, Throwable cause) {
        super(message, cause);
        Logger.error("Ticket not generated : " + message);
    }

    public TicketNotGenerated(Throwable cause) {
        super(cause);
    }

    public TicketNotGenerated() {
        super();
    }
}
