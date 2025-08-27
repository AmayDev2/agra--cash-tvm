package com.amay.tom.service.qrService2;

import com.amay.tom.model.GeneratedTicket;

import java.util.ArrayList;

public interface QRTicketService {
    ArrayList<GeneratedTicket> processTicket(String OrderId, String transactionId, TicketInfo ticketInfo);
}
