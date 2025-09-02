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
    private String ticketType;
    private String refundId;
    private String status;

    // ✅ New fields
    private boolean ccu = false;
    private boolean scu = false;

    public RefundDTO() {
    }

    // All-args constructor including new fields
    public RefundDTO(String ticketNumber, double amount, String shiftId, String operatorId,
                     String refundMode, String deviceId, LocalDateTime creationDateTime,
                     LocalDateTime updateDateTime, String ticketType, String refundId,
                     String status) {
        this.ticketNumber = ticketNumber;
        this.amount = amount;
        this.shiftId = shiftId;
        this.operatorId = operatorId;
        this.refundMode = refundMode;
        this.deviceId = deviceId;
        this.creationDateTime = creationDateTime;
        this.updateDateTime = updateDateTime;
        this.ticketType = ticketType;
        this.refundId = refundId;
        this.status = status;
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

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ✅ New getters and setters
    public boolean isCcu() {
        return ccu;
    }

    public void setCcu(boolean ccu) {
        this.ccu = ccu;
    }

    public boolean isScu() {
        return scu;
    }

    public void setScu(boolean scu) {
        this.scu = scu;
    }
}
