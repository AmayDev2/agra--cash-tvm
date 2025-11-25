package com.amay.tom.coin.enums;

import java.util.Arrays;

public enum TicketResultIssue {
    TO_PASSENGER((byte) 0x01),
    TO_REJECT((byte) 0x02);

    private final byte code;

    TicketResultIssue(byte b) {
        this.code=b;
    }

    public byte getCode() {
        return code;
    }

    public TicketResultIssue get(int i) {
        return Arrays.stream(TicketResultIssue.values()).filter(x->x.code==(byte) i).findFirst().orElse(TicketResultIssue.TO_PASSENGER);
    }
}
