package com.amay.tvm.overheadDisplay;

import java.nio.file.Path;

public class OverHeadMain {
    public static void main(String[] args) throws Exception {

        int cardNum = 1; // MUST match INI file

        OverheadDisplaySender sender = new OverheadDisplaySender();
        System.out.println(sender.sendRealtimeConnect());

//        System.out.println("Opening display...");
//        sender.open(1);

        System.out.println("Creating program...");
        int programIndex = sender.createProgram(cardNum);

        System.out.println("Creating BMP zone...");
        int zone = sender.createBmpZone(cardNum, programIndex, 128, 64);

        System.out.println("Adding BMP...");
        sender.addBmpToZone(cardNum, zone, Path.of("C:\\bmp\\BMP1.bmp"), programIndex);

        System.out.println("Sending program to display...");
        sender.sendProgram(cardNum, programIndex);

        Thread.sleep(2000);

        System.out.println("Sending realtime text...");
        sender.User_RealtimeSendText(cardNum, "HELLO WORLD!");

        System.out.println("Sending realtime BMP...");
        sender.sendRealtimeBmp(cardNum, Path.of("C:\\bmp\\BMP1.bmp"));

        System.out.println("Closing display...");
        sender.close();
    }
}
