package com.amay.tom.service.print.impl;

import com.amay.tom.service.devices.PeripheralMonitor;
import javafx.print.PrinterJob;
import javafx.scene.text.Text;
import org.tinylog.Logger;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterResolution;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;


public class ImplPrintTicket{
    public static void printText(String paymentData) {
        // Define the print flavor and attributes
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_8;

        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        // List all available print services
        //System.out.println("Available print services:");
        for (PrintService service : printServices) {
            //System.out.println(" - " + service.getName());
        }

        if (printServices.length == 0) {
            //System.out.println("No printer found.");
            return;
        }

        // Get the default print service
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
        if (printService == null) {
            //System.out.println("Default printer not found.");
            return;
        }

        // Create a print job
        DocPrintJob job = printService.createPrintJob();
        //System.out.println("Printing to " + printService.getName());

        PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
        MediaPrintableArea printableArea = new MediaPrintableArea(0, 0, 54, 70, MediaPrintableArea.MM);
        PrinterResolution resolution = new PrinterResolution(300, 300, PrinterResolution.DPI);

        attr.add(resolution);
        attr.add(printableArea);

        // Create a Doc object from the text data
        Doc doc = new SimpleDoc(paymentData, flavor, null);

        // Print the text
        try {
            job.print(doc, attr);
            //System.out.println("Text printed successfully.");
        } catch (PrintException e) {
            //System.out.println("Error printing text: " + e.getMessage());
        }
    }


    public static void printHelloWorld(String paymentData)  {
        PrinterJob job = PrinterJob.createPrinterJob();
//        byte[] documentData = Files.readAllBytes(Paths.get("amaylogo.png"));
//        // Define the document flavor
//        DocFlavor flavor = DocFlavor.INPUT_STREAM.PNG;
//            byte[] documentData = "Its' Risab, developing the code to print through thermal printer ".getBytes(StandardCharsets.UTF_8);

        // Create a Doc object representing your document
//        Doc doc = new SimpleDoc(documentData, flavor, null);
        //System.out.println(job.getPrinter().getName());
        if (job != null) {
            try {
//                job.showPageSetupDialog(null);
//                job.showPageSetupDialog(null);
                boolean success = job.printPage(new Text(paymentData));
                job.endJob();
                job.cancelJob();
                if (success) {
                    job.endJob();
                } else {
                    //System.out.println("Failed to print page.");
                }
            } catch (Exception e) {
                //System.out.println("Error printing: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            //System.out.println("Error creating printer job.");
        }
    }


//    @Override
//    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
//    }


    public static void printImageMaintenanceDep(BufferedImage resizedImage) throws RuntimeException {


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(resizedImage, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        byte[] imageBytes = baos.toByteArray();

        // Define the print flavor and attributes
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.PNG;
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, null);
        if (printServices.length == 0) {
            //System.out.println("No printer found.");
            return;
        }

        // Get the default print service
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
        if (printService == null) {
            //System.out.println("Default printer not found.");
            return;
        }

        // Create a print job
        DocPrintJob job = printService.createPrintJob();

//        //System.out.println("Printing to " + printService.getName());
        //System.out.println("Resolution: " + resizedImage.getWidth() + "x" + resizedImage.getHeight() + " dpi");

        PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
        MediaPrintableArea printableArea = new MediaPrintableArea(0, 0, 74, resizedImage.getHeight(), MediaPrintableArea.MM);

        // Increase the DPI for better resolution
        PrinterResolution resolution = new PrinterResolution(30, 30, PrinterResolution.DPCM);

        attr.add(resolution);
        attr.add(printableArea);


        // Create a Doc object from the image bytes
        Doc doc = new SimpleDoc(imageBytes, flavor, null);

        // Print the image
        try {
            job.print(doc, attr);
            //System.out.println("Image printed successfully.");
        } catch (Exception e){
            Logger.error("Error printing image: {}",e.getMessage());
        }
    }

    private static Optional<PrintService> findPrintService(String name, DocFlavor flavor) {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(name)) {
                return Optional.of(service);
            }
        }
        return Optional.empty();
    }




//    public static void printImageMaintenance(BufferedImage image,
//                                             String printerName,
//                                             String imageFormat,
//                                             boolean autoScale,
//                                             int dpi,
//                                             float marginMM,
//                                             boolean verbose) {
//        if (image == null) {
//            System.err.println("❌ No image to print.");
//            return;
//        }
//
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            // 1. Convert image to byte array
//            ImageIO.write(image, imageFormat, baos);
//            byte[] imageBytes = baos.toByteArray();
//
//            // 2. Define print flavor
//            DocFlavor flavor = new DocFlavor.INPUT_STREAM(imageFormat.equalsIgnoreCase("png")
//                    ? "image/png" : "image/jpeg");
//
//            // 3. Find the printer by name (or fallback to default)
//            PrintService printService = findPrintService(printerName, flavor)
//                    .orElse(PrintServiceLookup.lookupDefaultPrintService());
//
//            if (printService == null) {
//                System.err.println("❌ No printer found.");
//                return;
//            }
//
//            // 4. Check if format is supported
//            if (!printService.isDocFlavorSupported(flavor)) {
//                System.err.printf("❌ Printer '%s' does not support %s format.%n", printService.getName(), imageFormat);
//                return;
//            }
//
//            // 5. Build print attributes
//            PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
//            attr.add(new PrinterResolution(dpi, dpi, PrinterResolution.DPI));
//            attr.add(OrientationRequested.LANDSCAPE);
//
//            attr.add(PrintQuality.HIGH);
//
//            // Calculate scaling if needed
//            if (autoScale) {
//                int widthMM = (int) ((image.getWidth() / (dpi / 25.4)));
//                int heightMM = (int) ((image.getHeight() / (dpi / 25.4)));
//                attr.add(new MediaPrintableArea(marginMM, marginMM,
//                        widthMM - 2 * marginMM, heightMM - 2 * marginMM,
//                        MediaPrintableArea.MM));
//            } else {
//                attr.add(new MediaPrintableArea(marginMM, marginMM, 74, image.getHeight(), MediaPrintableArea.MM));
//            }
//
//            // 6. Print job
//            DocPrintJob job = printService.createPrintJob();
//            Doc doc = new SimpleDoc(new java.io.ByteArrayInputStream(imageBytes), flavor, null);
//
//            if (verbose) {
//                //System.out.printf("🖨️ Printing to: %s (%dx%d px, %s format)%n",
//                        printService.getName(), image.getWidth(), image.getHeight(), imageFormat.toUpperCase());
//            }
//
//            job.print(doc, attr);
//            //System.out.println("✅ Image printed successfully.");
//
//        } catch (PrintException e) {
//            System.err.printf("🛑 Print failed: %s%n", e.getMessage());
//        } catch (IOException e) {
//            System.err.printf("🛑 Image conversion failed: %s%n", e.getMessage());
//        }
//    }


    @Deprecated
    public static void printImage_DEP(BufferedImage resizedImage) throws RuntimeException {
        if (!PeripheralMonitor.getPrinterStatus()) {
            return;
        }

        try {
            // Convert image to ESC/POS byte array
            byte[] escposData = convertImageToEscPos(resizedImage);

            // Use RAW print flavor so the printer interprets commands directly
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, null);

            if (printServices.length == 0) {
                throw new RuntimeException("No printer found.");
            }

            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
            if (printService == null) {
                throw new RuntimeException("Default printer not found.");
            }

            DocPrintJob job = printService.createPrintJob();
            Doc doc = new SimpleDoc(escposData, flavor, null);

            job.print(doc, null);
            //System.out.println("Image printed successfully without margins.");
        } catch (Exception e) {
            Logger.error("Error printing image: {}", e.getMessage());
            throw new RuntimeException("Error printing image: " + e.getMessage(), e);
        }
    }

    private static byte[] convertImageToEscPos(BufferedImage img) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ESC @ - Initialize printer
        baos.write(new byte[]{0x1B, '@'});

        int width = img.getWidth();
        int height = img.getHeight();

        // Convert each 24-pixel-high band
        for (int y = 0; y < height; y += 24) {
            baos.write(new byte[]{0x1B, '*', 33, (byte) (width & 0xFF), (byte) ((width >> 8) & 0xFF)});
            for (int x = 0; x < width; x++) {
                for (int k = 0; k < 3; k++) {
                    byte slice = 0;
                    for (int b = 0; b < 8; b++) {
                        int yy = y + (k * 8) + b;
                        int pixelColor = 0xFFFFFF; // white by default
                        if (yy < height) {
                            pixelColor = img.getRGB(x, yy);
                        }
                        int r = (pixelColor >> 16) & 0xFF;
                        int g = (pixelColor >> 8) & 0xFF;
                        int bl = pixelColor & 0xFF;
                        int luminance = (r * 30 + g * 59 + bl * 11) / 100;
                        if (luminance < 128) {
                            slice |= (1 << (7 - b));
                        }
                    }
                    baos.write(slice);
                }
            }
            baos.write(0x0A); // line feed
        }

        // Cut paper (optional)
        baos.write(new byte[]{0x1D, 'V', 66, 0});

        return baos.toByteArray();
    }





    public static void printImage(BufferedImage resizedImage) throws RuntimeException {
        if(!PeripheralMonitor.getPrinterStatus()){
            return;
        }

//        if(FareMedium.QR.getFareMediumTotal()-FareMedium.QR.getFareMediumSale()<=0){
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText("No QR Ticket Available");
//            alert.setContentText("Please contact the admin for more tickets");
//            alert.showAndWait();
//            Logger.error("No QR Ticket Available");
//            throw new RuntimeException("No QR Ticket Stock Available");
//        }

//        BufferedImage resizedImage = resizeImage(image1, 100, 200);
        // Convert the BufferedImage to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(resizedImage, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        byte[] imageBytes = baos.toByteArray();

        // Define the print flavor and attributes
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.PNG;
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, null);
        if (printServices.length == 0) {
            //System.out.println("No printer found.");
            return;
        }

        // Get the default print service
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
        if (printService == null) {
            //System.out.println("Default printer not found.");
            throw new RuntimeException("Default printer not found.");
        }

        // Create a print job
        DocPrintJob job = printService.createPrintJob();

//        //System.out.println("Printing to " + printService.getName());
        //System.out.println("Resolution: " + resizedImage.getWidth() + "x" + resizedImage.getHeight() + " dpi");



        PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
        MediaPrintableArea printableArea = new MediaPrintableArea(0, 0, 74, resizedImage.getHeight(), MediaPrintableArea.MM);

        // Increase the DPI for better resolution
        PrinterResolution resolution = new PrinterResolution(30, 30, PrinterResolution.DPCM);

        attr.add(resolution);
        attr.add(printableArea);

        boolean isSupported = printService.isDocFlavorSupported(DocFlavor.BYTE_ARRAY.AUTOSENSE);

        // Create a Doc object from the image bytes
        Doc doc = new SimpleDoc(imageBytes, flavor, null);

        // Print the image
        try {
            job.print(doc, attr);
            //System.out.println("Image printed successfully.");
//            FareMedium.QR.incrementQRSaleByOne();
        } catch (Exception e){
            Logger.error("Error printing image: {}",e.getMessage());
            throw new RuntimeException("Error printing image: "+e.getMessage(),e);
        }
    }


    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }
}
