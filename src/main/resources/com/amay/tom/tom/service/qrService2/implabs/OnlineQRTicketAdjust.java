package com.amay.tom.service.qrService2.implabs;


import com.amay.tom.agent.Agent;
import com.amay.tom.service.qrService2.AbstractQRTicketAdjustment;
import com.amay.tom.service.qrService2.QRTicketService;

public class OnlineQRTicketAdjust extends AbstractQRTicketAdjustment {

    @Override
    public QRTicketService create(Agent agent) {
        return new com.amay.tom.service.qrService2.impl.OnlineQRTicketAdjust(agent);
    }

}
