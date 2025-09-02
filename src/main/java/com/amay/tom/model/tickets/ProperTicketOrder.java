package com.amay.tom.model.tickets;

import com.amay.tom.model.tickets.ProperTicket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public class ProperTicketOrder {
    private ProperTicket[] properTicket;
    private int totalPrice;
    private String orderId;
}
