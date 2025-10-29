package com.amay.tvm.backend.enums;

public enum CoinDenomination {
    COIN_5(5),
    COIN_10(10);

    private final int value;

    CoinDenomination(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // Optional: Get enum constant by numeric value
    public static CoinDenomination fromValue(int value) {
        for (CoinDenomination denomination : values()) {
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
