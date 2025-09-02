package com.amay.tom.service.qrService2.implabs;


import com.amay.tom.agent.Agent;
import com.amay.tom.service.qrService2.AbstractQRTicketGenerator;
import com.amay.tom.service.qrService2.QRTicketGenerator;

public class PartialOnlineQRTicketGenerator extends AbstractQRTicketGenerator {



    @Override
    public QRTicketGenerator create(Agent agent) {
        return new com.amay.tom.service.qrService2.impl.PartialOnlineQRTicketGenerator(agent.getTicketsRepository(),agent.getScuService(),agent.getShift(),agent.getThreadPool(),agent.getCcuService(),agent.getShiftIdGeneratorService().getTicketIdGeneratorService(),agent.getMasterConfigInfo());
    }

}
