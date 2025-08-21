//package com.amay.tom.service.payment.impl;
//
//import com.amay.tom.controller.PaymentController;
//import com.amay.tom.model.MetroTicket;
//import com.amay.tom.model.QRTicket;
//import com.amay.tom.grpc.scugrpc.ScuDataMapper;
//import com.amay.tom.grpc.scugrpc.ScuService;
//import com.amay.tom.service.chield.TicketService;
//import com.amay.tom.service.payment.IPayment;
//import com.amay.tom.service.payment.PaymentControllerListener;
//import org.tinylog.Logger;
//
//import java.awt.image.BufferedImage;
//import java.util.Arrays;
//import java.util.List;
//
//@Deprecated
//public class CashPay implements IPayment {
//
//
//    private TicketService ticketService;
//    private final PaymentControllerListener paymentController;
//    private ScuService scuService=null;
//
//    public CashPay(PaymentController paymentController,TicketService ticketService,ScuService scuService) {
//        super();
//        this.ticketService = ticketService;
//        this.paymentController = paymentController;
//        this.scuService = scuService;
//    }
//
//    @Override
//    public BufferedImage pay(double amount, MetroTicket[] metroTickets, String orderId) {
//
//        paymentController.waitForPayment();
//
//        QRTicket[] qrTickets=ticketService.generateQRTicket(metroTickets,orderId);
//
//        try {
//            //TODO:scucall
////            ScuService service = new ScuService();
//            org.amaytechnosystems.TicketRequestV1[] ticketRequestV1s = ScuDataMapper.createRequestList(List.of(qrTickets),orderId);
//            Arrays.stream(ticketRequestV1s).forEach(x->scuService.pushTicketIssueInfo(x));
//        }catch (Exception e){
//            Logger.error("Error in pushing ticket issue info to SCU {}",e.getMessage());
//        }
//
//        this.printTicket(qrTickets);
//
//        return new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
//    }
//
//    private void printTicket(QRTicket[] qrTickets) {
//        Logger.debug("Total Ticket {}",qrTickets.length);
//        for(QRTicket qrTicket:qrTickets){
//            qrTicket.setFareMode("CASH");                                                            //TODO: Need to change the fare mode
//            Logger.debug("Printing ticket  {}",qrTicket.getTicketNo());
//            ticketService.printAndSaveTicket(qrTicket);
//            try {
//                Thread.sleep(1000);
//            }catch (Exception e){
//                Logger.error("Error in printing ticket {}",qrTicket.getTicketNo());
//            }
//        }
//
//    }
//}
