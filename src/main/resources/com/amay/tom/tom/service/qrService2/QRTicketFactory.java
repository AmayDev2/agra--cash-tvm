package com.amay.tom.service.qrService2;

import com.amay.tom.agent.Agent;

public class QRTicketFactory {

    public static QRTicketService getQRService(AbstractQRTicketService abstractQRTicketService, Agent agent) {
        return abstractQRTicketService.create(agent);
    }
}

//public class Main{
//    public static void main(String[] args){
//        QRTicketService qrTicketService = QRTicketFactory.getQRService(new AbstractQRTicketAdjustment());
//        qrTicketService.generateQRCode("123", "path");
//        qrTicketService = QRTicketFactory.getQRService(new AbstractQRTicketGenerator());
//        qrTicketService.generateQRCode("123", "path");
//
//    }
//}

