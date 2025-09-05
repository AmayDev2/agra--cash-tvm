package com.amay.tom.grpc.ccugrpc;

import org.transaction.qr.*;

import java.util.List;

public class CCUTGService {
    private final QrTransactionGrpc.QrTransactionBlockingStub blockingStub;

    public CCUTGService (QrTransactionGrpc.QrTransactionBlockingStub blockingStub) {
        this.blockingStub = blockingStub;
    }

    public List<TicketData> issueTickets(IssueTicket ticketRequest) {
        List<TicketData> ticketData= null;
        try {
            TicketResponse response = blockingStub.issueTicket(ticketRequest);
             ticketData=response.getListList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ticketData;
    }

}
