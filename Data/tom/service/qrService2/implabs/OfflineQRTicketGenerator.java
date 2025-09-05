package com.amay.tom.service.qrService2.implabs;


import com.amay.tom.agent.Agent;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tom.repository.tickets.TicketsRepositoryImpl;
import com.amay.tom.service.qrService2.AbstractQRTicketGenerator;
import com.amay.tom.service.qrService2.QRTicketGenerator;

public class OfflineQRTicketGenerator extends AbstractQRTicketGenerator {



    @Override
    public QRTicketGenerator create(Agent agent) {
        return new com.amay.tom.service.qrService2.impl.OfflineQRTicketGenerator(agent.getTicketsRepository(),agent.getScuService(),agent.getShift(),agent.getThreadPool(),agent.getCcuService(),agent.getShiftIdGeneratorService().getTicketIdGeneratorService());
    }

}
