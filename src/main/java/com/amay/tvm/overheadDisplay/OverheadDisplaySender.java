package com.amay.tvm.overheadDisplay;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class OverheadDisplaySender {

    private final OverheadDisplayDLL dll = OverheadDisplayDLL.INSTANCE;

    // ================= OPEN / CLOSE =================
    public boolean open(int i) {
        int r = dll.User_OpenScreen();
        return r == 1 || r == 0;   // both mean success in EQ DLL
    }

    public boolean close() {
        int r = dll.User_CloseScreen();
        return r == 1 || r == 0;
    }


    // ================= CREATE PROGRAM =================
    public int createProgram(int card) {
        // BOOL bWaitToEnd → int (1 = yes, 0 = no)
        return dll.User_AddProgram(card, 1, 10);
    }


    // ================= CREATE BMP ZONE =================
    public int createBmpZone(int card, int programIndex, int w, int h) {

        OverheadDisplayDLL.User_Bmp bmp = new OverheadDisplayDLL.User_Bmp();

        bmp.PartInfo.iX = 0;
        bmp.PartInfo.iY = 0;
        bmp.PartInfo.iWidth = w;
        bmp.PartInfo.iHeight = h;
        bmp.PartInfo.iFrameMode = 0;    // no frame
        bmp.PartInfo.FrameColor = 0;

        bmp.BkColor = 0x000000; // black background

        return dll.User_AddBmpZone(card, bmp, programIndex);
    }


    // ================= ADD BMP TO ZONE =================
    /**
     * IMPORTANT:
     * User_AddBmp() expects HBITMAP (Windows GDI handle),
     * NOT RAW BMP BYTES.
     *
     * The DLL CANNOT display raw BMP bytes.
     * You must convert file → HBITMAP.
     */
    public boolean addBmpToZone(int card, int partNum, Path bmpPath, int programIndex) throws Exception {

        // Load BMP file as HBITMAP using Windows API:
        Pointer hBitmap = loadHBitmap(bmpPath.toString());

        OverheadDisplayDLL.User_MoveSet move = new OverheadDisplayDLL.User_MoveSet();
        move.iActionType = 0;     // no animation
        move.iActionSpeed = 0;
        move.bClear = 0;          // FALSE
        move.iHoldTime = 1;
        move.iClearSpeed = 0;
        move.iClearActionType = 0;
        move.iFrameTime = 0;

        int r = dll.User_AddBmp(card, partNum, hBitmap, move, programIndex);

        return r == 0;
    }


    // ================= REALTIME TEXT =================
    public boolean User_RealtimeSendText(int card, String msg) throws UnsupportedEncodingException {
        byte[] buffer = msg.getBytes("GBK");

        Memory p = new Memory(buffer.length + 1);
        p.write(0, buffer, 0, buffer.length);
        p.setByte(buffer.length, (byte) 0);

        dll.User_RealtimeConnect(card);
        int r = dll.User_RealtimeSendText(card, p, buffer.toString().length());
//        dll.User_RealtimeDisConnect(card);

        return r ==1;
    }


    public int sendRealtimeConnect(){
        return dll.User_RealtimeConnect(1);
    }


    // ================= REALTIME BMP (RAW) =================
    public boolean sendRealtimeBmp(int card, Path bmpPath) throws Exception {
        byte[] data = Files.readAllBytes(bmpPath);

        Pointer mem = new Pointer(Native.malloc(data.length));
        mem.write(0, data, 0, data.length);

        dll.User_RealtimeConnect(card);
        int r = dll.User_RealtimeSendBmpData(1,0,0,128,16,"C:\\bmp\\BMP1.bmp");
        dll.User_RealtimeDisConnect(card);

        Native.free(Pointer.nativeValue(mem));

        return r == 1;
    }


    // ================= SEND PROGRAM =================
    public boolean sendProgram(int card, int programIndex) {
        int r = dll.User_SendToScreen(card, programIndex);
        return r == 1;
    }


    // ================= Load HBITMAP Helper =================
    // You MUST implement this correctly using GDI+ or JNA WinGDI.
    // Placeholder below.
    private Pointer loadHBitmap(String bmpPath) {

        WinNT.HANDLE hBitmap = User32.INSTANCE.LoadImage(
                null,                   // HINSTANCE
                bmpPath,                // String file path
                WinUser.IMAGE_BITMAP,   // type
                0,                      // width
                0,                      // height
                WinUser.LR_LOADFROMFILE | WinUser.LR_CREATEDIBSECTION
        );


        if (hBitmap == null || Pointer.nativeValue(hBitmap.getPointer()) == 0) {
            throw new RuntimeException("Failed to load HBITMAP from: " + bmpPath);
        }

        return hBitmap.getPointer();  // THIS is the HANDLE (pointer)
    }

}
