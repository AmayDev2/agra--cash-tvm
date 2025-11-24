package com.amay.tom.cscCard;

public class CscCommandBuilder {
    public static byte[] buildCommand(byte code1, byte code2) {
        byte[] pkt = new byte[5];
        pkt[0] = CscConstants.STX;
        pkt[1] = code1;
        pkt[2] = code2;
        pkt[3] = CscConstants.ETX;
        pkt[4] = bcc(pkt, 4);
        return pkt;
    }

    private static byte bcc(byte[] data, int len) {
        byte b = 0x00;
        for (int i = 0; i < len; i++) {
            b ^= data[i];
        }
        return b;
    }

    private CscCommandBuilder() {
        // utility class
    }
}
