package com.amay.tom.coin.model;

public class CollectionBoxIdSetResponse extends CollectionBoxIdResponse {
    public CollectionBoxIdSetResponse(byte command, byte sequenceNumber, byte[] data) {
        super(command, sequenceNumber, data);
    }

    public boolean isAck() {
        byte[] d = getData();
        return d.length > 0 && d[0] == 0x00; // assume 0x00 means OK
    }
}
