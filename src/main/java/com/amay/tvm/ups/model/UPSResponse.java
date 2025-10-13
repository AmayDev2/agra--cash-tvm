package com.amay.tvm.ups.model;

import com.amay.tvm.ups.model.UPSStatus;

/**
 * UPS Response Object representing the parsed status data
 * Based on AGRA METRO PROJECT UPS Module Protocol Specification
 */
public class UPSResponse {
    private double inputVoltage;        // MMM.M
    private double inputFaultVoltage;   // NNN.N
    private double outputVoltage;       // PPP.P
    private int outputCurrent;          // QQQ (percentage)
    private double inputFrequency;      // RR.R
    private double batteryVoltage;      // SS.S or S.SS
    private double temperature;         // TT.T
    private UPSStatus status;
    private long timestamp;
    private String rawResponse;

    public UPSResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public double getInputVoltage() {
        return inputVoltage;
    }

    public void setInputVoltage(double inputVoltage) {
        this.inputVoltage = inputVoltage;
    }

    public double getInputFaultVoltage() {
        return inputFaultVoltage;
    }

    public void setInputFaultVoltage(double inputFaultVoltage) {
        this.inputFaultVoltage = inputFaultVoltage;
    }

    public double getOutputVoltage() {
        return outputVoltage;
    }

    public void setOutputVoltage(double outputVoltage) {
        this.outputVoltage = outputVoltage;
    }

    public int getOutputCurrent() {
        return outputCurrent;
    }

    public void setOutputCurrent(int outputCurrent) {
        this.outputCurrent = outputCurrent;
    }

    public double getInputFrequency() {
        return inputFrequency;
    }

    public void setInputFrequency(double inputFrequency) {
        this.inputFrequency = inputFrequency;
    }

    public double getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public UPSStatus getStatus() {
        return status;
    }

    public void setStatus(UPSStatus status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    @Override
    public String toString() {
        return "UPSResponse{" +
                "inputVoltage=" + inputVoltage +
                ", inputFaultVoltage=" + inputFaultVoltage +
                ", outputVoltage=" + outputVoltage +
                ", outputCurrent=" + outputCurrent + "%" +
                ", inputFrequency=" + inputFrequency +
                ", batteryVoltage=" + batteryVoltage +
                ", temperature=" + temperature +
                ", status=" + status +
                ", timestamp=" + timestamp +
                '}';
    }
}
