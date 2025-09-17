package com.amay.tvm.backend.repository;


import com.amay.tvm.backend.entity.CoinAmountEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public abstract class CoinAmountRepository {

    protected static final String TABLE_NAME = "coin_amount";
    protected Connection connection = null;

    protected static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            "containerId VARCHAR(255) PRIMARY KEY," +
            "unitAmount INTEGER," +
            "quantity INTEGER," +
            "createdAt TIMESTAMP," +
            "updatedAt TIMESTAMP" +
            ");";

    protected static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME + " (containerId, unitAmount, quantity, createdAt, updatedAt) " +
            "VALUES (?, ?, ?, ?, ?)";

    protected static final String SELECT_BY_ID_SQL = "SELECT * FROM " + TABLE_NAME + " WHERE containerId = ?";
    protected static final String UPDATE_SQL = "UPDATE " + TABLE_NAME + " SET  quantity=?, updatedAt=? WHERE containerId=?";
    protected static final String DELETE_BY_ID_SQL = "DELETE FROM " + TABLE_NAME + " WHERE containerId = ?";
    protected static final String SELECT_ALL_FROM_SQL = "SELECT * FROM " + TABLE_NAME + " WHERE createdAt >= ? ORDER BY createdAt DESC";

    protected abstract void createTableIfNotExists() throws SQLException;
    public abstract String save(CoinAmountEntity coinAmount);
    public abstract CoinAmountEntity findById(String containerId);
    public abstract void update(CoinAmountEntity coinAmount);
    public abstract void deleteById(String containerId);
    public abstract List<CoinAmountEntity> findAllFrom(Timestamp from);
}
