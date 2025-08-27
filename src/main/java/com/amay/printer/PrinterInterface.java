package com.amay.printer;

import com.amay.printer.Response.BaseResponse;
import com.amay.printer.Response.ImagePrintResponse;

import java.awt.image.BufferedImage;

public interface PrinterInterface {
    void getStatus();
    BaseResponse printImage(BufferedImage image);
    ImagePrintResponse printImage(String[] imageNames, String path, String extension);
    BaseResponse cutPaper();
}
