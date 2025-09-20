package com.amay.printer;

import com.amay.printer.Response.BaseResponse;
import com.amay.printer.Response.ImagePrintResponse;
import com.amay.tom.model.QRTicket;
import com.custom.wndapijwrap.PrinterStatus;

import java.awt.image.BufferedImage;
import java.util.List;

public interface PrinterInterface {
    void getStatus();
    BaseResponse printImage(BufferedImage image);

    BaseResponse printImageByText(ShiftReportData shiftReportData);

    PrinterStatus printerStatus();

    BaseResponse printImageQRImageByText(QRTicket qrTicket);

    BaseResponse printImagesbyText(List<Object> list);

    ImagePrintResponse printImage(String[] imageNames, String path, String extension);
    BaseResponse cutPaper();

    BaseResponse printImageByText(QRTicket qrTicket);



}
