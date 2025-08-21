package com.amay.tom.service.qrService2.implabs;


import com.amay.tom.agent.Agent;
import com.amay.tom.repository.adjustment.AdjustedTicketRepository;
import com.amay.tom.repository.adjustment.AdjustedTicketRepositoryImpl;
import com.amay.tom.service.qrService2.AbstractQRTicketAdjustment;
import com.amay.tom.service.qrService2.QRTicketService;
import org.tinylog.Logger;

//if only scu is connected not ccu
public class PartialOnlineQRTicketAdjust extends AbstractQRTicketAdjustment {

    @Override
    public QRTicketService create(Agent agent) {

        return new com.amay.tom.service.qrService2.impl.PartialOnlineQRTicketAdjust(agent);
    }


}
