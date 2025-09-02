package com.amay.tom.service.qrService2;

import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.tickets.PostGeneratedTicket;
import com.amay.tom.model.version.MasterConfigInfo;
import com.amay.tom.service.qrService2.TicketInfo;

import java.util.ArrayList;

public interface QRTicketService {
    ArrayList<GeneratedTicket> processTicket(String OrderId, String transactionId, TicketInfo ticketInfo);
}
