package com.amay.tom.service.print.impl;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrinterService implements Printable {

    public List<String> getPrinters(){

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printServices[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);

        List<String> printerList = new ArrayList<String>();
        for(PrintService printerService: printServices){
            printerList.add( printerService.getName());
        }

        return printerList;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page)
            throws PrinterException {
        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }

        /*
         * User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        /* Now we perform our rendering */

        g.setFont(new Font("Roman", 0, 8));
        g.drawString("Hello world !", 0, 10);

        return PAGE_EXISTS;
    }

    public void printString(String printerName, String text) {

        // find the printService of name printerName
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printService[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);
        PrintService service = findPrintService(printerName, printService);

        DocPrintJob job = service.createPrintJob();

        try {

            byte[] bytes;

            // important for umlaut chars
            bytes = text.getBytes("CP437");

            Doc doc = new SimpleDoc(bytes, flavor, null);


            job.print(doc, null);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void printBytes(String printerName, byte[] bytes) {

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printService[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);
        PrintService service = findPrintService(printerName, printService);

        DocPrintJob job = service.createPrintJob();

        try {

            Doc doc = new SimpleDoc(bytes, flavor, null);

            job.print(doc, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printImage(String printerName, byte[] bytes) {

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printService[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);
        PrintService service = findPrintService(printerName, printService);

        DocPrintJob job = service.createPrintJob();

        try {

            Doc doc = new SimpleDoc(bytes, flavor, null);

            job.print(doc, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PrintService findPrintService(String printerName,
                                          PrintService[] services) {
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }

        return null;
    }

    public void printImage(String printerName, BufferedImage image, int width, int height) {
        // Resize the image
        BufferedImage resizedImage = resizeImage(image, width, height);

        // Convert the resized BufferedImage to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(resizedImage, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        byte[] imageBytes = baos.toByteArray();

        // Define the print flavor and attributes
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

//        // Set page format and dimensions
//        MediaSizeName mediaSizeName = MediaSize.findMedia(width, height, MediaSize.INCH);
//        pras.add(MediaSizeName.findMedia(width, height, MediaSize.INCH));
//        pras.add(mediaSizeName);
//        pras.add(MediaSize.findMedia(width, height, MediaSize.INCH));
        pras.add(MediaSizeName.ISO_A7); // Example: ISO A4 paper size

        // Lookup available print services
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, pras);

        // Find the specified print service
        PrintService service = findPrintService(printerName, printServices);
        if (service == null) {
            System.out.println("Printer not found: " + printerName);
            return;
        }

        // Create a print job
        DocPrintJob job = service.createPrintJob();

        // Create a Doc object from the image bytes
        Doc doc = new SimpleDoc(imageBytes, flavor, null);

        // Print the image
        try {
            job.print(doc, null);
            System.out.println("Image printed successfully.");
        } catch (PrintException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }


}
