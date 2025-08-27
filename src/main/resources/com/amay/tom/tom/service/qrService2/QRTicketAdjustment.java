package com.amay.tom.service.qrService2;

import com.amay.tom.grpc.scugrpc.ScuDataMapper;
import com.amay.tom.grpc.scugrpc.ScuService;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.adjust.AdjustedTicket;
import com.amay.tom.model.adjust.AdjustedTicketMapper;
import com.amay.tom.model.session.Shift;
import com.amay.tom.repository.adjustment.AdjustedTicketRepository;
import com.amay.tom.threadpool.ThreadPool;
import org.amaytechnosystems.TicketAdjustedRequestV1;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public abstract class QRTicketAdjustment implements QRTicketService{
    private final AdjustedTicketRepository adjustedTicketRepository;
    private final ScuService scuService, ccuService; // Assuming ccuService is used in the future, otherwise can be removed
    private final Shift shift;
    private final ThreadPool threadPool;
    protected QRTicketAdjustment(AdjustedTicketRepository adjustedTicketRepository, ScuService scuService, Shift shift, ThreadPool threadPool, ScuService ccuService) {
        this.adjustedTicketRepository = adjustedTicketRepository;
        this.scuService=scuService;
        this.ccuService = ccuService; // Assuming ccuService is used in the future, otherwise can be removed
        this.shift=shift;
        this.threadPool=threadPool;
    }


    @Override
    public ArrayList<GeneratedTicket> processTicket(String OrderId, String transactionId, TicketInfo ticketInfo) {

        return null;
    }

    protected String saveIntoDb(AdjustedTicket adjustedTicket){
        adjustedTicket.setShiftId(this.shift.getShiftId());
        adjustedTicket.setOperatorId(this.shift.getOperatorId());
        adjustedTicket.setPaymentMode("CASH"); // TODO :assignation logic to be changed when more payment modes are introduced
        return adjustedTicketRepository.save(AdjustedTicketMapper.toDto(adjustedTicket));
    }

    protected void pushToScu(String orderId, String transactionId, AdjustedTicket adjustedTicket) {
        Logger.debug("Pushing ticket issue info to SCU");
        TicketAdjustedRequestV1 ticketRequestV1 = ScuDataMapper.createTicketAdjustRequest(orderId, transactionId,adjustedTicket,this.shift);
        ccuService.pushTicketAdjustedInfo(ticketRequestV1, adjustedTicketRepository);
        scuService.pushTicketAdjustedInfo(ticketRequestV1, adjustedTicketRepository);
    }

    // implement it using thread pool, make that class either static or provide thread access to it
    protected void pushToScuAsync(String orderId, String transactionId, AdjustedTicket adjustedTicket) {
        Logger.debug("Pushing ticket issue info to SCU and CCU asynchronously");
        CompletableFuture.runAsync(() -> pushToScu(orderId, transactionId, adjustedTicket),threadPool.getSingleThread());
    }

    public abstract AdjustedTicket adjustTicket(String OrderId, String transactionId, TicketInfo ticketInfo);
    public abstract AdjustedTicket adjustEntryTimeOverride(String OrderId, String transactionId, TicketInfo ticketInfo);
    public abstract AdjustedTicket adjustExitTimeOverride(String OrderId, String transactionId, TicketInfo ticketInfo);
    public abstract AdjustedTicket adjustDestinationOverride(String OrderId, String transactionId, TicketInfo ticketInfo);


}
