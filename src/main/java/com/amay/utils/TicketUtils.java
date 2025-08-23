package com.amay.utils;

import com.amay.tom.model.TicketType;
import com.amay.tvm.Env;

public class TicketUtils implements Env {

    public static int getMaxTicket(TicketType ticketType) {
        return switch (ticketType) {
            case SINGLE -> Env.MAX_TICKET_SJT;
            case RETURN -> Env.MAX_TICKET_RJT;
            case GROUP  -> Env.MAX_TICKET_GT;
            default -> -1;
        };
    }

    public static int getMinTicket(TicketType ticketType) {
        return switch (ticketType) {
            case SINGLE -> Env.MIN_TICKET_SJT;
            case RETURN -> Env.MIN_TICKET_RJT;
            case GROUP  -> Env.MIN_TICKET_GT;
            default -> -1;
        };
    }

    public static int getFare(TicketType ticketType) {
        return switch (ticketType) {
            case SINGLE -> Env.FARE_PER_GT;
            case RETURN -> Env.FARE_PER_RJT;
            case GROUP  -> Env.FARE_PER_GT;
            default -> -1;
        };
    }
}
