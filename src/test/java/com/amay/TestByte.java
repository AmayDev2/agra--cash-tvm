package com.amay;

import com.amay.tom.coin.constants.ProtocolConstants;
import com.amay.tom.coin.util.HexUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TestByte {

    public byte[] parseFrame(byte[] input) {

        boolean inFrame = false;
        int frameStart = -1;
        boolean escapeNext = false;

        for (int i = 0; i < input.length; i++) {
            byte b = input[i];

            if (!inFrame) {
                if (b == ProtocolConstants.STX) {
                    inFrame = true;
                    escapeNext = false;
                    frameStart = i;
                }
                continue;
            }

            if (escapeNext) {
                escapeNext = false;
                continue;
            }

            if (b == ProtocolConstants.DLE) {
                escapeNext = true;
                continue;
            }

            if (b == ProtocolConstants.ETX) {
                return Arrays.copyOfRange(input, frameStart, i + 1);
            }
        }

        return null; // no frame found
    }

    @Test
    public void test(){
//        byte[] test = new byte[] {
//                (byte)0x55, (byte)0xAA,      // noise
//                ProtocolConstants.STX,
//                0x41, 0x42,                  // data
//                ProtocolConstants.DLE, 0x03, // escaped ETX
//                ProtocolConstants.ETX,(byte)0x55        // real ETX
//        };
        byte[] test = new byte[] {
                (byte) 0x55, (byte) 0xAA,     // noise
                0x00, 0x00, 0x00, 0x00,       // many zeros before STX
                ProtocolConstants.STX,        // STX

                0x00, 0x00,                   // noise inside frame

                0x41, 0x42,                   // data

                0x00, 0x00, 0x00,             // zeros inside frame

                ProtocolConstants.DLE, 0x03,  // escaped ETX

                0x00, 0x00,                   // noise

                ProtocolConstants.ETX,        // real ETX

                0x00, 0x00, 0x00, 0x00,       // noise after ETX
                (byte) 0x55
        };


        byte[] result = parseFrame(test);

        System.out.println(HexUtil.toHex(result));

    }

}
