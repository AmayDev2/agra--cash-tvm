package com.amay.tvm.backend.model;


import com.amay.tvm.backend.enums.TransactionStatus;
import com.amay.tvm.backend.enums.TransactionSubStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class Transaction {
    private String transactionUniqueId;
    private TransactionStatus status;
    private TransactionSubStatus subStatus;
    private String transactionId;
    private String orderId;
    private String paymentMode;
    private String transactionType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long transactionCompleteTime;
    private Integer amount;
}
