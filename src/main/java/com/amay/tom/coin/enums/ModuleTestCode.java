package com.amay.tom.coin.enums;

public enum ModuleTestCode {
    DE_JAMMING((byte) 0x11),
    COIN_SHUTTER((byte) 0x12),
    DIVERTER((byte) 0x13);

    private final byte code;

    ModuleTestCode(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static ModuleTestCode fromCode(byte code) {
        for (ModuleTestCode m : values()) {
            if (m.code == code) return m;
        }
        throw new IllegalArgumentException("Invalid ModuleTestCode: " + code);
    }
}
