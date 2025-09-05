package com.amay.tvm.backend.enums;

public enum TransactionSubStatus {
    NONE,
    REFUND_INITIATED,
    REFUND_COMPLETED,
    REFUND_FAILED,
    CANCELLED,
    PARTIALLY_REFUNDED,
    ROLLBACK_INITIATED,
    ROLLBACK_COMPLETED,
    ROLLBACK_FAILED,

}
