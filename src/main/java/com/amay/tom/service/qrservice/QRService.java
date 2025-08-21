package com.amay.tom.service.qrservice;

import java.awt.image.BufferedImage;
import java.io.File;

public interface QRService {
    BufferedImage createQRCode(File qrCodeFile, String qrCodeData, int size, String fileType) throws Exception;
    void readQRCode() throws Exception;
}
