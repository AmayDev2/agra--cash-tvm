package com.amay.tom.service.qrservice.Impl;

import com.amay.tom.service.qrservice.QRService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;


public class ImplQRService implements QRService {

        @Override
        public BufferedImage createQRCode(File qrCodeFile, String qrCodeData, int size, String fileType)
                throws Exception {

            // Configure the encoding parameters
            Map<EncodeHintType, Object> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hintMap.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H); // L, M, Q, H
            hintMap.put(EncodeHintType.MARGIN, 1); // Internal QR margin

            BitMatrix matrix = new MultiFormatWriter().encode(qrCodeData, BarcodeFormat.QR_CODE, size, size, hintMap);

            BufferedImage qrCodeImage = new BufferedImage(matrix.getWidth(), matrix.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = qrCodeImage.createGraphics();

            // Fill background white
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, size, size);

            // Draw rounded black border
            int borderThickness = 5; // pixels
            int cornerRadius = 20;   // pixels
            graphics.setStroke(new BasicStroke(borderThickness));
            graphics.setColor(Color.BLACK);
            graphics.drawRoundRect(borderThickness / 2, borderThickness / 2,
                    size - borderThickness, size - borderThickness,
                    cornerRadius, cornerRadius);

            // Draw QR modules
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (matrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

            graphics.dispose();

            // Optional: Save the QR image
            // ImageIO.write(qrCodeImage, fileType, qrCodeFile);

            return qrCodeImage;
        }




//    public Image generateQRCodeImage(String text) throws WriterException, IOException {
//        // Create a QR Code Writer
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//
//        // Configure the encoding parameters
//        Map<EncodeHintType, Object> hintMap = new HashMap<>();
//        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
//
//        // Generate QR Code matrix from text
//        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 500, 500, hintMap);
//
//        // Convert BitMatrix to BufferedImage
//        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
//
//        // Convert BufferedImage to ByteArrayOutputStream
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
//
//        // Create an Image from the byte array
//        return new Image(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
//    }



    @Override
    public void readQRCode() throws Exception {

    }


}
