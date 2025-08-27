package com.amay.tom.service.qrService2.impl;


import com.amay.tom.config.SystemConfig;
import com.amay.tom.grpc.scugrpc.ScuService;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.tickets.PostGeneratedTicket;
import com.amay.tom.model.tickets.PreGeneratadTicket;
import com.amay.tom.model.tickets.ProperTicket;
import com.amay.tom.model.version.MasterConfigInfo;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tom.service.base36.TicketIdGeneratorService;
import com.amay.tom.service.qrService2.QRTicketGenerator;
import com.amay.tom.service.qrService2.TicketInfo;
import com.amay.tom.threadpool.ThreadPool;
import com.amay.tom.utils.encription.Base64Encoding;
import org.tinylog.Logger;

import java.util.ArrayList;

//1:- ticketIds generation  --off
//2:- ticketQRs string generation --off
//3:- ticketQRs string encryption --off
//3.5 save into db
    //4:- ticket's QR image generation
    //5:- ticket's QR image saving with id
    //6:- ticket image generation
    //7:- ticket image saving with id
        //8:- receipt image generation
        //9:- ticket printing
        //10:- receipt generation
public class PartialOnlineQRTicketGenerator extends QRTicketGenerator {

    private final TicketsRepository ticketsRepository;
    private final TicketIdGeneratorService ticketIdGeneratorService;

    private static final String delimiter = ":";

    public PartialOnlineQRTicketGenerator(TicketsRepository ticketsRepository, ScuService scuService, Shift shift, ThreadPool threadPool, ScuService ccuService, TicketIdGeneratorService ticketIdGeneratorService, MasterConfigInfo masterConfigInfo) {
        super(ticketsRepository, scuService, shift,threadPool, ccuService, masterConfigInfo);
        this.ticketsRepository = ticketsRepository;
        this.ticketIdGeneratorService = ticketIdGeneratorService;
    }

    @Override
    public ArrayList<GeneratedTicket> processTicket(String OrderId, String transactionId, TicketInfo ticketInfo) {
//        super.processTicket(OrderId, transactionId, ticketInfo);
        PreGeneratadTicket preGeneratadTicket=ticketInfo.getPreGeneratadTicket();

        //TODO: remove this insertion
        transactionId=preGeneratadTicket.getPaymentResponse().getTransactionId();
        OrderId= preGeneratadTicket.getProperTicketOrder().getOrderId();
        //preGeneratadTicket.getPaymentResponse().getAmount() will have total amount
        super.verifyPayment(OrderId,transactionId,preGeneratadTicket.getPaymentResponse().getAmount());

        ProperTicket[] properTicket=preGeneratadTicket.getProperTicketOrder().getProperTicket();
        ArrayList<PostGeneratedTicket> postGeneratedTickets=null;
        postGeneratedTickets= this.getTicketIds(properTicket,OrderId);

        return pushTicket(OrderId,transactionId,postGeneratedTickets);


    }



    @Override
    protected ArrayList<PostGeneratedTicket> getTicketIds(ProperTicket[] properTickets, String orderId) {
        ArrayList<PostGeneratedTicket> postGeneratedTickets=new ArrayList<>();
        for (ProperTicket ticket:properTickets) {
            String ticketId = this.ticketIdGeneratorService.generateTicketId();  // getting ticket id from service
            String qrString = this.getQRDataByTicketV1(ticketId, ticket.getSource().getStationId()
                    , ticket.getDestination().getStationId(), ticket.getPrice(), SystemConfig.getInstance().getLineNumber(), ticket.getSource().getStationId(), SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial()
                    , ticket.getIssuedAt().toString(), ticket.getValidUntil().toString(), Integer.parseInt(ticket.getTicketType().getTicketTypeId()), ticket.getQuantity());


            PostGeneratedTicket postGeneratedTicket = new PostGeneratedTicket(qrString, ticketId, ticket);
            postGeneratedTickets.add(postGeneratedTicket);
        }
        return postGeneratedTickets;
    }



    @Override
    protected String getQRDataByTicketV1(
            String ticketId,String source, String destination,
            int fare, String lineNumber, String stationId, String equipmentSerial,
            String issuanceTime, String validityTime, int ticketTypeId, int ticketQuantity) {
        String qrData=super.getQRDataByTicketV1(ticketId, source, destination, fare, lineNumber, stationId, equipmentSerial, issuanceTime, validityTime, ticketTypeId, ticketQuantity);
        qrData+=delimiter+String.format("%01d", 1);
        Logger.info("QR Data: {}", qrData);
        return Base64Encoding.encode(qrData);
    }





}
