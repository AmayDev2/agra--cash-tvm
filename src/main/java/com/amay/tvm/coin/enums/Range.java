package com.amay.tvm.coin.enums;

public enum Range {
    ONE((byte) 0x01),
    TWO((byte) 0x02),
    THREE((byte) 0x03);

    private final byte code;

    Range(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
