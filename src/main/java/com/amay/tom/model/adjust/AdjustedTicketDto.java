package com.amay.tom.model.adjust;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AdjustedTicketDto {
    private String orderId;
    private String adjustId;
    private String adjustmentType;
    private String encryptedQR;
    private String issueTime;
    private String entryTime;
    private String exitTime;
    private String destination;
    private String ticketNumber;
    private String deviceId;
    private String operatorId;
    private String reason;
    private String area; //paid or unpaid
    private String shiftId;
    private String paymentMode;
    private String penaltyAmount;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime transactionTime;
    private boolean ccu;
    private boolean scu;
}
