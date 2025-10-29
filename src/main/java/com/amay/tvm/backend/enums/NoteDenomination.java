package com.amay.tvm.backend.enums;

public enum NoteDenomination {
    NOTE_10(10),
    NOTE_20(20),
    NOTE_50(50),
    NOTE_100(100),
    NOTE_200(200),
    NOTE_500(500);

    private final int value;

    NoteDenomination(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // Optional: Get enum constant by numeric value
    public static NoteDenomination fromValue(int value) {
        for (NoteDenomination denomination : values()) {
            if (denomination.getValue() == value) {
                return denomination;
            }
        }
        throw new IllegalArgumentException("Invalid denomination value: " + value);
    }

    @Override
    public String toString() {
        return "₹" + value;
    }
}
