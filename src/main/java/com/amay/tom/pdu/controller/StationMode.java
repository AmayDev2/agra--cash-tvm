package com.amay.tom.pdu.controller;
public enum StationMode {

    EMERGENCY("⚠ EMERGENCY ⚠", "Please leave the Station Immediately", "/images/emergency.png"),
    OUT_OF_SERVICE("OUT OF SERVICE", "Please use another option", "/images/out _of_service.png"),
    STATION_CLOSED("STATION CLOSED", "Please leave the Station", "/images/out _of_service.png"),
    SHIFT_NOT_ACTIVE("COUNTER CLOSED", "Please use another window", "/images/out _of_service.png"),
    MAINTENANCE("UNDER MAINTENANCE"," Please don't use this", "/images/out _of_service.png");

    private final String label;
    private final String message;
    private final String path;

    StationMode(String label, String message, String path) {
        this.label = label;
        this.message = message;
        this.path = path;
    }

    public String getLabel() {
        return label;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
