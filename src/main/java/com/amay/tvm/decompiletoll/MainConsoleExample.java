package com.amay.tvm.decompiletoll;

public class MainConsoleExample {
    public static void main(String[] args) throws InterruptedException {

        EQ2008Library dll = EQ2008Library.INSTANCE;

        int card = 1; // g_iCardNum in original C#
        System.out.println("Connecting to realtime...");
        boolean connected = dll.User_RealtimeConnect(card);
        System.out.println("Connected? " + connected);
        if (!connected) {
            System.err.println("Could not connect. Check device, drivers and INI file.");
            return;
        }
        boolean cleared = dll.User_RealtimeScreenClear(card);
        System.out.println("Cleared screen: " + cleared);

        // Example: send a realtime text block
        EQ2008Library.User_FontSet font = new EQ2008Library.User_FontSet();
        font.strFontName = "Arial";
        font.iFontSize = 12;
        font.bFontBold = false;
        font.bFontItaic = false;
        font.bFontUnderline = false;
        font.colorFont = 255; // red in original mapping
        font.iAlignStyle = 1;
        font.iVAlignerStyle = 1;
        font.iRowSpace = 0;
        font.write(); // important: write values to native memory before passing

        int x = 0, y = 0, width = 128, height = 16;
        String text = "Hello from Java via JNA";

        boolean sent = dll.User_RealtimeSendText(card, x, y, width, height, text, font);
        System.out.println("Realtime send text result: " + sent);

        // Send a BMP file by path (must be accessible to the process)
        String bmpPath = "C:\\BMP1.bmp";
        boolean bmpOk = dll.User_RealtimeSendBmpData(card, 0, 0, 64, 32, bmpPath);
        System.out.println("Send bmp file result: " + bmpOk);



        // Disconnect
        boolean disc = dll.User_RealtimeDisConnect(card);
        System.out.println("Disconnected: " + disc);
    }
}
