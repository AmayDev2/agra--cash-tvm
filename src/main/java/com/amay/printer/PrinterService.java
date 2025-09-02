package com.amay.printer;

import com.amay.printer.Response.BaseResponse;
import com.amay.printer.Response.ImagePrintResponse;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.model.QRTicket;
import com.custom.wndapijwrap.*;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class PrinterService implements PrinterInterface {
    private CuCustomWndAPIJWrap cucjwrap = null;
    private CuCustomWndDevice cudev = null;

    public PrinterService(){
        // setup printer
        loadDLL();
    }

    private void loadDLL() {
        String[] args= new String[]{"."};
        try
        {
            // Direct DLL loading approach
            System.out.println("Attempting to load native library CuCustomWndAPI.dll...");

            // Method 1: Try to load from lib/Printer directory
            try {
                String dllPath = "E:\\Amay Technosystems\\AFC\\BackupTVM\\AgraCashTVM\\.";
                System.out.println("Trying to load from: " + dllPath);
                System.load(dllPath);
                System.out.println("SUCCESS: Native library loaded from lib/Printer directory!");
            } catch (UnsatisfiedLinkError e1) {
                System.out.println("Failed to load from lib/Printer: " + e1.getMessage());

                // Method 2: Try to load from absolute path
                try {
                    String absolutePath = System.getProperty("user.dir") + "/.";
                    System.out.println("Trying to load from absolute path: " + absolutePath);
                    System.load(absolutePath);
                    System.out.println("SUCCESS: Native library loaded from absolute path!");
                } catch (UnsatisfiedLinkError e2) {
                    System.out.println("Failed to load from absolute path: " + e2.getMessage());

                    // Method 3: Try to load using System.loadLibrary
                    try {
                        System.out.println("Trying to load using System.loadLibrary...");
                        System.loadLibrary("CuCustomWndAPI");
                        System.out.println("SUCCESS: Native library loaded using System.loadLibrary!");
                    } catch (UnsatisfiedLinkError e3) {
                        System.err.println("ERROR: All methods failed to load native library!");
                        System.err.println("Error 1: " + e1.getMessage());
                        System.err.println("Error 2: " + e2.getMessage());
                        System.err.println("Error 3: " + e3.getMessage());
                        System.err.println("Please ensure CuCustomWndAPI.dll is accessible");
                        return;
                    }
                }
            }

            //Create and Init the Class
            //Enable Log with the args paramether
            if (args.length > 0)
            {
                cucjwrap = new CuCustomWndAPIJWrap(CuCustomWndAPIJWrap.CcwLogVerbosity.CCW_LOG_DEBUG,args[0]);
            }
            else
            {
                cucjwrap = new CuCustomWndAPIJWrap();
            }
            //Init the library
            cucjwrap.InitLibrary();
            System.out.println("");
            System.out.println("CuCustomWndAPIJWrap Api version:"+cucjwrap.GetAPIVersion());
            //Get DLLs versions
            String strdllrels = cucjwrap.GetAPIVersionHwLibrary();
            System.out.println("low level DLL version:"+strdllrels);
            System.out.println("");
            openConnection();
        }
        catch(Exception e)
        {
            System.out.println("*** EXCEPTION: " + e);
        }
    }

    private void openConnection() {
        try
        {
            //If was open, close it
            if (cudev != null)
            {
                System.out.println("Previous device closed");
                cudev.Terminate();
                cudev = null;
            }

            //Enum the USB devices
            USBDevice[] udevArray = cucjwrap.EnumUSBDevices();
            if ((udevArray != null) && (udevArray.length > 0))
            {


                System.out.println("Try to connect USB device: "+udevArray[0]+" ...");
                //Open the 1st Device found
                cudev = cucjwrap.OpenPrinterUSB(udevArray[0]);
                System.out.println("OK!");
                System.out.println("Device Connected");
                printDeviceInfo(cudev);
            }
            else
                System.out.println("No devices found");
        }
        catch(Exception ctse)
        {
            System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
        }
    }

    private void printDeviceInfo(CuCustomWndDevice cudev) {
            try
            {
                System.out.println("Model: "+cudev.GetInfoDeviceModel());
                System.out.println("FW Release: "+cudev.GetInfoFirmwareVersion());
                System.out.println("Port Type: "+cudev.GetCapCommPortType());
                System.out.println("Print Resolution: "+cudev.GetCapPrinterResolution());
                System.out.println("Print Width: "+cudev.GetCapPrintWidth());
            }
            catch(CuCustomWndAPIJWrapException ctse)
            {
                System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
            }
            catch(Exception e)
            {
                System.out.println("*** EXCEPTION: " + e);
            }

    }

    @Override
    public void getStatus() {

        try {
            PrinterStatus ps = cudev.GetPrinterFullStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public BaseResponse printImage(BufferedImage image) {

        ImagePrintResponse imagePrintResponse=new ImagePrintResponse();
        PrintImageSettings pis = new PrintImageSettings();
        pis.PrintScaleMode = PrintImageSettings.ImageScale.IMAGE_SCALE_TO_FIT;
        pis.ImageAlignMode= PrintImageSettings.ImageAlign.IMAGE_ALIGN_TO_CENTER;

            try {
                cudev.PrintBitmapImage(image,pis);
                imagePrintResponse.setSuccess(true);
                cudev.Cut(CuCustomWndDevice.CutType.CUT_TOTAL);
            } catch (Exception e) {
                e.printStackTrace();
                imagePrintResponse.setSuccess(false).setError(e.getMessage());
            }
        return imagePrintResponse;
    }


    private void printImageByPath(){
        String path="src\\main\\resources\\images\\mpmrc.jpg";
        try {
            PrintImageSettings pis = new PrintImageSettings();
            pis.PrintScaleMode = PrintImageSettings.ImageScale.IMAGE_SCALE_NONE;
            pis.ImageAlignMode= PrintImageSettings.ImageAlign.IMAGE_ALIGN_TO_CENTER;

            cudev.PrintImageFromPath(path,pis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public BaseResponse printImageByText(QRTicket qrTicket) {
        ImagePrintResponse imagePrintResponse=new ImagePrintResponse();
            // print logo
            this.printImageByPath();

            // print qr code
            this.printQR(qrTicket.getQrCodeData());
            // print text
            // print bottom message
            this.printText(qrTicket);
            imagePrintResponse.setSuccess(true);


        return imagePrintResponse;
    }

    @Override
    public BaseResponse printImageQRImageByText(QRTicket qrTicket) {
        ImagePrintResponse imagePrintResponse=new ImagePrintResponse();
        // print logo
        this.printImageByPath();

        // print qr code
        this.printQRImage(qrTicket.getQrCode());
        // print text
        // print bottom message
        this.printText(qrTicket);
        imagePrintResponse.setSuccess(true);


        return imagePrintResponse;
    }

    private void printQRImage(Image qrCode) {
        try {
            PrintImageSettings pis = new PrintImageSettings();
            pis.PrintScaleMode = PrintImageSettings.ImageScale.IMAGE_SCALE_TO_WIDTH;
            pis.ImageAlignMode = PrintImageSettings.ImageAlign.IMAGE_ALIGN_TO_CENTER;
            BufferedImage bufferedImage = javafx.embed.swing.SwingFXUtils.fromFXImage(qrCode, null);
            cudev.PrintBitmapImage(bufferedImage, pis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse printImagesbyText(List<Object> list) {
        ImagePrintResponse imagePrintResponse=new ImagePrintResponse();
//        list.forEach(data->{
//        // print logo
//        this.printImageByPath();
//
//        // print qr code
//        this.printQR("23456");
//        // print text
//            // print bottom message
//        this.printText(data);
//        imagePrintResponse.setSuccess(true);
//        });


        return imagePrintResponse;
    }

    private void printText(QRTicket qrTicket) {

        if (qrTicket == null) {
            System.out.println("QRTicket is null. Skipping data population.");
            return;
        }
        System.out.println("Populating ticket data...");

        String initTime=qrTicket.getInitiateDateTime();
        String formatted = getFormatted(qrTicket, initTime);
        try {
            PrintFontSettings pfs=new PrintFontSettings();
            pfs.Emphasized=true;
            pfs.LeftMarginValue=10*10;
            pfs.LineSpacing=20;
            pfs.CharWidth= PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.CharHeight=PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.Justification= PrintFontSettings.FontJustification.FONT_JUSTIFICATION_LEFT;
            pfs.CharFontType=PrintFontSettings.FontType.FONT_TYPE_2;

            cudev.PrintText(formatted,pfs);
            cudev.Cut(CuCustomWndDevice.CutType.CUT_TOTAL);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static String getFormatted(QRTicket qrTicket, String initTime) {
        String paymentType = qrTicket.getFareMode();
        String equipmentId = SystemConfig.getInstance().getCurrentEquipment().getEquipmentId();
        String product = qrTicket.getType();
        String from = qrTicket.getFrom();
        String to = qrTicket.getTo();
        String price = "Rs. " + qrTicket.getPrice() + "/-";
        String ticketId = qrTicket.getTicketNo();

        // Use text block instead of escaped \n
        String ticketData = """
            
            Date/Time        : %s
            Payment Type     : %s
            Salepoint Id     : %s
            Type             : %s
            Platform NO.     : %s
            From             : %s
            To               : %s
            Price            : %s
            Ticket No.       : %s
            Valid Upto       : 120 minutes after entry

                Valid only for same working day
                
            """;

        // Insert values
        String formatted = String.format(ticketData, initTime, paymentType, equipmentId, product, "1", from, to, price, ticketId);
        return formatted;
    }


    private void printQR(String qrString) {
        try {
            PrintBarcodeSettings pbs = new PrintBarcodeSettings();
            pbs.BType= PrintBarcodeSettings.BarcodeType.BARCODE_TYPE_QRCODE;
            pbs.BarcodeWidth=200;
            pbs.BarcodeHeight=200;
            pbs.HRIPosition= PrintBarcodeSettings.BarcodeHRIPosition.BARCODE_HRI_TOP;
            pbs.AlignMode= PrintBarcodeSettings.BarcodeAlign.BARCODE_ALIGN_TO_CENTER;
            cudev.PrintBarcode(qrString,pbs);
        } catch (CuCustomWndAPIJWrapException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ImagePrintResponse printImage(String[] imageNames, String path, String extension) {
        ImagePrintResponse imagePrintResponse=new ImagePrintResponse();
        PrintImageSettings pis = new PrintImageSettings();
//        pis.PrintScaleMode = PrintImageSettings.ImageScale.IMAGE_SCALE_TO_FIT;
//        pis.ImageAlignMode= PrintImageSettings.ImageAlign.IMAGE_ALIGN_TO_CENTER;

        //Print Barcode
        Arrays.stream(imageNames).forEach(imageName-> {
            try {
                cudev.PrintImageFromPath(path+File.separator+imageName+extension, pis);
                imagePrintResponse.getImagesName().add(imageName);
                cudev.Cut(CuCustomWndDevice.CutType.CUT_TOTAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return imagePrintResponse;

    }

    @Override
    public BaseResponse cutPaper() {
        try {
            cudev.Cut(CuCustomWndDevice.CutType.CUT_TOTAL);
        } catch (Exception e) {
            return new BaseResponse().setSuccess(false).setError(e.getMessage());
        }
        return new BaseResponse().setSuccess(true).setError("Successfully Complete CUT");
    }


}
