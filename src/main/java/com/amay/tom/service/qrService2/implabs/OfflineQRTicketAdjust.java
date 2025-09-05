package com.amay.tom.service.qrService2.implabs;


import com.amay.tom.agent.Agent;
import com.amay.tom.repository.adjustment.AdjustedTicketRepository;
import com.amay.tom.repository.adjustment.AdjustedTicketRepositoryImpl;
import com.amay.tom.service.qrService2.AbstractQRTicketAdjustment;
import com.amay.tom.service.qrService2.QRTicketService;
import org.tinylog.Logger;

public class OfflineQRTicketAdjust extends AbstractQRTicketAdjustment {

    @Override
    public QRTicketService create(Agent agent) {
        AdjustedTicketRepository adjustedTicketRepository=agent.getAdjustedTicketRepository();
        agent.setAdjustedTicketRepository(adjustedTicketRepository);
        Logger.debug("Adjust Repo Agent Obj "+agent);
        return new com.amay.tom.service.qrService2.impl.OfflineQRTicketAdjust(agent);
    }
}
