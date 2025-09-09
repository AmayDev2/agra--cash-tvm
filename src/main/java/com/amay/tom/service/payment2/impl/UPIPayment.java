package com.amay.tom.service.payment2.impl;

import com.amay.tom.enums.PayMethod;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.service.payment2.PaymentMedia;
import com.amay.tvm.backend.repository.TransactionRepository;
import com.amay.tvm.controller.PaymentController;

import java.util.UUID;

public class UPIPayment implements PaymentMedia {
    @Override
    public Object

    pay(double amount, String orderId, Object... args) {

        TransactionRepository transactionRepository= (TransactionRepository) args[1];
        PaymentController paymentController= (PaymentController) args[2];

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

        new Thread(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            paymentController.eventListener(paymentResponse);
        }).start();
        return paymentResponse;

    }
}
