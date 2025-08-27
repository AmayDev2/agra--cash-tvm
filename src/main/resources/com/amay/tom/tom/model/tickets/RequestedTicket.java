package com.amay.tom.model.tickets;

import com.amay.tom.model.TicketType;
import com.amay.tom.model.station.Station;

public record RequestedTicket(Station source, Station destination, TicketType ticketType, int quantity) {
}
