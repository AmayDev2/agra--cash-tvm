package com.amay.tvm.eq2008;

import com.sun.jna.*;
import com.sun.jna.win32.StdCallLibrary;

import java.util.Arrays;
import java.util.List;

public interface EQ2008Library extends StdCallLibrary {

    EQ2008Library INSTANCE = Native.load(
            "EQ2008_Dll",
            EQ2008Library.class
    );


    /** Connect for realtime operation */
    boolean User_RealtimeConnect(int card);

    int User_ReloadIniFile();

    /** Clear screen (real-time mode) */
    boolean User_RealtimeScreenClear(int card);


    /** Send text in real-time mode */
    boolean User_RealtimeSendText(
            int card,
            int x,
            int y,
            int width,
            int height,
            String text,
            User_FontSet font
    );

    /** Send BMP file in real-time mode */
    boolean User_RealtimeSendBmpData(
            int card,
            int x,
            int y,
            int width,
            int height,
            String filePath
    );

    /** Send raw bitmap handle (32-bit HBITMAP) */
    boolean User_RealtimeSendData(
            int card,
            int x,
            int y,
            int width,
            int height,
            Pointer hBitmap
    );

    /** Disconnect realtime session */
    boolean User_RealtimeDisConnect(int card);



    // =========================================================================
    // ========================= STRUCTS ======================================
    // =========================================================================

    @Structure.FieldOrder({
            "strFontName",
            "iFontSize",
            "bFontBold",
            "bFontItaic",
            "bFontUnderline",
            "colorFont",
            "iAlignStyle",
            "iVAlignerStyle",
            "iRowSpace"
    })
    class User_FontSet extends Structure {

        public static class ByReference extends User_FontSet implements Structure.ByReference {}
        public static class ByValue extends User_FontSet implements Structure.ByValue {}

        /** Font name (ANSI/GBK inside DLL) */
        public String strFontName;
        public int iFontSize;
        public boolean bFontBold;
        public boolean bFontItaic;
        public boolean bFontUnderline;
        public int colorFont;
        public int iAlignStyle;
        public int iVAlignerStyle;
        public int iRowSpace;

        public User_FontSet() {}

        public User_FontSet(Pointer p) {
            super(p);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(
                    "strFontName",
                    "iFontSize",
                    "bFontBold",
                    "bFontItaic",
                    "bFontUnderline",
                    "colorFont",
                    "iAlignStyle",
                    "iVAlignerStyle",
                    "iRowSpace"
            );
        }
    }
}
