package com.amay.tom.service.analysis;


import com.amay.tom.controller.AnalysisController;
import com.amay.tom.exceptions.DecodingBase64Exception;
import com.amay.tom.exceptions.QRCodeListener;
import com.amay.tom.exceptions.TicketNotGenerated;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.tickets.QRTicketV2;
import com.amay.tom.service.qrDataGenerator.QRDataGenerator;
import com.amay.tom.service.qrDataGenerator.impl.ImplQRDataGenerator;
import com.amay.tom.service.qrReaderServiceTest.QrCodeListener;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.tinylog.Logger;

public class QrCodeEventListener implements QrCodeListener {

    private QRDataGenerator qrDataGenerator;

    public QrCodeEventListener(QRDataGenerator qrDataGenerator) {
        this.qrDataGenerator = qrDataGenerator;
    }


    @Override
    public void onQrCodeDetected(String qrCodeData) {
        QRTicketV2 qrTicket=null;
                try {
                    if (qrCodeData == null || qrCodeData.isEmpty()) {
                        throw new QRCodeListener("QR Code is empty");
                    }
                    String[] qrCodes = qrCodeData.split("\r");
                    qrTicket = qrDataGenerator.getTicketV2ByQRData(qrCodes[qrCodes.length-1]);

                } catch (Exception ticketNotGenerated) {
                    if (ticketNotGenerated instanceof TicketNotGenerated) {
                        Logger.error("Ticket Not Generated");
                    } else if (ticketNotGenerated instanceof DecodingBase64Exception) {
                        Logger.error("Decoding Base64 Exception");
                    } else {
                        Logger.error("Exception: "+ticketNotGenerated.getClass().getSimpleName());
                    }
                }

        if(AnalysisController.analysisController!=null)AnalysisController.getAnalysisController().qrDetails(qrTicket);
        else Logger.error("Analysis Controller is null");

    }
}

