package com.amay.tvm.eq2008;

public class DisplayService {

    private final EQ2008Library dll;

    public DisplayService() {
        this.dll = EQ2008Library.INSTANCE;
    }
    public void inti(){
        dll.User_ReloadIniFile();
        dll.User_RealtimeConnect(1);
        System.out.println("DisplayService inti");
    }

    public boolean sendRealtimeText(int card, String msg) {
        // 1. Build font info
        EQ2008Library.User_FontSet font = new EQ2008Library.User_FontSet();

        font.strFontName = "宋体";   // Chinese default font or any installed font
        font.iFontSize = 12;
        font.bFontBold = false;
        font.bFontItaic = false;
        font.bFontUnderline = false;
        font.colorFont = 0xFFFF;   // white
        font.iAlignStyle = 0;      // left
        font.iVAlignerStyle = 0;   // top
        font.iRowSpace = 0;
        font.write(); // IMPORTANT — write struct to native memory

        // 2. Connect
        boolean connected = dll.User_RealtimeConnect(card);
        if (!connected) {
            System.err.println("RealtimeConnect failed");
            return false;
        }

        // 3. Send text
        boolean ok = dll.User_RealtimeSendText(
                card,
                0, 0,         // x,y
                128, 16,       // width,height (adjust for your panel)
                msg,
                font
        );

        // 4. Disconnect
        dll.User_RealtimeDisConnect(card);

        return ok;
    }
}
