package com.amay.tvm.overheadDisplay;

import com.sun.jna.*;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface OverheadDisplayDLL extends StdCallLibrary {

    OverheadDisplayDLL INSTANCE = Native.load(
            "EQ2008_Dll",
            OverheadDisplayDLL.class,
            W32APIOptions.DEFAULT_OPTIONS  // ANSI, StdCall
    );

    // ===== BASIC =====
    int User_OpenScreen();
    int User_CloseScreen();

    // ===== PROGRAM =====
    int User_AddProgram(int CardNum, int bWaitToEnd, int iPlayTime);
    int User_DelAllProgram(int CardNum);
    int User_SendToScreen(int CardNum, int ProgramIndex);

    // ===== BMP ZONE =====
    int User_AddBmpZone(int CardNum, User_Bmp pBmp, int ProgramIndex);

    // BOOL User_AddBmp(...)
    int User_AddBmp(int CardNum, int PartNum, Pointer hBitmap, User_MoveSet pMove, int ProgramIndex);

    // ===== REALTIME =====
    int User_RealtimeConnect(int CardNum);
    int User_RealtimeSendText(int CardNum, Pointer text, int length);
    int User_RealtimeSendBmpData(
            int CardNum,
            int x,
            int y,
            int iWidth,
            int iHeight,
            String strFileName
    );
    int User_RealtimeDisConnect(int CardNum);

    // ================== STRUCTS ===================

    @Structure.FieldOrder({"iX", "iY", "iWidth", "iHeight", "iFrameMode", "FrameColor"})
    class User_PartInfo extends Structure {
        public int iX;
        public int iY;
        public int iWidth;
        public int iHeight;
        public int iFrameMode;
        public int FrameColor;

        public User_PartInfo() { super(ALIGN_NONE); }
    }

    @Structure.FieldOrder({"PartInfo", "BkColor"})
    class User_Bmp extends Structure {
        public User_PartInfo PartInfo;
        public int BkColor;

        public User_Bmp() {
            super(ALIGN_NONE);
            PartInfo = new User_PartInfo();
        }
    }

    @Structure.FieldOrder({
            "iActionType", "iActionSpeed", "bClear",
            "iHoldTime", "iClearSpeed", "iClearActionType", "iFrameTime"
    })
    class User_MoveSet extends Structure {
        public int iActionType;
        public int iActionSpeed;
        public int bClear;  // BOOL = int
        public int iHoldTime;
        public int iClearSpeed;
        public int iClearActionType;
        public int iFrameTime;

        public User_MoveSet() { super(ALIGN_NONE); }
    }
}
