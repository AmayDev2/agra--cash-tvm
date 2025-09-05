package com.amay.tom.exceptions;

public class TicketNotAddedException extends RuntimeException{

    public TicketNotAddedException(String ticketNotAdded) {
        super(ticketNotAdded);

    }
}
