package com.amay.tom.service.qrService2.impl;


import com.amay.tom.agent.Agent;
import com.amay.tom.model.adjust.AdjustedTicket;
import com.amay.tom.repository.adjustment.AdjustedTicketRepositoryImpl;
import com.amay.tom.service.qrService2.QRTicketAdjustment;
import com.amay.tom.service.qrService2.TicketInfo;

public class OnlineQRTicketAdjust extends QRTicketAdjustment {


    public OnlineQRTicketAdjust(Agent agent) {
        super(agent.getAdjustedTicketRepository(),agent.getScuService(),agent.getShift(),agent.getThreadPool(),agent.getCcuService());

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
