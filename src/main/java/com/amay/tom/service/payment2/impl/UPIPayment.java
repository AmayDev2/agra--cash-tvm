package com.amay.tom.service.payment2.impl;

import com.amay.tom.enums.PayMethod;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.service.payment2.PaymentMedia;
import com.amay.tvm.backend.repository.TransactionRepository;

import java.util.UUID;

public class UPIPayment implements PaymentMedia {
    @Override
    public Object pay(double amount, String orderId, Object... args) {
        TransactionRepository transactionRepository= (TransactionRepository) args[1];

        PaymentResponse paymentResponse= new PaymentResponse()
                .setOrderId(orderId)
                .setAmount((int)amount)
                .setTransactionId("UPI"+UUID.randomUUID().toString())
                .setRemoteTransactionId(UUID.randomUUID().toString())
                .setPaymentMode(PayMethod.UPI.name())
                .setSuccess(true)
                .setStatus("SUCCESS");
        saveInDbPaymentInitialization(paymentResponse,transactionRepository);
        saveInDbPaymentCompletion(paymentResponse,transactionRepository);
        return paymentResponse;

    }
}
