package com.amay.tom.model.tickets;

import com.amay.tom.model.station.Station;
import com.amay.tom.model.TicketType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Data
public class QRTicketV2 {
    private String operatorId;
    private String ticketId;
    private long issueAt;
    private long validUntil;
    private Station inStation;
    private Station outStation;
    private TicketType ticketType;
    private String fareMode;
    private int amount;
    private int quantity;
}
