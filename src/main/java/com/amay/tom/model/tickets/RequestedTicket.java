package com.amay.tom.model.tickets;

import com.amay.tom.model.station.Station;
import com.amay.tom.model.TicketType;

public record RequestedTicket(Station source, Station destination, TicketType ticketType, int quantity) {
}
