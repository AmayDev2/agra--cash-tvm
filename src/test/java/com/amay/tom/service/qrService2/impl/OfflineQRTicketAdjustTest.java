//package com.amay.tom.service.qrService2.impl;
//
//import com.amay.tom.agent.Agent;
//import com.amay.tom.config.SystemConfig;
//import com.amay.tom.database.SQLiteConnection;
//import com.amay.tom.model.MetroTicket;
//import com.amay.tom.model.TicketType;
//import com.amay.tom.model.payment.PaymentResponse;
//import com.amay.tom.model.tickets.PreGeneratadTicket;
//import com.amay.tom.model.tickets.ProperTicket;
//import com.amay.tom.model.tickets.ProperTicketOrder;
//import com.amay.tom.repository.StationData;
//import com.amay.tom.service.qrService2.*;
//import com.amay.tom.utils.env.EnvFile;
//import org.junit.jupiter.api.Test;
//
//import java.time.Instant;
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class OfflineQRTicketAdjustTest {
//
//    @Test
//    void adjustDestinationOverride() {
//        EnvFile.loadEnv();
//        SQLiteConnection.INSTANCE.setSQLiteConnection();
//        SystemConfig.getInstance();
//        Agent agent = new Agent();
//        QRTicketService qrTicketService = QRTicketFactory.getQRService(new AbstractQRTicketAdjustment(), agent);
//        assertNotNull(qrTicketService);
//        MetroTicket metroTicket=new MetroTicket("3", StationData.getInstance().getStation("st10"), Instant.now().getEpochSecond(),(int)Instant.now().getEpochSecond(),StationData.getInstance().getStation("st11"), StationData.getInstance().getStation("st14"),"1234",12, TicketType.SINGLE,1);
//
//        qrTicketService.processTicket("123","345",new TicketInfo(metroTicket));
//    }
//
//    @Test
//    void generateDestinationOverride() {
//        EnvFile.loadEnv();
//        Agent agent = new Agent();
//
////        QRTicketService qrTicketService= new OfflineQRTicketGenerator();
//        QRTicketService qrTicketService = QRTicketFactory.getQRService(new AbstractQRTicketGenerator(), agent);
//        assertNotNull(qrTicketService);
//        PreGeneratadTicket preGeneratadTicket=new PreGeneratadTicket();
//        preGeneratadTicket.setPaymentResponse(new PaymentResponse("123","345", LocalDateTime.now(),12,"SUCCESS"));
//        preGeneratadTicket.setProperTicketOrder(new ProperTicketOrder(new ProperTicket[]{new ProperTicket(StationData.getInstance().getStation("st12"), StationData.getInstance().getStation("st12"), TicketType.SINGLE, 1, 12, Instant.now().getEpochSecond(),Instant.now().getEpochSecond())},12,"123"));
//        TicketInfo ticketInfo=new TicketInfo(null);
//        ticketInfo.setPreGeneratadTicket(preGeneratadTicket);
//        qrTicketService.processTicket("123","345",ticketInfo);
//    }
//}