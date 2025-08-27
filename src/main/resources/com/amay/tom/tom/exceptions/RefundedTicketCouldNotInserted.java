package com.amay.tom.exceptions;

public class RefundedTicketCouldNotInserted extends RuntimeException {

    public RefundedTicketCouldNotInserted(String message) {
        super(message);
    }

    public RefundedTicketCouldNotInserted(String message, Throwable cause) {
        super(message, cause);
    }

    public RefundedTicketCouldNotInserted(Throwable cause) {
        super(cause);
    }
}
