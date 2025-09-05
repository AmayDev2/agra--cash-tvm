package com.amay.tom.model.adjust;

import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.service.qrService2.TicketInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AdjustedTicket implements GeneratedTicket {
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
    private String penaltyAmount;
    private String paymentMode;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime transactionTime;
    private TicketInfo ticketInfo;

}
