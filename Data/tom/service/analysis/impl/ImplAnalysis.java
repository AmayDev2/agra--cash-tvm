package com.amay.tom.service.analysis.impl;

import com.amay.tom.service.analysis.Analysis;
import com.amay.tom.service.analysis.QrCodeEventListener;
import com.amay.tom.service.qrDataGenerator.impl.ImplQRDataGenerator;
import com.amay.tom.service.qrReaderServiceTest.QrReader;
import javafx.scene.layout.AnchorPane;

public class ImplAnalysis implements Analysis {
    private static ImplAnalysis implAnalysis=new ImplAnalysis();
    private ImplAnalysis() {
    }

    public static ImplAnalysis getInstance() {
        return implAnalysis;
    }

    public void analysisCard() {
    }


    public void analysisQR(AnchorPane ancher) {

        QrReader qrReader = new QrReader(ancher);
        qrReader.addQrCodeListener(new QrCodeEventListener(new ImplQRDataGenerator()));
        qrReader.start();

//        Platform.runLater(() -> {
//            QrReader qrReader = new QrReader();
//            qrReader.addQrCodeListener(new QrCodeEventListener(new ImplQRDataGenerator()));
//            qrReader.start();
//        });
    }

//    private void scan(){
//
//
//
//    }

    public void stopReading(){

//        qrReader.stopReading();



    }
}
