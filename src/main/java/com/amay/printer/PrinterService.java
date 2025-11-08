package com.amay.printer;

import com.amay.printer.Response.BaseResponse;
import com.amay.printer.Response.ImagePrintResponse;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.model.QRTicket;
import com.amay.tom.repository.StationData;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.ui.Images;
import com.custom.wndapijwrap.*;
import javafx.scene.image.Image;
import org.tinylog.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class PrinterService implements PrinterInterface {
    private CuCustomWndAPIJWrap cucjwrap = null;
    private CuCustomWndDevice cudev = null;
    boolean isBusy=false;

    public PrinterService(){
        // setup printer
        loadDLL();
    }

    private void loadDLL() {
        String[] args= new String[]{"."};
        try
        {
            // Direct DLL loading approach
            //System.out.println("Attempting to load native library CuCustomWndAPI.dll...");

            // Method 1: Try to load from lib/Printer directory
            try {
                String dllPath = "E:\\Amay Technosystems\\AFC\\BackupTVM\\AgraCashTVM\\.";
                //System.out.println("Trying to load from: " + dllPath);
                System.load(dllPath);
                //System.out.println("SUCCESS: Native library loaded from lib/Printer directory!");
            } catch (UnsatisfiedLinkError e1) {
                //System.out.println("Failed to load from lib/Printer: " + e1.getMessage());

                // Method 2: Try to load from absolute path
                try {
                    String absolutePath = System.getProperty("user.dir") + "/.";
                    //System.out.println("Trying to load from absolute path: " + absolutePath);
                    System.load(absolutePath);
                    //System.out.println("SUCCESS: Native library loaded from absolute path!");
                } catch (UnsatisfiedLinkError e2) {
                    //System.out.println("Failed to load from absolute path: " + e2.getMessage());

                    // Method 3: Try to load using System.loadLibrary
                    try {
                        //System.out.println("Trying to load using System.loadLibrary...");
                        System.loadLibrary("CuCustomWndAPI");
                        //System.out.println("SUCCESS: Native library loaded using System.loadLibrary!");
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
            //System.out.println("");
            //System.out.println("CuCustomWndAPIJWrap Api version:"+cucjwrap.GetAPIVersion());
            //Get DLLs versions
            String strdllrels = cucjwrap.GetAPIVersionHwLibrary();
            //System.out.println("low level DLL version:"+strdllrels);
            //System.out.println("");
            openConnection();
        }
        catch(Exception e)
        {
            //System.out.println("*** EXCEPTION: " + e);
        }
    }

    public boolean isConnectedIfNotThenConnect(){
//        boolean status=isConnected();
        var allStatus=getStatus();
        if(null !=allStatus){
            return  !(allStatus.StsNOPAPER ||allStatus.StsPAPERJAM || allStatus.StsOVERTEMP) ;
        }
        openConnection();
        return false;
    }

    @Override
    public BaseResponse testPrint() {
        String formatted = """
                This is test print
                
                
                
                
                
                
                
                
                """;
        try {
            PrintFontSettings pfs=new PrintFontSettings();
            pfs.Emphasized=true;
            pfs.LeftMarginValue=10*10;
            pfs.LineSpacing=30;
            pfs.CharWidth= PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.CharHeight=PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.Justification= PrintFontSettings.FontJustification.FONT_JUSTIFICATION_LEFT;
            pfs.CharFontType=PrintFontSettings.FontType.FONT_TYPE_2;

            cudev.PrintText(formatted,pfs);
            cudev.Cut(CuCustomWndDevice.CutType.CUT_TOTAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BaseResponse().setSuccess(true).setError("Successfully Complete CUT");
    }

    private void openConnection() {
        try
        {
            //If was open, close it
            if (cudev != null)
            {
                //System.out.println("Previous device closed");
                cudev.Terminate();
                cudev = null;
            }

            //Enum the USB devices
            USBDevice[] udevArray = cucjwrap.EnumUSBDevices();
            if ((udevArray != null) && (udevArray.length > 0))
            {
                cudev = cucjwrap.OpenPrinterUSB(udevArray[0]);
                printDeviceInfo(cudev);
            }
            else
                Logger.tag(LoggerTag.APP).debug("No Printer devices found");
        }
        catch(Exception ctse)
        {
            Logger.tag(LoggerTag.APP).error("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
        }
    }

    private void printDeviceInfo(CuCustomWndDevice cudev) {
//            try
//            {
//                //System.out.println("Model: "+cudev.GetInfoDeviceModel());
//                //System.out.println("FW Release: "+cudev.GetInfoFirmwareVersion());
//                //System.out.println("Port Type: "+cudev.GetCapCommPortType());
//                //System.out.println("Print Resolution: "+cudev.GetCapPrinterResolution());
//                //System.out.println("Print Width: "+cudev.GetCapPrintWidth());
//            }
//            catch(CuCustomWndAPIJWrapException ctse)
//            {
//                //System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
//            }
//            catch(Exception e)
//            {
//                //System.out.println("*** EXCEPTION: " + e);
//            }

    }

//    private PrinterStatus printerStatus;

    @Override
    public PrinterStatus getStatus() {
//        if(isBusy){
//            return printerStatus;
//        }
        try {
            Logger.tag(LoggerTag.APP).debug("Printer status : {}",cudev.GetPrinterFullStatus());
            return cudev.GetPrinterFullStatus();
        } catch (Exception e) {
            Logger.tag(LoggerTag.APP).error("Printer Error : {}", Arrays.stream(e.getStackTrace()).toList());
        }

        return null;
    }

    public boolean isConnected()  {
        try {
            return isBusy || cudev.PrinterIsReady();
        }catch (Exception e){
            Logger.tag(LoggerTag.APP).error("ERROR : ",e.getMessage());
        }
        return false;
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
                imagePrintResponse.setSuccess(false).setError(e.getMessage());
            }
        return imagePrintResponse;
    }



    private void printImageByPath(){
        try {
            String path = Images.PROJECT_LOGO_FOR_TICKET;
            PrintImageSettings pis = new PrintImageSettings();
            pis.PrintScaleMode = PrintImageSettings.ImageScale.IMAGE_SCALE_NONE;
            pis.ImageAlignMode= PrintImageSettings.ImageAlign.IMAGE_ALIGN_TO_CENTER;

            cudev.PrintImageFromPath(path,pis);
        } catch (Exception e) {
            Logger.tag(LoggerTag.APP).error(e.getMessage());
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
    public BaseResponse printTicketsWithPayReceipt(List<QRTicket> qrTickets, PayReceipt payReceipt) {
        isBusy=true;
        ImagePrintResponse imagePrintResponse=new ImagePrintResponse();
        try {
            qrTickets.forEach(qrTicket -> {
                this.printImageByPath();
                this.printQR(qrTicket.getQrCodeData());
                this.printTextWithException(qrTicket);
                imagePrintResponse.getImagesName().add(qrTicket.getTicketNo());
            });
            imagePrintResponse.setSuccess(true);
            printReceipt(payReceipt);
        }catch(Exception exception){
            Logger.tag(LoggerTag.APP).error("Printer error {}", exception.fillInStackTrace());
        }finally {
            isBusy=false;
        }
        return imagePrintResponse;

    }

    private void printReceipt(PayReceipt payReceipt) throws Exception {
        if(null==payReceipt)return; 
        this.printImageByPath();
        String formatted = payReceipt.formatedText();
            PrintFontSettings pfs=new PrintFontSettings();
            pfs.Emphasized=true;
            pfs.LeftMarginValue=10*10;
            pfs.LineSpacing=30;
            pfs.CharWidth= PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.CharHeight=PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.Justification= PrintFontSettings.FontJustification.FONT_JUSTIFICATION_LEFT;
            pfs.CharFontType=PrintFontSettings.FontType.FONT_TYPE_2;

            cudev.PrintText(formatted,pfs);
            cudev.Cut(CuCustomWndDevice.CutType.CUT_TOTAL);

    }



    @Override
    public BaseResponse printImageByText(ShiftReportData shiftReportData) {
        ImagePrintResponse imagePrintResponse=new ImagePrintResponse();
        // print logo
        this.printImageByPath();
        // print bottom message
        this.printText(shiftReportData);
        imagePrintResponse.setSuccess(true);

        return imagePrintResponse;
    }
    @Override
    public BaseResponse printBNRText(BNRLoadUnload bnRLoadUnload){
        ImagePrintResponse imagePrintResponse=new ImagePrintResponse();
        // print logo
        this.printImageByPath();
        // print bottom message
        this.printBNRLoadUnload(bnRLoadUnload);
        imagePrintResponse.setSuccess(true);

        return imagePrintResponse;
    }
    @Override
    public BaseResponse printCoinLoadedReport(CoinLoadedReport coinLoadedReport){
        ImagePrintResponse imagePrintResponse=new ImagePrintResponse();
        // print logo
        this.printImageByPath();
        // print bottom message
        this.coinLoadedReport(coinLoadedReport);
        imagePrintResponse.setSuccess(true);

        return imagePrintResponse;
    }


    @Override
    public BaseResponse printBalanceReport(BalanceReport balanceReport){
        ImagePrintResponse imagePrintResponse=new ImagePrintResponse();
        // print logo
        this.printImageByPath();
        // print bottom message
        this.balanceReport(balanceReport);
        imagePrintResponse.setSuccess(true);

        return imagePrintResponse;
    }

    @Override
    public PrinterStatus printerStatus() {
        PrinterStatus ps = null;
        try {
            ps = cudev.GetPrinterFullStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ps;
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


    private void printTextWithException(QRTicket qrTicket)  {

        if (qrTicket == null) {
            //System.out.println("QRTicket is null. Skipping data population.");
            return;
        }
        //System.out.println("Populating ticket data...");

        String formatted = getFormatted(qrTicket);
            PrintFontSettings pfs=new PrintFontSettings();
            pfs.Emphasized=true;
            pfs.LeftMarginValue=10*10;
            pfs.LineSpacing=30;
            pfs.CharWidth= PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.CharHeight=PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.Justification= PrintFontSettings.FontJustification.FONT_JUSTIFICATION_LEFT;
            pfs.CharFontType=PrintFontSettings.FontType.FONT_TYPE_2;

        try {
            cudev.PrintText(formatted,pfs);
            cudev.Cut(CuCustomWndDevice.CutType.CUT_TOTAL);
        } catch (Exception e) {
            Logger.tag(LoggerTag.APP).error("Cut Error : {}",e.getMessage());
            throw new RuntimeException(e);
        }


    }



    private void printText(QRTicket qrTicket) {

        if (qrTicket == null) {
            //System.out.println("QRTicket is null. Skipping data population.");
            return;
        }
        //System.out.println("Populating ticket data...");

        String formatted = getFormatted(qrTicket);
        try {
            PrintFontSettings pfs=new PrintFontSettings();
            pfs.Emphasized=true;
            pfs.LeftMarginValue=10*10;
            pfs.LineSpacing=30;
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

    private void printText(ShiftReportData shiftReportData){
        String formated=getShiftReportTemplate();
        formated=fillTheData(formated,shiftReportData);

        try {
            PrintFontSettings pfs=new PrintFontSettings();
            pfs.Emphasized=true;
            pfs.LeftMarginValue=10*10;
            pfs.LineSpacing=30;
            pfs.CharWidth= PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.CharHeight=PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.Justification= PrintFontSettings.FontJustification.FONT_JUSTIFICATION_LEFT;
            pfs.CharFontType=PrintFontSettings.FontType.FONT_TYPE_2;

            cudev.PrintText(formated,pfs);
            cudev.Cut(CuCustomWndDevice.CutType.CUT_TOTAL);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void printBNRLoadUnload(BNRLoadUnload bnRLoadUnload){
        String formated=getBNRLoadUnloadTemplate();
        formated=fillTheData(formated,bnRLoadUnload);

        try {
            PrintFontSettings pfs=new PrintFontSettings();
            pfs.Emphasized=true;
            pfs.LeftMarginValue=10*10;
            pfs.LineSpacing=30;
            pfs.CharWidth= PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.CharHeight=PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.Justification= PrintFontSettings.FontJustification.FONT_JUSTIFICATION_LEFT;
            pfs.CharFontType=PrintFontSettings.FontType.FONT_TYPE_2;

            cudev.PrintText(formated,pfs);
            cudev.Cut(CuCustomWndDevice.CutType.CUT_TOTAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void coinLoadedReport(CoinLoadedReport coinLoadedReport){
        String formated=getCoinLoadedReportTemplate();
        formated=fillTheData(formated,coinLoadedReport);

        try {
            PrintFontSettings pfs=new PrintFontSettings();
            pfs.Emphasized=true;
            pfs.LeftMarginValue=10*10;
            pfs.LineSpacing=30;
            pfs.CharWidth= PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.CharHeight=PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.Justification= PrintFontSettings.FontJustification.FONT_JUSTIFICATION_LEFT;
            pfs.CharFontType=PrintFontSettings.FontType.FONT_TYPE_2;

            cudev.PrintText(formated,pfs);
            cudev.Cut(CuCustomWndDevice.CutType.CUT_TOTAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void balanceReport(BalanceReport balanceReport){
        String formated=getBalanceReportTemplate();
        formated=fillTheData(formated,balanceReport);

        try {
            PrintFontSettings pfs=new PrintFontSettings();
            pfs.Emphasized=true;
            pfs.LeftMarginValue=10*10;
            pfs.LineSpacing=30;
            pfs.CharWidth= PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.CharHeight=PrintFontSettings.FontSize.FONT_SIZE_X1;
            pfs.Justification= PrintFontSettings.FontJustification.FONT_JUSTIFICATION_LEFT;
            pfs.CharFontType=PrintFontSettings.FontType.FONT_TYPE_2;

            cudev.PrintText(formated,pfs);
            cudev.Cut(CuCustomWndDevice.CutType.CUT_TOTAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fillTheData(String receiptTemplate, ShiftReportData shiftReportData) {
          return String.format(receiptTemplate,
                shiftReportData.getStationName(),
                shiftReportData.getShiftId(),
                shiftReportData.getStartTime(),
                shiftReportData.getEndTime(),
                shiftReportData.getEquipmentId(),
                shiftReportData.getOperatorId(),
                shiftReportData.getImpressMoney(),

                shiftReportData.getSjtCashCount(),
                shiftReportData.getSjtCashAmount(),
                shiftReportData.getRjtCashCount(),
                shiftReportData.getRjtCashAmount(),
                shiftReportData.getGtCashCount(),
                shiftReportData.getGtCashAmount(),
                shiftReportData.getSjtUpiCount(),
                shiftReportData.getSjtUpiAmount(),
                shiftReportData.getRjtUpiCount(),
                shiftReportData.getRjtUpiAmount(),
                shiftReportData.getGtUpiCount(),
                shiftReportData.getGtUpiAmount(),
                shiftReportData.getSjtPosCount(),
                shiftReportData.getSjtPosAmount(),
                shiftReportData.getRjtPosCount(),
                shiftReportData.getRjtPosAmount(),
                shiftReportData.getGtPosCount(),
                shiftReportData.getGtPosAmount(),
                shiftReportData.getQrTotalCount(),
                shiftReportData.getQrTotalAmount(),

                shiftReportData.getNcmcCashCount(),
                shiftReportData.getNcmcCashAmount(),
                shiftReportData.getNcmcUpiCount(),
                shiftReportData.getNcmcUpiAmount(),
                shiftReportData.getNcmcPosCount(),
                shiftReportData.getNcmcPosAmount(),
                shiftReportData.getNcmcTotalCount(),
                shiftReportData.getNcmcTotalAmount(),

                shiftReportData.getRs10Count(),
                shiftReportData.getRs10Amount(),
                shiftReportData.getRs20Count(),
                shiftReportData.getRs20Amount(),
                shiftReportData.getRs50Count(),
                shiftReportData.getRs50Amount(),
                shiftReportData.getRs100Count(),
                shiftReportData.getRs100Amount(),
                shiftReportData.getRs200Count(),
                shiftReportData.getRs200Amount(),
                shiftReportData.getRs500Count(),
                shiftReportData.getRs500Amount(),
                shiftReportData.getBankTotalCount(),
                shiftReportData.getBankTotalAmount(),

                shiftReportData.getHopper1Count(),
                shiftReportData.getHopper1Amount(),
                shiftReportData.getHopper2Count(),
                shiftReportData.getHopper2Amount(),
                shiftReportData.getHopper3Count(),
                shiftReportData.getHopper3Amount(),
                shiftReportData.getCoinTotalCount(),
                shiftReportData.getCoinTotalAmount(),

                shiftReportData.getTotalCashSales(),
                shiftReportData.getTotalUpiSales(),
                shiftReportData.getTotalPosSales(),
                shiftReportData.getTotalRevenue(),
                shiftReportData.getAvailableCash(),
                shiftReportData.getPrintTime()
        );

    }
    private String fillTheData(String bnrReportTemplate, BNRLoadUnload bnRLoadUnload) {
          return String.format(bnrReportTemplate,
                bnRLoadUnload.getReportType(),
                bnRLoadUnload.getStationName(),
                  bnRLoadUnload.getShiftId(),
                  bnRLoadUnload.getStartTime(),
                  bnRLoadUnload.getEndTime(),
                  bnRLoadUnload.getEquipmentId(),
                  bnRLoadUnload.getOperatorId(),



                  bnRLoadUnload.getRs10Count(),
                  bnRLoadUnload.getRs10Amount(),
                  bnRLoadUnload.getRs20Count(),
                  bnRLoadUnload.getRs20Amount(),
                  bnRLoadUnload.getRs50Count(),
                  bnRLoadUnload.getRs50Amount(),
                  bnRLoadUnload.getRs100Count(),
                  bnRLoadUnload.getRs100Amount(),
                  bnRLoadUnload.getRs200Count(),
                  bnRLoadUnload.getRs200Amount(),
                  bnRLoadUnload.getRs500Count(),
                  bnRLoadUnload.getRs500Amount(),
                  bnRLoadUnload.getBankTotalCount(),
                  bnRLoadUnload.getBankTotalAmount()
        );
    }
    private String fillTheData(String coinLoadedReportTemplate, CoinLoadedReport coinLoadedReport) {
          return String.format(coinLoadedReportTemplate,
                coinLoadedReport.getReportType(),
                coinLoadedReport.getStationName(),
                  coinLoadedReport.getShiftId(),
                  coinLoadedReport.getStartTime(),
                  coinLoadedReport.getEndTime(),
                  coinLoadedReport.getEquipmentId(),
                  coinLoadedReport.getOperatorId(),



                  coinLoadedReport.getHopper1Count(),
                  coinLoadedReport.getHopper1Amount(),
                  coinLoadedReport.getHopper2Count(),
                  coinLoadedReport.getHopper2Amount(),
                  coinLoadedReport.getHopper3Count(),
                  coinLoadedReport.getHopper3Amount(),

                  coinLoadedReport.getCoinTotalCount(),
                  coinLoadedReport.getCoinTotalAmount()

        );
    }
    private String fillTheData(String balanceReportTemplate, BalanceReport balanceReport) {
          return String.format(balanceReportTemplate,
                balanceReport.getReportType(),
                balanceReport.getStationName(),
                  balanceReport.getShiftId(),
                  balanceReport.getStartTime(),
                  balanceReport.getEndTime(),
                  balanceReport.getEquipmentId(),
                  balanceReport.getOperatorId(),

                  balanceReport.getRs10Count(),
                  balanceReport.getRs10Amount(),
                  balanceReport.getRs20Count(),
                  balanceReport.getRs20Amount(),
                  balanceReport.getRs50Count(),
                  balanceReport.getRs50Amount(),
                  balanceReport.getRs100Count(),
                  balanceReport.getRs100Amount(),
                  balanceReport.getRs200Count(),
                  balanceReport.getRs200Amount(),
                  balanceReport.getRs500Count(),
                  balanceReport.getRs500Amount(),
                  balanceReport.getBankTotalCount(),
                  balanceReport.getBankTotalAmount(),

                  balanceReport.getHopper1Count(),
                  balanceReport.getHopper1Amount(),
                  balanceReport.getHopper2Count(),
                  balanceReport.getHopper2Amount(),
                  balanceReport.getHopper3Count(),
                  balanceReport.getHopper3Amount(),

                  balanceReport.getCoinTotalCount(),
                  balanceReport.getCoinTotalAmount()

        );
    }

    private static String getFormatted(QRTicket qrTicket) {
        String paymentType = qrTicket.getFareMode();
        String equipmentId = SystemConfig.getInstance().getCurrentEquipment().getEquipmentId();
        String product = qrTicket.getType();
        String from = qrTicket.getFrom();
        String to = qrTicket.getTo();
        String price = "Rs. " + qrTicket.getPrice() + "/-";
        String ticketId = qrTicket.getTicketNo();
        String initTime = qrTicket.getInitiateDateTime();
        String quantity = qrTicket.getQty() > 1 ? " (" + qrTicket.getQty() + ")" : "";

        // Use text block instead of escaped \n
        String ticketData = """
            
            Date-Time        : %s
            Payment Type     : %s
            Salepoint Id     : %s
            Type             : %s %s
            Platform No.     : %s
            From             : %s
            To               : %s
            Price            : %s
            Ticket No.       : %s
            Valid Upto       : 120 minutes after entry

                Valid only for same working day
                
            """;

        // Insert values
        String platformNo = String.valueOf(StationData.getInstance().getPlatform(qrTicket.getFrom(),qrTicket.getTo()));
        String formatted = String.format(ticketData, initTime, paymentType, equipmentId, product,quantity, platformNo, from, to, price, ticketId);
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
        } catch (Exception e) {
            Logger.tag(LoggerTag.APP).error(e.getMessage());
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

    private static String getShiftReportTemplate() {
        String receiptTemplate = """
   
                INDORE METRO
             END OF SHIFT REPORT
             
    
    Station Name   : %s
    Shift ID       : %s
    Shift Start    : %s
    Shift End      : %s
    Equipment ID   : %s
    Operator ID    : %s
     
   
                START BALANCE
   
    Impress Money       :     Rs. %s
    
    
                QR SALE TRANSACTIONS
   
    Transaction         Cnt    Amount
    
    SJT Cash            %s     Rs. %s
    RJT Cash            %s     Rs. %s
    GT Cash             %s     Rs. %s
    SJT UPI             %s     Rs. %s
    RJT UPI             %s     Rs. %s
    GT UPI              %s     Rs. %s
    SJT POS             %s     Rs. %s
    RJT POS             %s     Rs. %s
    GT POS              %s     Rs. %s
    
    Total               %s     Rs. %s
    
    
                NCMC TRANSACTIONS
   
    Transaction         Cnt    Amount
    
    NCMC Top Up Cash    %s     Rs. %s
    NCMC Top Up UPI     %s     Rs. %s
    NCMC Top Up POS     %s     Rs. %s
    
    Total               %s     Rs. %s
    
    
                BANK NOTE DETAILS
                
    Bill Type           Cnt     Amount
    
     10                 %s      Rs. %s
     20                 %s      Rs. %s
     50                 %s      Rs. %s
     100                %s      Rs. %s
     200                %s      Rs. %s
     500                %s      Rs. %s
    
    Total               %s      Rs. %s
    
    
                COIN DETAILS
   
    Coin Type           Cnt      Amount
    
     5                  %s       Rs. %s
     10                 %s       Rs. %s
     10                 %s       Rs. %s
    
    Total               %s       Rs. %s
    
    
                SHIFT SUMMARY
   
    Total Sale by Cash  :        Rs. %s
    Total Sale by UPI   :        Rs. %s
    Total Sale by POS   :        Rs. %s
    
    Total Revenue       :        Rs. %s
    
    Available Cash      :        Rs. %s
   
    Print Time  : %s
    
    """;



        return receiptTemplate;
    }
    private static String getBNRLoadUnloadTemplate() {
        String bnrReportTemplate = """   
   
                INDORE METRO
             %s
              
    
    Station Name   : %s
    Shift ID       : %s
    Shift Start    : %s
    Shift End      : %s
    Equipment ID   : %s
    Operator ID    : %s
    
                BANK NOTE DETAILS
                
    Bill Type           Cnt     Amount
    
     10                 %s      Rs. %s
     20                 %s      Rs. %s
     50                 %s      Rs. %s
     100                %s      Rs. %s
     200                %s      Rs. %s
     500                %s      Rs. %s
    
    Total               %s      Rs. %s
    
    
    """;
        return bnrReportTemplate;
    }
    private static String getCoinLoadedReportTemplate() {
        String coinLoadedReportTemplate = """   
   
                INDORE METRO
             %s
              
    Station Name   : %s
    Shift ID       : %s
    Shift Start    : %s
    Shift End      : %s
    Equipment ID   : %s
    Operator ID    : %s
    
                 COIN DETAILS
   
    Coin Type           Cnt      Amount
    
     5                  %s       Rs. %s
     10                 %s       Rs. %s
     10                 %s       Rs. %s
    
    Total               %s      Rs. %s
    
    """;
        return coinLoadedReportTemplate;
    }
    private static String getBalanceReportTemplate(){
        String balanceLoadedReportTemplate = """   
   
                INDORE METRO
             %s
              
    Station Name   : %s
    Shift ID       : %s
    Shift Start    : %s
    Shift End      : %s
    Equipment ID   : %s
    Operator ID    : %s
    
    
    
               BANK NOTE DETAILS
                
    Bill Type           Cnt     Amount
    
     10                 %s      Rs. %s
     20                 %s      Rs. %s
     50                 %s      Rs. %s
     100                %s      Rs. %s
     200                %s      Rs. %s
     500                %s      Rs. %s
    
    Total               %s      Rs. %s
    
    
    
                 COIN DETAILS
   
    Coin Type           Cnt      Amount
    
     5                  %s       Rs. %s
     10                 %s       Rs. %s
     10                 %s       Rs. %s
    
    Total               %s      Rs. %s
    
    """;
        return balanceLoadedReportTemplate;
    }

}
