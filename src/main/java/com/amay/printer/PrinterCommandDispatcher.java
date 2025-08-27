package com.amay.printer;

import com.amay.printer.Response.BaseResponse;

import java.awt.image.BufferedImage;

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

}
