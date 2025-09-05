package com.amay.tvm.backend.mapper;


import com.amay.tvm.backend.entity.TransactionEntity;
import com.amay.tvm.backend.model.Transaction;

public class TransactionMapper {
    public static Transaction toModel(TransactionEntity entity) {
        if (entity == null) return null;
        Transaction model = new Transaction();
        model.setTransactionUniqueId(entity.getId());
        model.setStatus(entity.getStatus());
        model.setSubStatus(entity.getSubStatus());
        model.setTransactionId(entity.getTransactionId());
        model.setOrderId(entity.getOrderId());
        model.setPaymentMode(entity.getPaymentMode());
        model.setTransactionType(entity.getTransactionType());
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());
        model.setTransactionCompleteTime(entity.getTransactionCompleteTime());
        model.setAmount(entity.getAmount());
        return model;
    }

    public static TransactionEntity toEntity(Transaction model) {
        if (model == null) return null;
        TransactionEntity entity = new TransactionEntity();
        entity.setId(model.getTransactionUniqueId());
        entity.setStatus(model.getStatus());
        entity.setSubStatus(model.getSubStatus());
        entity.setTransactionId(model.getTransactionId());
        entity.setOrderId(model.getOrderId());
        entity.setPaymentMode(model.getPaymentMode());
        entity.setTransactionType(model.getTransactionType());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedAt(model.getUpdatedAt());
        entity.setTransactionCompleteTime(model.getTransactionCompleteTime());
        entity.setAmount(model.getAmount());
        return entity;
    }
}
