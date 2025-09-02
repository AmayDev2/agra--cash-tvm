package com.amay.tom.service.qrService2.impl;


import com.amay.tom.grpc.ccugrpc.CCUTGDataMapper;
import com.amay.tom.grpc.ccugrpc.CCUTGService;
import com.amay.tom.grpc.scugrpc.ScuService;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.tickets.PostGeneratedTicket;
import com.amay.tom.model.tickets.PreGeneratadTicket;
import com.amay.tom.model.tickets.ProperTicket;
import com.amay.tom.model.version.MasterConfigInfo;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tom.service.qrService2.QRTicketGenerator;
import com.amay.tom.service.qrService2.TicketInfo;
import com.amay.tom.threadpool.ThreadPool;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class OnlineQRTicketGeneratorDept extends QRTicketGenerator {

    private final CCUTGService ccutgService;
    private final Shift shift;
    public OnlineQRTicketGeneratorDept(TicketsRepository ticketsRepository, ScuService scuService, CCUTGService ccutgService, Shift shift, ThreadPool threadPool, ScuService ccuService, MasterConfigInfo masterConfigInfo) {
        super(ticketsRepository, scuService, shift, threadPool, ccuService,masterConfigInfo);
        this.ccutgService = ccutgService;
        this.shift = shift;
    }

    @Override
    public  ArrayList<GeneratedTicket>  processTicket(String OrderId, String transactionId, TicketInfo ticketInfo) {
        PreGeneratadTicket preGeneratadTicket = ticketInfo.getPreGeneratadTicket();
        transactionId = preGeneratadTicket.getPaymentResponse().getTransactionId();
        OrderId = preGeneratadTicket.getProperTicketOrder().getOrderId();
        this.verifyPayment(OrderId, transactionId, preGeneratadTicket.getPaymentResponse().getAmount());
        ProperTicket[] properTicket = preGeneratadTicket.getProperTicketOrder().getProperTicket();
        ArrayList<PostGeneratedTicket> postGeneratedTickets;
        postGeneratedTickets = this.getTicketIds(properTicket,OrderId);

        return pushTicket(OrderId,transactionId,postGeneratedTickets);
    }

    @Override
    protected ArrayList<PostGeneratedTicket> getTicketIds(ProperTicket[] properTicket, String orderId) {
        return null;
    }


}
