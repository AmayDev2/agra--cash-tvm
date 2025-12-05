package com.amay.tvm.eq2008;

import com.amay.tvm.eq2008.DisplayService;

public class Test {
    public static void main(String[] args) {
        DisplayService display = new DisplayService();

        EQ2008Library dll = EQ2008Library.INSTANCE;


        dll.User_RealtimeConnect(1);

        dll.User_RealtimeSendBmpData(1, 0, 0, 128, 16, "C:\\bmp\\BMP1.bmp");
        EQ2008Library.User_FontSet font = new EQ2008Library.User_FontSet();

// FONT PROPERTIES
        font.strFontName = "Arial";     // OR "宋体"
        font.iFontSize = 18;
        font.bFontBold = true;          // bold text
        font.bFontItaic = false;
        font.bFontUnderline = false;

// RED COLOR
        font.colorFont = 0xF800;        // RED (RGB 565)

// TEXT ALIGN
        font.iAlignStyle = 0;           // left
        font.iVAlignerStyle = 0;        // top
        font.iRowSpace = 0;

// WRITE memory to native before calling DLL
        font.write();
        dll.User_RealtimeSendText(1, 0, 0, 128, 16, "HelloEQ2008", font);
        dll.User_RealtimeDisConnect(1);

        boolean ok = display.sendRealtimeText(1, "Hello EQ2008!");
//        System.out.println("Status: " + ok);
    }
}
