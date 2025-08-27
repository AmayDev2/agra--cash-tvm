package com.amay.tom.service.analysis;


import com.amay.tom.exceptions.DecodingBase64Exception;
import com.amay.tom.exceptions.QRCodeListener;
import com.amay.tom.exceptions.TicketNotGenerated;
import com.amay.tom.model.tickets.QRTicketV2;
import com.amay.tom.service.qrDataGenerator.QRDataGenerator;
import com.amay.tom.service.qrReaderServiceTest.QrCodeListener;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.tinylog.Logger;

public class TicketNumberFromQREventListener implements QrCodeListener {

    private QRDataGenerator qrDataGenerator;
    TextField inputTextField;
    Text errorText;

    public TicketNumberFromQREventListener(QRDataGenerator qrDataGenerator, TextField inputTextField, Text errorText) {
        this.qrDataGenerator = qrDataGenerator;
        this.inputTextField=inputTextField;
        this.errorText=errorText;
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
                    errorText.setText("Couldn't detect Ticket Number");
                }
        if(qrTicket!=null){
            QRTicketV2 finalQrTicket = qrTicket;
            Platform.runLater(()->inputTextField.setText(finalQrTicket.getTicketId()));
        }else{
            errorText.setText("Couldn't detect Ticket Number");
        }

    }
}

