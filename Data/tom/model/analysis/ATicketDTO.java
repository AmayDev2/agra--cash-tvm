package com.amay.tom.model.analysis;

// ATicketDTO.java
import lombok.Data;

@Data
public class ATicketDTO {
    private String ticketNumber;
    private String orderId;
    private String transactionId;
    private String transactionUk;
    private String ticketIssue;
    private String ticketExp;

    private String sourceStation;
    private String destinationStation;

    private String language;
    private double amount;
    private double discount;
    private boolean isActive;
    private String ticketType;
    private int quantity;

    private String status;
    private String paymentMode;
    private String qrData;
    private boolean isAdjusted;
}
