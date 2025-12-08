package com.amay.tvm.ohdSerial;


public class DisplayService {

    private final int cardNo;

    public DisplayService(int cardNo) {
        this.cardNo = cardNo;
        // DLL must know config path (INI)
        OverheadDisplayDll.INSTANCE.User_SetWorkDir("C:\\AgraCashTVM2\\EQ2008_Dll_Set.ini");
    }

    public boolean connect() {
        System.out.println("Connecting...");
        return OverheadDisplayDll.INSTANCE.User_RealtimeConnect(cardNo);
    }


    public void disconnect() {
        OverheadDisplayDll.INSTANCE.User_RealtimeDisConnect(cardNo);
    }

    public boolean sendText(String text, int x, int y, int w, int h) {

        com.amay.tvm.eq2008.EQ2008Models.User_FontSet font = new com.amay.tvm.eq2008.EQ2008Models.User_FontSet();
        font.fontName = "Arial";
        font.fontSize = 12;
        font.color = 0x00FF00; // green
        font.bold = 1;

        return OverheadDisplayDll.INSTANCE.User_RealtimeSendText(
                cardNo,
                x, y,
                w, h,
                text,
                font
        );
    }
}
