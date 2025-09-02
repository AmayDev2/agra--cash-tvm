package com.amay.tom.model.replacement;

import java.time.LocalDateTime;

public class Replacement {
    private String ticketNumber;
    private double amount;
    private String shiftId;
    private String operatorId;
    private String deviceId;
    private LocalDateTime creationDateTime;
    private LocalDateTime updateDateTime;
    private String ticketType;

    public Replacement() {
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public Replacement(String ticketNumber, double amount, String shiftId, String operatorId
            , String deviceId, LocalDateTime creationDateTime,
                  LocalDateTime updateDateTime, String ticketType) {
        this.ticketNumber = ticketNumber;
        this.amount = amount;
        this.shiftId = shiftId;
        this.operatorId = operatorId;
        this.deviceId = deviceId;
        this.creationDateTime = creationDateTime;
        this.updateDateTime = updateDateTime;
        this.ticketType=ticketType;
    }

    // Getters and Setters
    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }
} 