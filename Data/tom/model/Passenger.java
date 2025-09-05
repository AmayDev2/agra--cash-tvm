package com.amay.tom.model;

import com.amay.tom.enums.Language;
public record Passenger(Station source, Station destination, int quantity, TicketType ticketType, Language language) {
    public Passenger {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity should be greater than 0");
        }
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Source and destination stations cannot be null");
        }
        if(ticketType == null){
            throw new IllegalArgumentException("Ticket type cannot be null");
        }
    }

    public Station getSource() {
        return source;
    }

    public Station getDestination() {
        return destination;
    }

    public int getQuantity() {
        return quantity;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public Language getLanguage() {
        return language;
    }
}

