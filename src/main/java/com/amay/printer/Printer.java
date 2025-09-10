package com.amay.printer;

//************************************************************************************
//*                                                                                  *
//* This document contains programming examples.                                     *
//*                                                                                  *
//* CUSTOM S.p.A. grants you a nonexclusive copyright license to use                 *
//* all programming code examples from which you can generate similar                *
//* function tailored to your own specific needs.                                    *
//*                                                                                  *
//* All sample code is provided by CUSTOM S.p.A. for illustrative purposes           *
//* only. These examples have not been thoroughly tested under all conditions.       *
//* CUSTOM S.p.A., therefore, cannot guarantee or imply reliability,                 *
//* serviceability, or function of these programs.                                   *
//*                                                                                  *
//* In no event shall CUSTOM S.p.A. be liable for any direct, indirect,              *
//* incidental, special, exemplary, or consequential damages (including, but not     *
//* limited to, procurement of substitute goods or services; loss of use, data,      *
//* or profits; or business interruption) however caused and on any theory of        *
//* liability, whether in contract, strict liability, or tort (including negligence  *
//* or otherwise) arising in any way out of the use of this software, even if        *
//* advised of the possibility of such damage.                                       *
//*                                                                                  *
//* All programs contained herein are provided to you "as is" without any            *
//* warranties of any kind.                                                          *
//* The implied warranties of non-infringement, merchantability and fitness for a    *
//* particular purpose are expressly disclaimed.                                     *
//*                                                                                  *
//************************************************************************************


import com.amay.tvm.backend.enums.LoggerTag;
import com.custom.wndapijwrap.*;
import com.amay.utils.NativeLibraryLoader;
import org.tinylog.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Printer {

    static CuCustomWndAPIJWrap cucjwrap = null;
    static CuCustomWndDevice cudev = null;


    static void  PrintMenu()
    {

        //Show the Keys
        //System.out.println("");
        //System.out.println("CuCustomWndAPIJWrap Menu:\n");
        //System.out.println("1. Open a USB device");
        //System.out.println("2. Open a ETH/WIFI device");
        //System.out.println("3. Open a SERIAL device");
        //System.out.println("4. Open a installed Printer");

        //System.out.println("11. Feed");
        //System.out.println("12. Cut");
        //System.out.println("13. Align");
        //System.out.println("14. Present");
        //System.out.println("15. Open Cash Drawer");

        //System.out.println("21. Print Text");
        //System.out.println("22. Print Barcode");
        //System.out.println("23. Print Image");

        //System.out.println("31. Status");

        //System.out.println("");
        //System.out.println("Q. Exit");

        //System.out.println("");
        //System.out.print("select the operation to do:");
    }

    public static void PrintDeviceInfo(CuCustomWndDevice cudev)
    {
        try
        {
            Logger.tag(LoggerTag.APP).debug("Model: "+cudev.GetInfoDeviceModel());
            Logger.tag(LoggerTag.APP).debug("FW Release: "+cudev.GetInfoFirmwareVersion());
            Logger.tag(LoggerTag.APP).debug("Port Type: "+cudev.GetCapCommPortType());
            Logger.tag(LoggerTag.APP).debug("Print Resolution: "+cudev.GetCapPrinterResolution());
            Logger.tag(LoggerTag.APP).debug("Print Width: "+cudev.GetCapPrintWidth());
        }
        catch(CuCustomWndAPIJWrapException ctse)
        {
            //System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
            Logger.tag(LoggerTag.APP).error("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
        }
        catch(Exception e)
        {
            //System.out.println("*** EXCEPTION: " + e);
            Logger.tag(LoggerTag.APP).error("*** EXCEPTION: " + e);
        }
    }

    public static int WaitCommand()
    {
        BufferedReader in = null;
        // TODO code application logic here
        in = new BufferedReader(new InputStreamReader(System.in));
        String userInput = null;

        //Read the Asnwer
        try
        {
            userInput = in.readLine();
        }
        catch(Exception e)
        {
        }

        //CR without value
        if (userInput == null || userInput.length() == 0)
            return 0;

        char c = userInput.charAt(0);
        //Quit
        if (c == 'q' || c == 'Q')
            return -1;

        //Try to conver to integer
        try
        {
            int iNum = Integer.parseInt(userInput);
            return iNum;
        }
        catch(Exception e)
        {
        }

        return 0;
    }

    public static String WaitString()
    {
        BufferedReader in = null;

        // TODO code application logic here
        in = new BufferedReader(new InputStreamReader(System.in));

        String userInput = null;

        //Read the Asnwer
        try
        {
            userInput = in.readLine();
        }
        catch(Exception e)
        {
        }

        return userInput;
    }

    /**
     * @param args the command line arguments
     */
    public static void Main(String[] args)
    {


        boolean bExit = false;


        try
        {
            // Direct DLL loading approach
            //System.out.println("Attempting to load native library CuCustomWndAPI.dll...");
            
            // Method 1: Try to load from lib/Printer directory
            try {
                String dllPath = ".";
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
        }
        catch(Exception e)
        {
            //System.out.println("*** EXCEPTION: " + e);
            return;
        }

        do
        {
            //Show the menu
            PrintMenu();

            //Wait command
            int imenuc = WaitCommand();
            switch (imenuc)
            {
                //Open the Device
                case 1:
                {
                    //System.out.println("-> Open the Device");
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
                            //Fill a list
                            //System.out.println("USB devices available: ");
                            for(int i=0;i<udevArray.length;i++)
                            {
                                //System.out.println((i+1)+") "+udevArray[i].SerialNumber);
                            }
                            //System.out.println("Q. Exit");
                            //System.out.print("Select the device to open: ");
                            int idev = WaitCommand();
                            if (idev <= 0 || idev > udevArray.length)
                                break;
                            idev--;

                            //System.out.println("Try to connect USB device: "+udevArray[idev]+" ...");
                            //Open the 1st Device found
                            cudev = cucjwrap.OpenPrinterUSB(udevArray[idev]);
                            //System.out.println("OK!");
                            //System.out.println("Device Connected");
                            PrintDeviceInfo(cudev);
                        }
                        else
                            //System.out.println("No devices found");
                        Logger.tag(LoggerTag.APP).info("No USB devices found");
                    }
                    catch(CuCustomWndAPIJWrapException ctse)
                    {
                        //System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
                    }
                    catch(Exception e)
                    {
                        //System.out.println("*** EXCEPTION: " + e);
                    }

                    break;
                }

                //Open the Device
                case 2:
                {
                    //System.out.println("-> Open the Device");
                    try
                    {
                        //If was open, close it
                        if (cudev != null)
                        {
                            //System.out.println("Previous device closed");
                            cudev.Terminate();
                            cudev = null;
                        }

                        //Enum the ETH/WIFI devices
                        ETHDevice[] edevArray = cucjwrap.EnumETHDevices(500);
                        if ((edevArray != null) && (edevArray.length > 0))
                        {
                            //Fill a list
                            //System.out.println("ETH/WIFI devices available: ");
                            for(int i=0;i<edevArray.length;i++)
                            {
                                //System.out.println((i+1)+") "+edevArray[i].IPAddress+" ("+edevArray[i].DeviceName+")");
                            }
                            //System.out.println("Q. Exit");
                            //System.out.print("Select the device to open: ");
                            int idev = WaitCommand();
                            if (idev <= 0 || idev > edevArray.length)
                                break;
                            idev--;

                            //System.out.println("Try to connect ETH/WIFI device: "+edevArray[idev]+" ...");
                            //Open the 1st Device found
                            cudev = cucjwrap.OpenPrinterETH(edevArray[idev]);
                            //System.out.println("OK!");
                            //System.out.println("Device Connected");
                            PrintDeviceInfo(cudev);
                        }
                        else
                            //System.out.println("No devices found");
                            Logger.tag(LoggerTag.APP).info("No USB devices found");
                    }
                    catch(CuCustomWndAPIJWrapException ctse)
                    {
                        //System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
                    }
                    catch(Exception e)
                    {
                        //System.out.println("*** EXCEPTION: " + e);
                    }

                    break;
                }

                //Open the Device
                case 3:
                {
                    //System.out.println("-> Open the Device");
                    try
                    {
                        //If was open, close it
                        if (cudev != null)
                        {
                            //System.out.println("Previous device closed");
                            cudev.Terminate();
                            cudev = null;
                        }

                        //Enum the COM ports
                        COMPort[] cportsArray = cucjwrap.EnumCOMPorts();
                        if ((cportsArray != null) && (cportsArray.length > 0))
                        {
                            //Fill a list
                            //System.out.println("COM ports available: ");
                            for(int i=0;i<cportsArray.length;i++)
                            {
                                //System.out.println((i+1)+") "+cportsArray[i].Port);
                            }
                            //System.out.println("Q. Exit");
                            //System.out.print("Select the port to open: ");
                            int idev = WaitCommand();
                            if (idev <= 0 || idev > cportsArray.length)
                                break;
                            idev--;

                            //System.out.println("Try to connect a device connect to: "+cportsArray[idev]+" ...");
                            //Open the 1st Device found
                            cudev = cucjwrap.OpenPrinterCOM(cportsArray[idev]);
                            //System.out.println("OK!");
                            //System.out.println("Device Connected");
                            PrintDeviceInfo(cudev);
                        }
                        else
                            //System.out.println("No devices found");
                        Logger.tag(LoggerTag.APP).error("No COM ports found" );
                    }
                    catch(CuCustomWndAPIJWrapException ctse)
                    {
                        //System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
                    }
                    catch(Exception e)
                    {
                        //System.out.println("*** EXCEPTION: " + e);
                    }

                    break;
                }

                //Open the Device
                case 4:
                {
                    //System.out.println("-> Open the Device");
                    try
                    {
                        //If was open, close it
                        if (cudev != null)
                        {
                            //System.out.println("Previous device closed");
                            cudev.Terminate();
                            cudev = null;
                        }

                        //Enum the Printer
                        PrinterInfo[] prnArray = cucjwrap.EnumInstalledPrinters();
                        if ((prnArray != null) && (prnArray.length > 0))
                        {
                            //Fill a list
                            //System.out.println("Printers available: ");
                            for(int i=0;i<prnArray.length;i++)
                            {
                                //System.out.println((i+1)+") "+prnArray[i].PrinterName);
                            }
                            //System.out.println("Q. Exit");
                            //System.out.print("Select the printer to open: ");
                            int idev = WaitCommand();
                            if (idev <= 0 || idev > prnArray.length)
                                break;
                            idev--;

                            //System.out.println("Try to connect to the printer: "+prnArray[idev].PrinterName+" ...");
                            //Open the 1st Device found
                            cudev = cucjwrap.OpenInstalledPrinter(prnArray[idev].PrinterName);
                            //System.out.println("OK!");
                            //System.out.println("Device Connected");
                            PrintDeviceInfo(cudev);
                        }
                        else
                            //System.out.println("No devices found");
                            Logger.tag(LoggerTag.APP).info("No USB devices found");
                    }
                    catch(CuCustomWndAPIJWrapException ctse)
                    {
                        //System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
                    }
                    catch(Exception e)
                    {
                        //System.out.println("*** EXCEPTION: " + e);
                    }

                    break;
                }

                //Feed
                case 11:
                {
                    //System.out.println("-> Feed (X3)");
                    try
                    {
                        cudev.Feed(10);
                    }
                    catch(CuCustomWndAPIJWrapException ctse)
                    {
                        //System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
                    }
                    catch(Exception e)
                    {
                        //System.out.println("*** EXCEPTION: " + e);
                    }

                    break;
                }

                //Cut
                case 12:
                {
                    //System.out.println("-> Cut (Total)");
                    try
                    {
                        cudev.Cut(CuCustomWndDevice.CutType.CUT_TOTAL);
                    }
                    catch(CuCustomWndAPIJWrapException ctse)
                    {
                        //System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
                    }
                    catch(Exception e)
                    {
                        //System.out.println("*** EXCEPTION: " + e);
                    }

                    break;
                }

                //Align
                case 13:
                {
                    //System.out.println("-> Align (Cutter)");
                    try
                    {
                        cudev.Align(CuCustomWndDevice.PaperAlignType.PALIGN_CUT);
                    }
                    catch(CuCustomWndAPIJWrapException ctse)
                    {
                        //System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
                    }
                    catch(Exception e)
                    {
                        //System.out.println("*** EXCEPTION: " + e);
                    }

                    break;
                }

                //Present
                case 14:
                {
                    //System.out.println("-> Present (10mm)");
                    try
                    {
                        cudev.Present(10);
                    }
                    catch(CuCustomWndAPIJWrapException ctse)
                    {
                        //System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
                    }
                    catch(Exception e)
                    {
                        //System.out.println("*** EXCEPTION: " + e);
                    }

                    break;
                }

                //Open Cash Drawer
                case 15:
                {
                    //System.out.println("Open Cash Drawer");

                    //Delcare the time on and time off of the impulse
                    int timeOn = 500;
                    int timeOff = 200;
                    CuCustomWndDevice.CashDrawerType drawerType = CuCustomWndDevice.CashDrawerType.CASH_DRAWER_2;

                    try
                    {
                        cudev.OpenCashDrawer(drawerType, timeOn, timeOff);
                    }
                    catch(CuCustomWndAPIJWrapException ctse)
                    {
                        //System.out.println("*** EXCEPTION: " + ctse + "(" + ctse.getMessage() + ")" );
                    }
                    catch(Exception e)
                    {
                        //System.out.println("*** EXCEPTION: " + e);
                    }

                    break;
                }

                //Print Text
                case 21:
                {
                    try
                    {
                        //Wait text
                        //System.out.print("Insert the text to print: ");
                        String strText = WaitString();
                        if (strText == null || strText.length() == 0)
                            break;

                        PrintFontSettings pfs = new PrintFontSettings();
                        pfs.Emphasized = true;
                        pfs.Justification = PrintFontSettings.FontJustification.FONT_JUSTIFICATION_CENTER;

                        //Print Text
                        cudev.PrintText(strText, pfs, true);
                    }
                    catch(CuCustomWndAPIJWrapException ctse)
                    {
                        //System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
                    }
                    catch(Exception e)
                    {
                        //System.out.println("*** EXCEPTION: " + e);
                    }

                    break;
                }

                //Print Barcode
                case 22:
                {
                    try
                    {
                        //Wait barcode text
                        //System.out.print("Insert the text to print into QRCODE: ");
                        String strText = WaitString();
                        if (strText == null || strText.length() == 0)
                            break;

                        PrintBarcodeSettings pbs = new PrintBarcodeSettings();
                        pbs.BType = PrintBarcodeSettings.BarcodeType.BARCODE_TYPE_QRCODE;
                        pbs.AlignMode = PrintBarcodeSettings.BarcodeAlign.BARCODE_ALIGN_TO_CENTER;
                        pbs.BarcodeWidth = 200;
                        pbs.BarcodeHeight = 200;

                        //Print Barcode
                        cudev.PrintBarcode(strText, pbs);
                    }
                    catch(CuCustomWndAPIJWrapException ctse)
                    {
                        //System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
                    }
                    catch(Exception e)
                    {
                        //System.out.println("*** EXCEPTION: " + e);
                    }

                    break;
                }

                //Print Image
                case 23:
                {
                    try
                    {
                        //Wait Image path
                        //System.out.print("Insert the path of the image to print: ");
                        String strPath = WaitString();
                        if (strPath == null || strPath.length() == 0)
                            break;

                        PrintImageSettings pis = new PrintImageSettings();
                        pis.PrintScaleMode = PrintImageSettings.ImageScale.IMAGE_SCALE_TO_FIT;

                        //Print Barcode
                        cudev.PrintImageFromPath(strPath, pis);
                    }
                    catch(CuCustomWndAPIJWrapException ctse)
                    {
                        //System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
                    }
                    catch(Exception e)
                    {
                        //System.out.println("*** EXCEPTION: " + e);
                    }

                    break;
                }

                //Status
                case 31:
                {
                    //System.out.println("-> Status");
                    try
                    {
                        PrinterStatus ps = cudev.GetPrinterFullStatus();
                        //System.out.println(ps);
                    }
                    catch(CuCustomWndAPIJWrapException ctse)
                    {
                        //System.out.println("*** EXCEPTION: " + ctse + " ("+ctse.getMessage()+")");
                    }
                    catch(Exception e)
                    {
                        //System.out.println("*** EXCEPTION: " + e);
                    }

                    break;
                }

                //Exit
                case -1:
                {
                    //System.out.println("-> EXIT");
                    bExit = true;
                    break;
                }
            }
        }
        while(bExit == false);


    }

}
