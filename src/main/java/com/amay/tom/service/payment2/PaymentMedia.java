package com.amay.tom.service.payment2;

import com.amay.tom.enums.PayMethod;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.utils.time.TimeUtil;
import com.amay.tvm.backend.entity.TransactionEntity;
import com.amay.tvm.backend.enums.TransactionStatus;
import com.amay.tvm.backend.enums.TransactionSubStatus;
import com.amay.tvm.backend.mapper.TransactionMapper;
import com.amay.tvm.backend.model.Transaction;
import com.amay.tvm.backend.repository.TransactionRepository;

import java.time.Instant;

public interface PaymentMedia {
     Object pay(double amount,String orderId, Object... args);
    default void saveInDbPaymentInitialization(PaymentResponse paymentResponse, TransactionRepository transactionRepository){
        Transaction transaction = new Transaction();
        transaction.setAmount(paymentResponse.getAmount());
        transaction.setOrderId(paymentResponse.getOrderId());
        transaction.setPaymentMode(paymentResponse.getPaymentMode());
        transaction.setTransactionUniqueId(paymentResponse.getTransactionId());
        transaction.setStatus(TransactionStatus.valueOf(paymentResponse.getStatus()));
        transaction.setSubStatus(TransactionSubStatus.NONE);
        transaction.setTransactionCompleteTime(0L);
        transactionRepository.save(TransactionMapper.toEntity(transaction));
    }

    default void saveInDbPaymentCompletion(PaymentResponse paymentResponse, TransactionRepository transactionRepository){
        paymentResponse.setTransactionTimeEpoch(Instant.now().toEpochMilli());
        paymentResponse.setTransactionTime(TimeUtil.epochMilliToFormattedSystemLocalDateTime(paymentResponse.getTransactionTimeEpoch(),"dd-MM-yyyy HH:mm:ss"));
        TransactionEntity transaction=transactionRepository.findById(paymentResponse.getTransactionId());
        transaction.setTransactionId(paymentResponse.getRemoteTransactionId());
        transaction.setStatus(TransactionStatus.valueOf(paymentResponse.getStatus()));
        transaction.setSubStatus(TransactionSubStatus.NONE);
        transaction.setTransactionCompleteTime(paymentResponse.getTransactionTimeEpoch());
        transactionRepository.update(transaction);
    }

}
