package com.amay.tom.cscCard;

public class CscConstants {
    public static final byte STX = 0x02;
    public static final byte ETX = 0x03;
    public static final byte ACK = 0x06;
    public static final byte NAK = 0x15;
    public static final byte ENQ = 0x05;

    // Command packets (from spec)
    public static final byte[] CMD_DISPENSE =
            CscCommandBuilder.buildCommand((byte) 0x44, (byte) 0x43); // 'D','C'
    public static final byte[] CMD_CAPTURE  =
            CscCommandBuilder.buildCommand((byte) 0x43, (byte) 0x50); // 'C','P'
    public static final byte[] CMD_ISSUE    =
            CscCommandBuilder.buildCommand((byte) 0x46, (byte) 0x55); // 'F','U'
    public static final byte[] CMD_RESET    =
            CscCommandBuilder.buildCommand((byte) 0x53, (byte) 0x54); // 'S','T'
    public static final byte[] CMD_REQFLAG  =
            CscCommandBuilder.buildCommand((byte) 0x52, (byte) 0x46); // 'R','F'

    private CscConstants() {
        // utility class
    }
}
