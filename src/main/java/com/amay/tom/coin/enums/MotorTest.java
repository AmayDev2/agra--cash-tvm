package com.amay.tom.coin.enums;

import java.util.Arrays;

public enum MotorTest {
    OPEN_ALL_MOTOR((byte) 0x00),
    FEEDER_MOTOR((byte) 0x01),
    TRANS_MOTOR((byte) 0x03),
    MOTOR_TEST_OFF((byte) 0x05)
    ;

    private final byte code;

    MotorTest(byte b) {
        this.code=b;
    }

    public byte getCode() {
        return code;
    }

    public MotorTest get(int i) {
        return Arrays.stream(MotorTest.values()).filter(x->x.code==(byte) i).findFirst().orElse(MotorTest.MOTOR_TEST_OFF);
    }
}
