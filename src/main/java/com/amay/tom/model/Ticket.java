package com.amay.tom.model;

import com.amay.tom.model.TicketType;

public class Ticket {
    private String ticketId;
    private int fare;
    private TicketType ticketType;
    private int ticketQuantity;

    public Ticket(String ticketId, int fare, TicketType ticketType, int ticketQuantity) {
        this.ticketId = ticketId;
        this.fare = fare;
        this.ticketType = ticketType;
        this.ticketQuantity = ticketQuantity;
    }

    public String getTicketId() {
        return ticketId;
    }

    public int getFare() {
        return fare;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public int getTicketQuantity() {
        return ticketQuantity;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
}
