package com.amay.tom.model.refund;

import java.time.LocalDateTime;

public class RefundDTO {
    private String ticketNumber;
    private double amount;
    private String shiftId;
    private String operatorId;
    private String refundMode;
    private String deviceId;
    private LocalDateTime creationDateTime;
    private LocalDateTime updateDateTime;

    public RefundDTO() {
    }

    public RefundDTO(String ticketNumber, double amount, String shiftId, String operatorId, 
                    String refundMode, String deviceId, LocalDateTime creationDateTime, 
                    LocalDateTime updateDateTime) {
        this.ticketNumber = ticketNumber;
        this.amount = amount;
        this.shiftId = shiftId;
        this.operatorId = operatorId;
        this.refundMode = refundMode;
        this.deviceId = deviceId;
        this.creationDateTime = creationDateTime;
        this.updateDateTime = updateDateTime;
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

    public String getRefundMode() {
        return refundMode;
    }

    public void setRefundMode(String refundMode) {
        this.refundMode = refundMode;
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