package com.amay.tvm.backend.entity;

import com.amay.tvm.backend.enums.TransactionStatus;
import com.amay.tvm.backend.enums.TransactionSubStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class TransactionEntity {
    private String id; //transactionUniqueId
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
