package com.amay.tom.enums;

public enum Alarm {

    // 300- info alarms
    // 400- warning alarms
    // 500- error alarms
    // 600- critical alarms

    MAINTENANCE_LOGIN(301,"Maintenance Login"),
    MAINTENANCE_LOGOUT(302,"Maintenance Logout"),
    MAINTENANCE_LOGIN_FAILED(303,"Maintenance Login Failed"),
    MAINTENANCE_LOGOUT_FAILED(304,"Maintenance Logout Failed"),
    OPERATION_LOGIN(305,"Operation Login"),
    OPERATION_LOGOUT(306,"Operation Logout"),
    OPERATION_LOGIN_FAILED(307,"Operation Login Failed"),
    OPERATION_LOGOUT_FAILED(308,"Operation Logout Failed"),
    SHIFT_START(309,"Shift Start"),
    SHIFT_END(310,"Shift End"),
    SHIFT_END_FAILED(311,"Shift End Failed"),
    LOGIN_FAILED(311,"Login Failed"),
    SHIFT_PAUSE(312,"Shift Pause"),
    ATTEMPT_SHIFT_RESUME(313,"Attempt Shift Resume"),
    SHIFT_RESUME_FAILED(401,"Shift Resume Failed"),
    SHIFT_RESUME(315,"Shift Resume"),
    IN_SERVICE(316, "In Service mode set"),
    STATION_CLOSE(318, "Station Close mode set"),
    EMERGENCY(317, "Emergency mode set"),
    OUT_OF_SERVICE(318, "Out Of Service"),
    MAINTENANCE_MODE(319, "Maintenance Mode"),
    TEST_MODE(320, "Test Mode"),
    NO_STATION_MODE(321,"No Station Mode" ),
    IN_SERVICE_QR(320, "In Service mode set QR"),
    IN_SERVICE_CARD(321, "In Service mode set QR"),
    IN_SERVICE_QR_CARD(322, "In Service mode set QR & CARD")


    ;

    private int code;
    private String message;

    Alarm(int i, String s) {
        this.code = i;
        this.message = s;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
