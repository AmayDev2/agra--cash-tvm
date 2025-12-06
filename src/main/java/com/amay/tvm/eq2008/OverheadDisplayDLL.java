package com.amay.tvm.eq2008;

import com.sun.jna.*;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.platform.win32.WinDef.HBITMAP;

/**
 * EQ2008 DLL Wrapper
 * Works for 32-bit & 64-bit if matching JDK/JNA
 */
public interface OverheadDisplayDLL extends Library {

    OverheadDisplayDLL INSTANCE = Native.load(
            "EQ2008_Dll",
            OverheadDisplayDLL.class,
            W32APIOptions.UNICODE_OPTIONS
    );

    // ===========================
    // 1. PROGRAM MODE FUNCTIONS
    // ===========================

    // Add Program
    int User_AddProgram(int cardNo, boolean bWaitToEnd, int playTime);

    // Delete all programs
    boolean User_DelAllProgram(int cardNo);

    // Add BMP Zone
    int User_AddBmpZone(int cardNo, EQ2008Models.User_Bmp bmp, int programIndex);

    // Add BMP using bitmap handle
    boolean User_AddBmp(int cardNo, int partNum, HBITMAP hBitmap,
                        EQ2008Models.User_MoveSet moveSet, int programIndex);

    // Add BMP file
    boolean User_AddBmpFile(int cardNo, int partNum, String fileName,
                            EQ2008Models.User_MoveSet moveSet, int programIndex);

    // Add Multi-line text zone
    int User_AddText(int cardNo, EQ2008Models.User_Text text, int programIndex);

    // Add Single-line text
    int User_AddSingleText(int cardNo, EQ2008Models.User_SingleText text,
                           int programIndex);

    // Add Time Display
    int User_AddTime(int cardNo, EQ2008Models.User_DateTime time,
                     int programIndex);

    // Add Countdown Timer
    int User_AddTimeCount(int cardNo, EQ2008Models.User_Timer timer,
                          int programIndex);

    // Add Temperature
    int User_AddTemperature(int cardNo, EQ2008Models.User_Temperature temp,
                            int programIndex);

    // Send Program to Display
    boolean User_SendToScreen(int cardNo);


    // ===========================
    // 2. REAL-TIME MODE
    // ===========================

    boolean User_RealtimeConnect(int cardNo);

    boolean User_RealtimeSendData(int cardNo, int x, int y, int width,
                                  int height, HBITMAP hBitmap);

    boolean User_RealtimeSendBmpData(int cardNo, int x, int y, int width,
                                     int height, String bmpFile);

    boolean User_RealtimeSendText(int cardNo, int x, int y, int width,
                                  int height, String text,
                                  EQ2008Models.User_FontSet font);

    boolean User_RealtimeDisConnect(int cardNo);

    boolean User_RealtimeScreenClear(int cardNo);


    // ===========================
    // 3. SCREEN CONTROL
    // ===========================

    boolean User_OpenScreen(int cardNo);

    boolean User_CloseScreen(int cardNo);

    boolean User_AdjustTime(int cardNo);

    boolean User_SetScreenLight(int cardNo, int brightness);

    boolean User_SetWorkDir(String path);

    void User_ReloadIniFile(String dllConfigDir);
}
