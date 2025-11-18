package com.amay.tom.coin.enums;

import java.util.Arrays;

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

    public Range get(int i) {
        return Arrays.stream(Range.values()).filter(x->x.code==(byte) i).findFirst().orElse(Range.ONE);
    }
}
