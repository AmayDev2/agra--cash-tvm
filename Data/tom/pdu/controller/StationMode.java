package com.amay.tom.pdu.controller;
public enum StationMode {

    EMERGENCY("⚠ EMERGENCY MODE ACTIVATED ⚠", "Please leave the Station Immediately", "src/main/resources/images/emergency.png"),
    OUT_OF_SERVICE("OUT OF SERVICE", "", "src/main/resources/images/out _of_service.png"),
    STATION_CLOSED("STATION CLOSED", "Please leave the Station", "src/main/resources/images/out _of_service.png");

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
