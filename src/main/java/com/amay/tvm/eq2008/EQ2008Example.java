package com.amay.tvm.eq2008;

public class EQ2008Example {

    public static void main(String[] args) {

        int CARD = 1;

        // Step 1: Connect Realtime
        System.out.println("Connecting...");
        boolean ok = OverheadDisplayDLL.INSTANCE.User_RealtimeConnect(CARD);

        if(!ok) {
            System.out.println("Connect Failed");
            return;
        }

        // Step 2: Send Real-time Text
        EQ2008Models.User_FontSet font = new EQ2008Models.User_FontSet();
        font.fontName = "Arial";
        font.fontSize = 10;
        font.color    = 0xFF0000;  // red
        font.bold     = 1;

        boolean sent = OverheadDisplayDLL.INSTANCE.User_RealtimeSendText(
                CARD,
                0, 0,
                128, 32,
                "Hello EQ2008!",
                font
        );

        System.out.println("Send Text: " + sent);

        // Step 3: Disconnect
        OverheadDisplayDLL.INSTANCE.User_RealtimeDisConnect(CARD);
    }
}
