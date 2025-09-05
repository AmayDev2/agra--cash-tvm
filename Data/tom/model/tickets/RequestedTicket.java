package com.amay.tom.model.tickets;

import com.amay.tom.model.Station;
import com.amay.tom.model.TicketType;
import lombok.ToString;

public record RequestedTicket(Station source, Station destination, TicketType ticketType, int quantity) {
}
