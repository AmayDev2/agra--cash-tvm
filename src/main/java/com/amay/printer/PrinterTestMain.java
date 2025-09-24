package com.amay.printer;

public class PrinterTestMain {

    public static void main(String[] arg){
        PrinterCommandDispatcher.INSTANCE.setupPrinter();
        System.out.println(PrinterCommandDispatcher.INSTANCE.getStatus());
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        System.out.println(PrinterCommandDispatcher.INSTANCE.isConnected());
    }
}
