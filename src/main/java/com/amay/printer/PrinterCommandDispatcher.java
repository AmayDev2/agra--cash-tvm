package com.amay.printer;

import com.amay.printer.Response.BaseResponse;
import com.amay.tom.model.QRTicket;

import java.awt.image.BufferedImage;
import java.util.List;

public enum PrinterCommandDispatcher {
    INSTANCE;
    private  PrinterInterface printerInterface;

    public void setupPrinter(){
        if(printerInterface==null) {
            printerInterface = new PrinterService();
        }
    }

    public BaseResponse printImage(BufferedImage image){
        return  printerInterface.printImage(image);
    }

    public BaseResponse printText(List<Object> list){
        return  printerInterface.printImagesbyText(list);
    }

    public BaseResponse printText(QRTicket qrTicket){
        return  printerInterface.printImageByText(qrTicket);
    }
    public BaseResponse printText(ShiftReportData shiftReportData){
        return  printerInterface.printImageByText(shiftReportData);
    }

}
