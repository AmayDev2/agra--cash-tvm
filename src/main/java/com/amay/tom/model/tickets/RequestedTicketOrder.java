package com.amay.tom.model.tickets;

import com.amay.tom.model.tickets.RequestedTicket;

public record RequestedTicketOrder(RequestedTicket[] requestedTicket, String orderId) {
}


