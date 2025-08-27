package com.amay.printer;

import com.amay.printer.Response.BaseResponse;
import com.amay.printer.Response.ImagePrintResponse;
import com.custom.wndapijwrap.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

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
                String dllPath = ".";
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

    @Override
    public ImagePrintResponse printImage(String[] imageNames, String path, String extension) {
        ImagePrintResponse imagePrintResponse=new ImagePrintResponse();
        PrintImageSettings pis = new PrintImageSettings();
        pis.PrintScaleMode = PrintImageSettings.ImageScale.IMAGE_SCALE_TO_FIT;
        pis.ImageAlignMode= PrintImageSettings.ImageAlign.IMAGE_ALIGN_TO_CENTER;

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
