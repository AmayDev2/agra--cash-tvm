package com.amay.tvm.backend.repository;

import com.amay.tvm.backend.entity.TransactionEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public abstract class TransactionRepository {

    protected static final String TABLE_NAME = "transaction";
    protected Connection connection = null;

    protected static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            "transactionUniqueId VARCHAR(255) PRIMARY KEY," +
            "status VARCHAR(50)," +
            "subStatus VARCHAR(50)," +
            "transactionId VARCHAR(255)," +
            "orderId VARCHAR(255)," +
            "paymentMode VARCHAR(50)," +
            "transactionType VARCHAR(50)," +
            "createdAt TIMESTAMP," +
            "updatedAt TIMESTAMP," +
            "transactionCompleteTime BIGINT," +
            "amount INTEGER" +
            ");";

    protected static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME + " (transactionUniqueId, status, subStatus,  orderId, paymentMode, transactionType, createdAt, updatedAt,  amount) " +
            "VALUES (?, ?, ?, ?,  ?, ?, ?,  ?, ?)";

    protected static final String SELECT_BY_ID_SQL = "SELECT * FROM " + TABLE_NAME + " WHERE transactionUniqueId = ?";
    protected static final String UPDATE_SQL = "UPDATE " + TABLE_NAME + " SET status=?, subStatus=?, transactionId=?, orderId=?, paymentMode=?, transactionType=?,  updatedAt=?, transactionCompleteTime=?, amount=? WHERE transactionUniqueId=?";
    protected static final String DELETE_BY_ID_SQL = "DELETE FROM " + TABLE_NAME + " WHERE transactionUniqueId = ?";
    protected static final String SELECT_ALL_FROM_SQL = "SELECT * FROM " + TABLE_NAME + " WHERE createdAt >= ? ORDER BY createdAt DESC";

    protected abstract void createTableIfNotExists() throws SQLException;
    public abstract String save(TransactionEntity transaction);
    public abstract TransactionEntity findById(String transactionUniqueId);
    public abstract void update(TransactionEntity transaction);
    public abstract List<TransactionEntity> findAllFrom(Timestamp from);
}
