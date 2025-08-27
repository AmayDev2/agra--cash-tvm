package com.amay.tom.exceptions;

public class TicketAlreadyRefunded extends RuntimeException {
    public TicketAlreadyRefunded(String message) {
        super(message);
    }

    public TicketAlreadyRefunded(String message, Throwable cause) {
        super(message, cause);
    }

    public TicketAlreadyRefunded(Throwable cause) {
        super(cause);
    }
}
