package com.amay.tom.exceptions;

public class TicketCouldNotRefunded extends RuntimeException{
    public TicketCouldNotRefunded(String message) {
        super(message);
    }

    public TicketCouldNotRefunded(String message, Throwable cause) {
        super(message, cause);
    }

    public TicketCouldNotRefunded(Throwable cause) {
        super(cause);
    }
}
