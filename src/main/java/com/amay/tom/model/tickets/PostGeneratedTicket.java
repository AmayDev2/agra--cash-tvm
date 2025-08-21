package com.amay.tom.model.tickets;

import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.Station;
import com.amay.tom.model.TicketType;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(chain = true)
@ToString
public class PostGeneratedTicket implements GeneratedTicket {
    private final String qrCodeString;
    private final String TicketId;
    private final ProperTicket properTicket;
}
