package com.amay.tom.model.tickets;

import com.amay.tom.model.station.Station;
import com.amay.tom.model.TicketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@AllArgsConstructor
@ToString
public class ProperTicket {
    private Station source;
    private Station destination;
    private TicketType ticketType;
    private int quantity;
    private int price;
    private Long issuedAt;
    private Long validUntil;
}
