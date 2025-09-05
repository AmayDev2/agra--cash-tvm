package com.amay.tom.service.chield;

import com.amay.tom.model.QRTicket;

public interface ReprintTicket{
    boolean reprintTicketByTicketNo(String ticketNo);
    QRTicket getTicketByTicketNumber(String ticketNo);
}