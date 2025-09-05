package com.amay.tom.service.cscoperations.impl;

import com.amay.tom.model.QRTicket;
import com.amay.tom.repository.QRDataArray;
import com.amay.tom.service.cscoperations.CSCOperations;
import com.amay.tom.utils.folder.NewFolder;

public class ImplCSCOperations implements CSCOperations {
    @Override
    public QRTicket findQRTicketByQRData(String qrCodeData) {
        return null;
    }

    @Override
    public QRTicket findQRTicketByTicketId(String ticketId) {

        if(NewFolder.findTicket(ticketId)!=null){
            for(QRTicket qrt:QRDataArray.qrDataArray){
                if(qrt.getTicketNo().equals(ticketId)){
                    return qrt;
                }
            }

        }

        return null;
    }

    @Override
    public boolean PrintTicket(QRTicket qrTicket) {
        return false;
    }

    @Override
    public boolean PrintTicketByTicketId(String ticketId) {
        return false;
    }
}
