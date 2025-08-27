package com.amay.tom.service.qrService2.impl;


import com.amay.tom.agent.Agent;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.adjust.AdjustedTicket;
import com.amay.tom.service.qrService2.QRTicketAdjustment;
import com.amay.tom.service.qrService2.TicketInfo;

import java.util.ArrayList;

//if only scu is connected not ccu
public class PartialOnlineQRTicketAdjust extends QRTicketAdjustment {

    private OfflineQRTicketAdjust offlineQRTicketAdjust;

    public PartialOnlineQRTicketAdjust(Agent agent) {
        super(agent.getAdjustedTicketRepository(), agent.getScuService(),agent.getShift(),agent.getThreadPool(),agent.getCcuService());
    }

    @Override
    public ArrayList<GeneratedTicket> processTicket(String OrderId, String transactionId, TicketInfo ticketInfo) {

        return null;
    }

    @Override
    public AdjustedTicket adjustTicket(String OrderId, String transactionId, TicketInfo ticketInfo) {
        return null;
    }

    @Override
    public AdjustedTicket adjustEntryTimeOverride(String OrderId, String transactionId, TicketInfo ticketInfo) {

        return null;
    }

    @Override
    public AdjustedTicket adjustExitTimeOverride(String OrderId, String transactionId, TicketInfo ticketInfo) {

        return null;
    }

    @Override
    public AdjustedTicket adjustDestinationOverride(String OrderId, String transactionId, TicketInfo ticketInfo) {

        return null;
    }
}
