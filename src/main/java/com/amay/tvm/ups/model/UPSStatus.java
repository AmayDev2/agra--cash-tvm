package com.amay.tvm.ups.model;

/**
 * UPS Status flags parsed from the 8-bit status byte
 */
public class UPSStatus {
    private boolean utilityFail;        // Bit 7
    private boolean batteryLow;         // Bit 6
    private boolean bypassBoostActive;  // Bit 5
    private boolean upsFailed;          // Bit 4
    private boolean standbyType;        // Bit 3 (1=Standby, 0=Online)
    private boolean testInProgress;     // Bit 2
    private boolean shutdownActive;     // Bit 1
    private boolean beeperOn;           // Bit 0

    private String rawStatusByte;       // Original 8-bit string

    public UPSStatus() {}

    public UPSStatus(String statusByte) {
        this.rawStatusByte = statusByte;
        parseStatusByte(statusByte);
    }

    private void parseStatusByte(String statusByte) {
        if (statusByte == null || statusByte.length() != 8) {
            throw new IllegalArgumentException("Status byte must be 8 characters");
        }

        this.utilityFail = statusByte.charAt(0) == '1';
        this.batteryLow = statusByte.charAt(1) == '1';
        this.bypassBoostActive = statusByte.charAt(2) == '1';
        this.upsFailed = statusByte.charAt(3) == '1';
        this.standbyType = statusByte.charAt(4) == '1';
        this.testInProgress = statusByte.charAt(5) == '1';
        this.shutdownActive = statusByte.charAt(6) == '1';
        this.beeperOn = statusByte.charAt(7) == '1';
    }

    // Getters and Setters
    public boolean isUtilityFail() {
        return utilityFail;
    }

    public void setUtilityFail(boolean utilityFail) {
        this.utilityFail = utilityFail;
    }

    public boolean isBatteryLow() {
        return batteryLow;
    }

    public void setBatteryLow(boolean batteryLow) {
        this.batteryLow = batteryLow;
    }

    public boolean isBypassBoostActive() {
        return bypassBoostActive;
    }

    public void setBypassBoostActive(boolean bypassBoostActive) {
        this.bypassBoostActive = bypassBoostActive;
    }

    public boolean isUpsFailed() {
        return upsFailed;
    }

    public void setUpsFailed(boolean upsFailed) {
        this.upsFailed = upsFailed;
    }

    public boolean isStandbyType() {
        return standbyType;
    }

    public void setStandbyType(boolean standbyType) {
        this.standbyType = standbyType;
    }

    public boolean isTestInProgress() {
        return testInProgress;
    }

    public void setTestInProgress(boolean testInProgress) {
        this.testInProgress = testInProgress;
    }

    public boolean isShutdownActive() {
        return shutdownActive;
    }

    public void setShutdownActive(boolean shutdownActive) {
        this.shutdownActive = shutdownActive;
    }

    public boolean isBeeperOn() {
        return beeperOn;
    }

    public void setBeeperOn(boolean beeperOn) {
        this.beeperOn = beeperOn;
    }

    public String getRawStatusByte() {
        return rawStatusByte;
    }

    @Override
    public String toString() {
        return "UPSStatus{" +
                "utilityFail=" + utilityFail +
                ", batteryLow=" + batteryLow +
                ", bypassBoostActive=" + bypassBoostActive +
                ", upsFailed=" + upsFailed +
                ", standbyType=" + (standbyType ? "Standby" : "Online") +
                ", testInProgress=" + testInProgress +
                ", shutdownActive=" + shutdownActive +
                ", beeperOn=" + beeperOn +
                '}';
    }
}
