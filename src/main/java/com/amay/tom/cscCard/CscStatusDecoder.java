package com.amay.tom.cscCard;

public class CscStatusDecoder {
    public static String decodeStatus(byte d1, byte d2, byte d3) {
        if (d1 == 0x38 && d2 == 0x30 && d3 == 0x30) return "Issuing ticket in progress";
        if (d1 == 0x34 && d2 == 0x30 && d3 == 0x30) return "Capturing ticket in progress";
        if (d1 == 0x32 && d2 == 0x30 && d3 == 0x30) return "Module error";
        if (d1 == 0x31 && d2 == 0x30 && d3 == 0x30) return "Reject bin full";
        if (d1 == 0x30 && d2 == 0x32 && d3 == 0x30) return "Issue ticket jam";
        if (d1 == 0x30 && d2 == 0x31 && d3 == 0x30) return "Magazine low";
        if (d1 == 0x30 && d2 == 0x30 && d3 == 0x38) return "Magazine empty";
        if (d1 == 0x30 && d2 == 0x30 && d3 == 0x34) return "Ticket at exit bezel, pending removal";
        if (d1 == 0x30 && d2 == 0x30 && d3 == 0x32) return "Ticket at CSC-RW position";
        if (d1 == 0x30 && d2 == 0x30 && d3 == 0x31) return "Pre-issue ticket not in position";
        if (d1 == 0x30 && d2 == 0x30 && d3 == 0x30) return "Module ready";

        return String.format("Unknown status (%02X %02X %02X)", d1, d2, d3);
    }

    private CscStatusDecoder() {
        // utility class
    }
}
