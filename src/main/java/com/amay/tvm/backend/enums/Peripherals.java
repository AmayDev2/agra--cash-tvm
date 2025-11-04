package com.amay.tvm.backend.enums;

public enum Peripherals {
    PRINTER(1),
    SCU(2),
    CCU(3),
    READER(4),
    PDU(5),
    BNR(7),
    OHD(8),
    UPS(9),
    UPS_ON(10),
    UPOS(11);

    private final int index;

    Peripherals(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
