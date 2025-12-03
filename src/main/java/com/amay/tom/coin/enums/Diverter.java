package com.amay.tom.coin.enums;

public enum Diverter {
    NO_ACTION((byte) 0x00),
    RETURN_TRAY((byte) 0x01),
    COLLECTION_BOX((byte) 0x02),
    D3((byte) 0x03),
    D4((byte) 0x04),
    D5((byte) 0x05)
    ;

    private final byte code;

    Diverter(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
