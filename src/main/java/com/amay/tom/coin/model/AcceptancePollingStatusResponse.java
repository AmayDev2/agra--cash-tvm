package com.amay.tom.coin.model;


import com.amay.tom.coin.enums.CoinError;
import lombok.ToString;


public class AcceptancePollingStatusResponse extends ModuleResponse {
    public AcceptancePollingStatusResponse(byte command, byte sequenceNumber, byte[] data) {
        super(command, sequenceNumber, data);
    }

    public boolean isAck() {
        byte[] d = getData();
        return d.length > 0 && d[0] == 0x00; // assume 0x00 means OK
    }

    public  CoinPollResponse parse() {
        byte[] data = getData();

        int rs20 = data[1] & 0xFF;
        int rs10 = data[2] & 0xFF;
        int rs5  = data[3] & 0xFF;
        int rs2  = data[4] & 0xFF;
        int rs1  = data[5] & 0xFF;

        byte flags = data[6];

        boolean shutterOpen   = (flags & 0b1000_0000) != 0;
        boolean clearRegister = (flags & 0b0100_0000) != 0;
        boolean inhibitRs20   = (flags & 0b0001_0000) != 0;
        boolean inhibitRs10   = (flags & 0b0000_1000) != 0;
        boolean inhibitRs5    = (flags & 0b0000_0100) != 0;
        boolean inhibitRs2    = (flags & 0b0000_0010) != 0;
        boolean inhibitRs1    = (flags & 0b0000_0001) != 0;

        return new CoinPollResponse(
                rs20, rs10, rs5, rs2, rs1,
                shutterOpen, clearRegister,
                inhibitRs20, inhibitRs10, inhibitRs5, inhibitRs2, inhibitRs1
        );
    }

    public CoinError getStatus(){
        return CoinError.fromCode(getData()[7]);
    }





/*    Data item 	Value	Length	Description
    STX	Hex	1 byte	0x02
    Length Hex	1 byte	0x0C
    Command	Hex	1 byte	0x05
    Number	Hex	1 byte	The same as received sequence number
    COM State	Hex	1 byte	Response status:
            0x00: The command is successful
0x01: The command is failed
0x02: The module is busy
0x04: Data error
    Data0	Hex	1 byte	Reserved
    Data1	Hex	1 byte	Number of Rs20 accepted
    Data2	Hex	1 byte	Number of Rs10 accepted
    Data3	Hex	1 byte	Number of Rs5 accepted
    Data4	Hex	1 byte	Number of Rs2 accepted
    Data5	Hex	1 byte	Number of Rs1 accepted
    Data6	Hex	1 byte	Bit7: 1 – Open Coin Shutter
     0 - Close Coin Shutter
    Bit6: 1 – Clear the coin register
     0 - NOT Clear the coin register
    Bit5: Reserved
    Bit4: Rs 20
            1 - inhibit, 0 - activate
    Bit3: Rs 10
            1 - inhibit, 0 - activate
    Bit2: Rs 5
            1 - inhibit, 0 - activate
    Bit1: Rs 2
            1 - inhibit, 0 - activate
    Bit0: Rs 1
            1 - inhibit, 0 - activate
    Data7	Hex	1 byte	Error Code, refer to Chapter 5
    Data8	Hex	1 byte	Reserved
    Check	Hex	1 byte	BCC check
    ETX	Hex	1 byte	0x03*/

}
