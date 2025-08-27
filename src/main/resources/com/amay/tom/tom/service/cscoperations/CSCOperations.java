package com.amay.tom.service.cscoperations;

import com.amay.tom.model.QRTicket;

public interface CSCOperations {

    QRTicket findQRTicketByQRData(String qrCodeData);

    QRTicket findQRTicketByTicketId(String ticketId);

    boolean PrintTicket(QRTicket qrTicket);

    boolean PrintTicketByTicketId(String ticketId);
}
