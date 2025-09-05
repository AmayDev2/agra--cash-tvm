//package com.amay.tom.service.print;
//
//import jpos.JposException;
//import jpos.POSPrinter;
//import jpos.POSPrinterConst;
//
//public class ThermalPrinterExample {
//
//    public static void thermalPrinter() {
//        // Initialize the printer
//        // Initialize the printer
//        POSPrinter printer = new POSPrinter();
//
//        // Set the logical name of the printer (as defined in the JPOS configuration)
//        String logicalName = "ThermalPrinter";
//
//        try {
//            // Open the printer
//            printer.open(logicalName);
//            printer.claim(1000); // Timeout in milliseconds
//
//            // Enable the printer
//            printer.setDeviceEnabled(true);
//
//            // Set the print format using control characters
//            String controlCharacters = "\u001B|2C"; // Character spacing 2
//            controlCharacters += "\u001B|R1"; // Set character set to Unicode
//            String textToPrint = controlCharacters + "Hello, World!";
//
//            // Print the string
//            printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textToPrint);
//
//            // Cut the paper (if supported)
//            printer.cutPaper(100);
//
//            // Close the printer
//            printer.setDeviceEnabled(false);
//            printer.release();
//            printer.close();
//        } catch (JposException e) {
//            e.printStackTrace();
//        }
//    }
//}
