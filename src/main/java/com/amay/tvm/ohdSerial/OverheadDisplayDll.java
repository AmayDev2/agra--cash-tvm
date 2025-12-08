package com.amay.tvm.ohdSerial;

import com.amay.tvm.eq2008.EQ2008Models;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

public interface OverheadDisplayDll extends Library {
    OverheadDisplayDll INSTANCE = Native.load(
            "EQ2008_Dll",
            OverheadDisplayDll.class,
            W32APIOptions.UNICODE_OPTIONS
    );

    // realtime mode
    boolean User_RealtimeConnect(int cardNo);

    boolean User_RealtimeSendText(int cardNo,
                                  int x, int y,
                                  int width, int height,
                                  String text,
                                  EQ2008Models.User_FontSet font);

    boolean User_RealtimeDisConnect(int cardNo);

    boolean User_SetWorkDir(String path);
}
