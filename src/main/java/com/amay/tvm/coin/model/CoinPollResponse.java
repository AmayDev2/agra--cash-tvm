package com.amay.tvm.coin.model;

import lombok.ToString;


public record CoinPollResponse(
        int rs20Count,   // Data1
        int rs10Count,   // Data2
        int rs5Count,    // Data3
        int rs2Count,    // Data4
        int rs1Count,    // Data5

        boolean shutterOpen,       // Data6 Bit7
        boolean clearRegister,     // Data6 Bit6
        boolean inhibitRs20,       // Data6 Bit4
        boolean inhibitRs10,       // Data6 Bit3
        boolean inhibitRs5,        // Data6 Bit2
        boolean inhibitRs2,        // Data6 Bit1
        boolean inhibitRs1        // Data6 Bit0
) {


    public byte getStatusByte() {
        byte value = 0;

        if (shutterOpen)   value |= (byte) (1 << 7);
        if (clearRegister) value |= (1 << 6);
        // bit 5 is reserved, always 0.
        if (inhibitRs20)   value |= (1 << 4);
        if (inhibitRs10)   value |= (1 << 3);
        if (inhibitRs5)    value |= (1 << 2);
        if (inhibitRs2)    value |= (1 << 1);

        return value;
    }


}
