package com.amay.tom.service.qrService2;

import com.amay.tom.config.SystemConfig;
import com.amay.tom.enums.PayMethod;
import com.amay.tom.exceptions.PaymentNotDoneException;
import com.amay.tom.exceptions.TicketNotGenerated;
import com.amay.tom.grpc.scugrpc.ScuDataMapper;
import com.amay.tom.grpc.scugrpc.ScuService;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.tickets.PostGeneratedTicket;
import com.amay.tom.model.tickets.ProperTicket;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.model.version.MasterConfigInfo;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tom.threadpool.ThreadPool;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.backend.repository.TransactionRepository;
import org.amaytechnosystems.TicketRequestV1;
import org.tinylog.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public  abstract class QRTicketGenerator implements QRTicketService {

    private final TicketsRepository ticketsRepository;
    private final TransactionRepository transactionRepository;
    private final MasterConfigInfo masterConfigInfo;
    private final ScuService scuService;
    private final Shift shift;
    private final ThreadPool threadPool;
    private final ScuService ccuService; // Not used in the current implementation, can be removed if not needed
    private final String delimiter = ":"; // Delimiter used in QR code data

    protected QRTicketGenerator(TicketsRepository ticketsRepository, TransactionRepository transactionRepository, ScuService scuService, Shift shift, ThreadPool threadPool, ScuService ccuService, MasterConfigInfo masterConfigInfo) {
        this.scuService = scuService;
        this.ticketsRepository = ticketsRepository;
        this.transactionRepository=transactionRepository;
        this.shift=shift;
        this.threadPool=threadPool;
        this.ccuService = ccuService; // Not used in the current implementation, can be removed if not needed'
        this.masterConfigInfo=masterConfigInfo;
    }

    protected abstract ArrayList<PostGeneratedTicket> getTicketIds(ProperTicket[] properTicket, String orderId);

    @Override
    public ArrayList<GeneratedTicket> processTicket(String OrderId, String transactionId, TicketInfo ticketInfo) {
        // not implemented because child is overriding the methods
        return null;
    }



    private void saveInLocalDB(String orderId, String transactionId, PostGeneratedTicket postGeneratedTicket, PaymentResponse paymentResponse) {
    TicketsDto ticketDto = new TicketsDto()
            .setTicketId(postGeneratedTicket.getTicketId())
            .setQrData(postGeneratedTicket.getQrCodeString())
            .setActive(true)
            .setTicketType(postGeneratedTicket.getProperTicket().getTicketType().getTicketTypeId())
            .setInStation(postGeneratedTicket.getProperTicket().getSource().getStationId())
            .setOutStation(postGeneratedTicket.getProperTicket().getDestination().getStationId())
            .setAmount(postGeneratedTicket.getProperTicket().getPrice())
            .setQuantity(postGeneratedTicket.getProperTicket().getQuantity())
            .setIssueAt(postGeneratedTicket.getProperTicket().getIssuedAt())
            .setValidUntil(postGeneratedTicket.getProperTicket().getValidUntil())
            .setOrderId(orderId)
            .setPaymentMode(paymentResponse.getPaymentMode())
            .setDeviceType(shift.getDeviceId().substring(4, 6))
            .setStationId(shift.getDeviceId().substring(2, 4))
            .setLineId(shift.getDeviceId().substring(0, 2))
            .setShiftId(shift.getShiftId())
            .setOperatorId(shift.getOperatorId())
            .setDeviceId(shift.getDeviceId())
            .setTransactionId(transactionId)
            .setCreatedAt(LocalDateTime.now())
            .setUpdatedAt(LocalDateTime.now())
            .setTicketVer(masterConfigInfo.getProductConfig())
            .setFaretableVer(masterConfigInfo.getFareConfig())
            .setSoftwareVer(masterConfigInfo.getTomSwVer());
    ticketsRepository.save(ticketDto);

    }

    private void pushToScu(String orderId, String transactionId, PostGeneratedTicket postGeneratedTicket, PaymentResponse paymentResponse) {
        Logger.debug("Pushing ticket issue info to SCU");
        TicketRequestV1 ticketRequestV1 = ScuDataMapper.createTicketIssueRequest(orderId, transactionId, postGeneratedTicket, this.shift,paymentResponse);
        scuService.pushTicketIssueInfo(ticketRequestV1,ticketsRepository);
        Logger.info("Ticket issue info pushed to SCU for orderId: {}, transactionId: {}, ticketId: {}", orderId, transactionId, postGeneratedTicket.getTicketId());
    }

    private void pushToCcu(String orderId, String transactionId, PostGeneratedTicket postGeneratedTicket,PaymentResponse paymentResponse) {
        Logger.debug("Pushing ticket issue info to CCU");

        TicketRequestV1 ticketRequestV1 = ScuDataMapper.createTicketIssueRequest(orderId, transactionId, postGeneratedTicket, this.shift,paymentResponse);
        ccuService.pushTicketIssueInfo(ticketRequestV1,ticketsRepository);
        Logger.tag(LoggerTag.APP).info("Ticket issue info pushed to CCU for orderId: {}, transactionId: {}, ticketId: {}", orderId, transactionId, postGeneratedTicket.getTicketId());
    }

    // implement it using thread pool, make that class either static or provide thread access to it
    protected void pushToScuAsync(String orderId, String transactionId, PostGeneratedTicket postGeneratedTicket, PaymentResponse paymentResponse) {
        Logger.tag(LoggerTag.APP).debug("Pushing ticket issue info to SCU asynchronously");
        CompletableFuture.runAsync(() -> pushToScu(orderId, transactionId, postGeneratedTicket,paymentResponse),threadPool.getSingleThread());
    }

    protected void pushToCcuAsync(String orderId, String transactionId, PostGeneratedTicket postGeneratedTicket, PaymentResponse paymentResponse) {
        Logger.tag(LoggerTag.APP).debug("Pushing ticket issue info to CCU asynchronously");
        CompletableFuture.runAsync(() -> pushToCcu(orderId, transactionId, postGeneratedTicket,paymentResponse),threadPool.getSingleThread());
    }


    // This  is only for cross  verifying the payment status with order id of ticket and unique transaction id of payment
    protected void verifyPayment(String orderId, String transactionId, int amount) {
        //TODO: verify the payment from db or from the payment gateway
        Logger.info("Verifying the payment for the order id: {} {} {}", orderId,transactionId,amount);
        if(!transactionRepository.verify(orderId,transactionId,amount)) throw new PaymentNotDoneException("Payment is not done for the order id: "+orderId);
    }

    protected ArrayList<GeneratedTicket> pushTicket(String finalOrderId, String finalTransactionId, List<PostGeneratedTicket> postGeneratedTickets, PaymentResponse paymentResponse){

        //TODO: save the ticket in the db, and push the ticket to the SCU, and generate the QR code image
        postGeneratedTickets.forEach(postGeneratedTicket -> {
            Logger.info("Ticket ID: {}", postGeneratedTicket.toString());
            saveInLocalDB(finalOrderId, finalTransactionId,postGeneratedTicket,paymentResponse);
            pushToScuAsync(finalOrderId, finalTransactionId,postGeneratedTicket,paymentResponse);
            pushToCcuAsync(finalOrderId, finalTransactionId,postGeneratedTicket,paymentResponse);
        });

        ArrayList<GeneratedTicket> generatedTickets=new ArrayList<>();
        postGeneratedTickets.forEach(postGeneratedTicket -> {
            generatedTickets.add(postGeneratedTicket);
            Logger.info("Ticket ID: {}", postGeneratedTicket.toString());
        });

        return generatedTickets;
    }



    protected String getQRDataByTicketV1(
            String ticketId,String source, String destination,
            int fare,
            String lineNumber, String stationId, String equipmentSerial,
            String issuanceTime, String validityTime, int ticketTypeId, int ticketQuantity) {

        if(ticketId == null || ticketId.isEmpty()) {
            throw new TicketNotGenerated("Ticket ID is not generated for the ticket.");
        }

        String fareFormatted = String.format("%04d", fare);

        // 10 Parameters are used to generate the QR code
        String qrData = SystemConfig.getInstance().getCurrentEquipment().getEquipmentId() + delimiter + // 8 digit
                ticketId + delimiter + // 6 digit
                issuanceTime + delimiter + // 10 digit
                validityTime + delimiter + // 10 digit
                fareFormatted + delimiter + // 4 digit
                source + delimiter + // 2 digit
                destination + delimiter + // 2 digit
                String.format("%02d", ticketTypeId) + delimiter + // 2 digit
                String.format("%02d", ticketQuantity);
        Logger.info("QR Data: {}", qrData);
        return qrData;
    }



}
