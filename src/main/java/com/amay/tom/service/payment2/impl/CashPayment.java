package com.amay.tom.service.payment2.impl;

import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.service.payment2.PaymentMedia;

import java.util.UUID;

public class CashPayment implements PaymentMedia {

    @Override
    public Object pay(double amount, String orderId, Object... args) {
        return new PaymentResponse()
                .setOrderId(orderId)
                .setAmount((int)amount)
                .setTransactionId("CASH_"+UUID.randomUUID())
                .setStatus("SUCCESS");
    }
}
