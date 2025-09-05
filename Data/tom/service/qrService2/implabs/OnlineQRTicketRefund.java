package com.amay.tom.service.qrService2.implabs;

import com.amay.tom.agent.Agent;
import com.amay.tom.repository.adjustment.AdjustedTicketRepository;
import com.amay.tom.repository.adjustment.AdjustedTicketRepositoryImpl;
import com.amay.tom.repository.refund.RefundTicketRepository;
import com.amay.tom.repository.refund.RefundTicketRepositoryImpl;
import com.amay.tom.service.qrService2.AbstractQRTicketRefunde;
import com.amay.tom.service.qrService2.QRTicketService;
import com.amay.tom.service.qrService2.impl.RefundQRServiceImpl;
import org.tinylog.Logger;

public class OnlineQRTicketRefund extends AbstractQRTicketRefunde {

    @Override
    public QRTicketService create(Agent agent) {
        RefundTicketRepository adjustedTicketRepository=new RefundTicketRepositoryImpl(agent.getConnection(),agent.getTicketsRepository());
        agent.setRefundTicketRepository(adjustedTicketRepository);
        Logger.debug("Adjust Repo Agent Obj "+agent);
        return new com.amay.tom.service.qrService2.impl.OfflineQRTicketRefund(agent);
    }


}
