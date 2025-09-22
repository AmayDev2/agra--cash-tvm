package com.amay.tom.model.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Data
public class PaymentResponse {
    private String orderId;
    private String transactionId;
    private String remoteTransactionId;
    private String transactionTime;
    private long transactionTimeEpoch;
    private int amount;
    private String paymentMode;
    private String status;
    private boolean isSuccess;
    //FOR BNR
    private int denomination;
}
