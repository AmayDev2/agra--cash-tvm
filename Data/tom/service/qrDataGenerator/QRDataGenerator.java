package com.amay.tom.service.qrDataGenerator;

import com.amay.tom.model.MetroTicket;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.tickets.QRTicketV2;

public interface QRDataGenerator {

//    public String getQRDataByTicket(MetroTicket metroTicket);

    public String getQRDataByTicketV1(MetroTicket metroTicket);

//    public QRTicket getTicketByQRData(String qrData) throws Exception;

    public QRTicketV2 getTicketV2ByQRData(String qrData) throws Exception;

    public QRTicket getTgTicketByQRData(String qrData) throws Exception;

//    String getQRDataByTicketV1(
//            String ticketId, String issueDay, String issueHM, String source, String destination,
//            int fare, String lineNumber, String stationId, String equipmentSerial,
//            String issuanceTime, String validityTime, int ticketTypeId, int ticketQuantity);
}
