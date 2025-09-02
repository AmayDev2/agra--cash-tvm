package com.amay.tom.service.payment.impl;//package com.amay.tom.service.payment.impl;
//
//import com.amay.tom.grpc.CcuTgService;
//import com.amay.tom.grpc.GrpcConfig;
////import com.amay.tom.grpc.TgGrpcService;
//import com.amay.tom.model.MetroTicket;
//import com.amay.tom.model.QRTicket;
//import com.amay.tom.grpc.scugrpc.ScuDataMapper;
//import com.amay.tom.grpc.scugrpc.ScuService;
//import com.amay.tom.service.chield.TicketService;
//import com.amay.tom.service.payment.IPayment;
//import com.amay.tom.service.payment.PaymentControllerListener;
//import com.amay.tom.service.qrDataGenerator.impl.ImplQRDataGenerator;
//import org.amaytechnosystems.TicketRequestV1;
//import org.tinylog.Logger;
//
//import java.awt.image.BufferedImage;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//@Deprecated
//public class QRPayment implements IPayment {
//
//
//    private TicketService ticketService;
//    private final PaymentControllerListener paymentController;
////    private final TgGrpcService tgGrpcService;
//    private final CcuTgService ccuTgService;
//    private final ScuService scuService;
//
//    public QRPayment(PaymentControllerListener paymentController, TicketService ticketService, ScuService scuService) {
//        super();
//        this.ticketService = ticketService;
//        this.paymentController = paymentController;
//        this.ccuTgService=new CcuTgService(new ImplQRDataGenerator());
//        this.scuService = scuService;
////        this.tgGrpcService = new TgGrpcService(new ImplQRDataGenerator());
//    }
//
//
//    @Override
//    public BufferedImage pay(double amount, MetroTicket[] metroTickets, String orderId) {
//        paymentController.waitForPayment();
//
//        List<QRTicket> qrTickets = new ArrayList<>();
//        if(this.makePayment( amount)){
//            Logger.info("Payment Successfully");
//            for(MetroTicket metroTicket:metroTickets){
////                qrTickets.addAll(Arrays.asList(tgGrpcService.getTicket(GrpcConfig.getBlockingStub(), tgGrpcService.getTicketRequestData(metroTicket))));
//                QRTicket[] qrTickets1=ccuTgService.getTicket(GrpcConfig.getBlockingStub(), ccuTgService.getTicketRequestData(metroTicket,orderId));
//                qrTickets.addAll(Arrays.asList(qrTickets1));
//            }
//            this.printTicket(qrTickets.toArray(new QRTicket[0]));
//            try {
//                //TODO:scucall
//                TicketRequestV1[] ticketRequestV1s = ScuDataMapper.createRequestList(qrTickets, orderId);
//                Arrays.stream(ticketRequestV1s).forEach(this.scuService::pushTicketIssueInfo);
//            }catch (Exception e){
//                Logger.error("Error in pushing ticket issue info to SCU {}",e.getMessage());
//            }
//
//            return new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
//        }else{
//            Logger.error("Payment Failed");
//        }
//        return null;
//
//    }
//
//    private boolean makePayment(double amount) {
//        // TODO Auto-generated method stub
//        try{
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return true;
//
//    }
//
//    private void printTicket(QRTicket[] qrTickets) {
//        Logger.debug("Total Ticket {}",qrTickets.length);
//        for(QRTicket qrTicket:qrTickets){
//            qrTicket.setFareMode("QR");                                                            //TODO: Need to change the fare mode
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
