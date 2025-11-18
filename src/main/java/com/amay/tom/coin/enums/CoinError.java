package com.amay.tom.coin.enums;

public enum CoinError {
    COIN_SHUTTER_OPEN_FAILURE((byte) 1),
    COIN_SHUTTER_CLOSE_FAILURE((byte) 2),
    COIN_DETECTION_PATH_JAMMED((byte) 3),
    COIN_ENTRY_CHUTE_JAMMED((byte) 4),
    COIN_JAMMED_DUMPING((byte) 7),
    DE_JAMMING_MOTOR_FAILURE((byte) 28),
    ESCROW_TO_COLLECTION_BOX_FAILURE((byte) 34),
    ESCROW_TO_RETURN_TRAY_FAILURE((byte) 36),
    DIVERTER_TO_RETURN_TRAY_FAILURE((byte) 37),
    DIVERTER_TO_COLLECTION_BOX_FAILURE((byte) 38),
    HOPPER1_NOT_IN_POSITION((byte) 60),
    HOPPER1_EMPTY((byte) 61),
    HOPPER1_FAILURE((byte) 62),
    HOPPER2_NOT_IN_POSITION((byte) 70),
    HOPPER2_EMPTY((byte) 71),
    HOPPER2_FAILURE((byte) 72),
    HOPPER3_NOT_IN_POSITION((byte) 80),
    HOPPER3_EMPTY((byte) 81),
    HOPPER3_FAILURE((byte) 82);

    private final byte code;
    CoinError(byte code) { this.code = code; }
    public byte getCode() { return code; }

    public static CoinError fromCode(byte code) {
        for (CoinError e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}
