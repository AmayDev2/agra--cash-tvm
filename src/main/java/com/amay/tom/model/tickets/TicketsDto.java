package com.amay.tom.model.tickets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class TicketsDto {

    private String orderId;

    private String ticketId;
    private long IssueAt;
    private long entryTime;
    private long ValidUntil;
    private String inStation;
    private String outStation;
    private String language;
    private String ticketType;
    private String qrData;

    private String operatorId;
    private String shiftId;

    private String deviceId;
    private String deviceType;
    private String deviceSerial;

    private String lineId;
    private String stationId;

    private double amount;
    private double discount;
    private String paymentMode;

    private boolean isCanceled;
    private boolean isRefund;
    private boolean isReplaced;
    private boolean isAdjusted;
    private boolean isActive;

    private String softwareVer;
    private String ticketVer;
    private String faretableVer;

    private int quantity;
    private String status;

    private String transactionId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Boolean ccu=false;
    private Boolean scu=false;
}
