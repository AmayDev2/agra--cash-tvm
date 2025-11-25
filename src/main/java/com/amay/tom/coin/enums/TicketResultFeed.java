package com.amay.tom.coin.enums;

import java.util.Arrays;

public enum TicketResultFeed {
    AUTO((byte) 0x01),
    MANUAL((byte) 0x11);

    private final byte code;

    TicketResultFeed(byte b) {
        this.code=b;
    }

    public byte getCode() {
        return code;
    }

    public TicketResultFeed get(int i) {
        return Arrays.stream(TicketResultFeed.values()).filter(x->x.code==(byte) i).findFirst().orElse(TicketResultFeed.AUTO);
    }
}
