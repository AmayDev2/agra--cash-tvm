package com.amay.tom.repository;

import com.amay.tom.model.TicketType;

public class TicketTypeData {
    private static TicketTypeData instance = null;
    private final TicketType[] ticketTypeArray = { TicketType.SINGLE, TicketType.RETURN,TicketType.GROUP, TicketType.FREE};

    private TicketTypeData() {
    }

    public static TicketTypeData getInstance() {
        if (instance == null) {
            instance = new TicketTypeData();
        }
        return instance;
    }

    public TicketType getTicketType(String ticketTypeId) {
        for (TicketType ticketType : ticketTypeArray) {
            String ticketTypeChar =  ticketType.getTicketTypeId();

            if (ticketTypeId.equals(String.valueOf(ticketTypeChar)) ) {
                return ticketType;
            }
        }
        return null;
    }

    public TicketType[] getTicketTypeArray() {
        return ticketTypeArray;
    }

}
