package com.amay.tom.service.qrService2.implabs;

import com.amay.tom.agent.Agent;
import com.amay.tom.repository.refund.RefundTicketRepository;
import com.amay.tom.repository.refund.RefundTicketRepositoryImpl;
import com.amay.tom.service.qrService2.AbstractQRTicketRefunde;
import com.amay.tom.service.qrService2.QRTicketService;
import org.tinylog.Logger;

public class OfflineQRTicketRefund extends AbstractQRTicketRefunde {

    @Override
    public QRTicketService create(Agent agent) {
        RefundTicketRepository adjustedTicketRepository=new RefundTicketRepositoryImpl(agent.getConnection(),agent.getTicketsRepository());
        agent.setRefundTicketRepository(adjustedTicketRepository);
        Logger.debug("Adjust Repo Agent Obj "+agent);
        return new com.amay.tom.service.qrService2.impl.OfflineQRTicketRefund(agent);
    }


}
