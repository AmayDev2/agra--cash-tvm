package com.amay.tom.grpc;

import com.amay.tom.model.QRTicket;
import com.amay.tom.service.qrDataGenerator.QRDataGenerator;
import com.amay.tom.service.qrservice.Impl.ImplQRService;
import com.amay.tom.service.qrservice.QRService;

public class CcuTgService {
    private QRDataGenerator qrDataGenerator=null;
    private QRService qrService=null;

    public CcuTgService(QRDataGenerator qrDataGenerator){
        super();
        this.qrDataGenerator=qrDataGenerator;
        qrService=new ImplQRService();
    }


    public QRTicket[] getTicket() {
return null;
    }






}
