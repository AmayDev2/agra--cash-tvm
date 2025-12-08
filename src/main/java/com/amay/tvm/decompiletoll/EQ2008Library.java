package com.amay.tvm.decompiletoll;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * JNA mapping for EQ2008_Dll.dll (64-bit)
 */
public interface EQ2008Library extends StdCallLibrary {
    // Load the library. Make sure PATH contains the DLL or set -Djava.library.path
    EQ2008Library INSTANCE = Native.load("EQ2008_Dll.dll", EQ2008Library.class, W32APIOptions.DEFAULT_OPTIONS);

    // --------------------
    // Structures (corresponding to the C# structs)
    // --------------------
    class User_PartInfo extends Structure {
        public static class ByValue extends User_PartInfo implements Structure.ByValue {}
        public int iX;
        public int iY;
        public int iWidth;
        public int iHeight;
        public int iFrameMode;
        public int FrameColor;

        @Override
        protected java.util.List<String> getFieldOrder() {
            return java.util.Arrays.asList("iX","iY","iWidth","iHeight","iFrameMode","FrameColor");
        }
    }

    class User_FontSet extends Structure {
        public static class ByReference extends User_FontSet implements Structure.ByReference {}
        public String strFontName; // ANSI string
        public int iFontSize;
        public boolean bFontBold;
        public boolean bFontItaic;
        public boolean bFontUnderline;
        public int colorFont;
        public int iAlignStyle;
        public int iVAlignerStyle;
        public int iRowSpace;

        public User_FontSet() { super(); }
        public User_FontSet(Pointer p) { super(p); read(); }

        @Override
        protected java.util.List<String> getFieldOrder() {
            return java.util.Arrays.asList(
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

    class User_MoveSet extends Structure {
        public static class ByValue extends User_MoveSet implements Structure.ByValue {}
        public int iActionType;
        public int iActionSpeed;
        public boolean bClear;
        public int iHoldTime;
        public int iClearSpeed;
        public int iClearActionType;
        public int iFrameTime;

        @Override
        protected java.util.List<String> getFieldOrder() {
            return java.util.Arrays.asList(
                    "iActionType","iActionSpeed","bClear","iHoldTime","iClearSpeed","iClearActionType","iFrameTime"
            );
        }
    }

    class User_Text extends Structure {
        public static class ByReference extends User_Text implements Structure.ByReference {}
        public String chContent;
        public User_PartInfo PartInfo;
        public int BkColor;
        public User_FontSet FontInfo;
        public User_MoveSet MoveSet;

        public User_Text() { super(); }
        public User_Text(Pointer p) { super(p); read(); }

        @Override
        protected java.util.List<String> getFieldOrder() {
            return java.util.Arrays.asList("chContent","PartInfo","BkColor","FontInfo","MoveSet");
        }
    }

    class User_SingleText extends Structure {
        public static class ByReference extends User_SingleText implements Structure.ByReference {}
        public String chContent;
        public User_PartInfo PartInfo;
        public int BkColor;
        public User_FontSet FontInfo;
        public User_MoveSet MoveSet;

        @Override
        protected java.util.List<String> getFieldOrder() {
            return java.util.Arrays.asList("chContent","PartInfo","BkColor","FontInfo","MoveSet");
        }
    }

    class User_DateTime extends Structure {
        public User_PartInfo PartInfo;
        public int BkColor;
        public User_FontSet FontInfo;
        public int iDisplayType;
        public String chTitle;
        public boolean bYearDisType;
        public boolean bMulOrSingleLine;
        public boolean bYear;
        public boolean bMouth;
        public boolean bDay;
        public boolean bWeek;
        public boolean bHour;
        public boolean bMin;
        public boolean bSec;

        @Override
        protected java.util.List<String> getFieldOrder() {
            return java.util.Arrays.asList(
                    "PartInfo","BkColor","FontInfo","iDisplayType","chTitle",
                    "bYearDisType","bMulOrSingleLine","bYear","bMouth","bDay","bWeek",
                    "bHour","bMin","bSec"
            );
        }
    }

    // --------------------
    // Function mappings (as in C# DllImport)
    // --------------------

    boolean User_RealtimeConnect(int CardNum);
    boolean User_RealtimeDisConnect(int CardNum);
    boolean User_RealtimeScreenClear(int CardNum);

    boolean User_RealtimeSendBmpData(int CardNum, int x, int y, int iWidth, int iHeight, String strFileName);
    boolean User_RealtimeSendData(int CardNum, int x, int y, int iWidth, int iHeight, Pointer hBitmap);

    boolean User_RealtimeSendText(int CardNum, int x, int y, int iWidth, int iHeight, String strText, User_FontSet pFontInfo);

    boolean User_SendToScreen(int CardNum);
    boolean User_OpenScreen(int CardNum);
    boolean User_CloseScreen(int CardNum);
    boolean User_AdjustTime(int CardNum);
    boolean User_DelAllProgram(int CardNum);
    int User_AddProgram(int CardNum, boolean bWaitToEnd, int iPlayTime);
    int User_AddText(int CardNum, User_Text pText, int iProgramIndex);
    int User_AddSingleText(int CardNum, User_SingleText pSingleText, int iProgramIndex);
    int User_AddBmpZone(int CardNum, User_Bmp pBmp, int iProgramIndex);
    boolean User_AddBmpFile(int CardNum, int iBmpPartNum, String strFileName, User_MoveSet pMoveSet, int iProgramIndex);
    boolean User_AddBmp(int CardNum, int iBmpPartNum, Pointer hBitmap, User_MoveSet pMoveSet, int iProgramIndex);

    void User_ReloadIniFile(String strEQ2008_Dll_Set_Path);
    boolean User_SetScreenLight(int CardNum, int iLightDegreen);

    // Simple wrapper struct for bitmap zone used earlier
    class User_Bmp extends Structure {
        public User_PartInfo PartInfo;
        @Override
        protected java.util.List<String> getFieldOrder() {
            return java.util.Arrays.asList("PartInfo");
        }
    }
}

