package com.amay.tom.coin.enums;

public enum Escrow {
    NO_ACTION((byte) 0x00),
    COIN_RETURN((byte) 0x01),
    COIN_COLLECTION((byte) 0x02),
    E3((byte) 0x03),
    E4((byte) 0x04),
    E5((byte) 0x05)
    ;

    private final byte code;

    Escrow(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
