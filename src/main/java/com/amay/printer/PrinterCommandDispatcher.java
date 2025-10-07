package com.amay.printer;

import com.amay.printer.Response.BaseResponse;
import com.amay.tom.model.QRTicket;
import com.amay.tvm.backend.enums.LoggerTag;
import com.custom.wndapijwrap.PrinterStatus;
import org.tinylog.Logger;

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
        return  printerInterface. printImagesbyText(list);
    }

    public BaseResponse printText(QRTicket qrTicket){
        return  printerInterface.printImageByText(qrTicket);
    }
    public BaseResponse printText(List<QRTicket> qrTickets,PayReceipt payReceipt){
        return  printerInterface.printTicketsWithPayReceipt(qrTickets,payReceipt);
    }

    public BaseResponse testPrint(){
        return printerInterface.testPrint();
    }
    public BaseResponse printText(ShiftReportData shiftReportData){
        return  printerInterface.printImageByText(shiftReportData);
    }
    public BaseResponse printBNRLoadUnload(BNRLoadUnload bnRLoadUnload){
        return  printerInterface.printBNRText(bnRLoadUnload);
    }
    public BaseResponse printCoinLoadedReport(CoinLoadedReport coinLoadedReport){
        return  printerInterface.printCoinLoadedReport(coinLoadedReport);
    }
    public BaseResponse printBalanceReport(BalanceReport balanceReport){
        return  printerInterface.printBalanceReport(balanceReport);
    }

    public PrinterStatus getStatus() {
        return printerInterface.getStatus();
    }

    public boolean isConnected() {
        try {
            return printerInterface.isConnectedIfNotThenConnect();
        } catch (Exception e) {
            Logger.tag(LoggerTag.APP).error(e.getMessage());
        }
        return false;
    }
}
