package com.amay.tvm.backend.repository;


import com.amay.tvm.backend.entity.NoteAmountEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public abstract class NoteAmountRepository {

    protected static final String TABLE_NAME = "note_amount";
    protected Connection connection = null;

    protected static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            "containerId VARCHAR(255) ," +
            "unitAmount INTEGER PRIMARY KEY," +
            "cashInQuantity INTEGER," +
            "cashOutQuantity INTEGER," +
            "createdAt TIMESTAMP," +
            "updatedAt TIMESTAMP" +
            ");";

    protected static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME + " (containerId, unitAmount, cashInQuantity, cashOutQuantity, createdAt, updatedAt) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    protected static final String SELECT_BY_ID_SQL = "SELECT * FROM " + TABLE_NAME + " WHERE unitAmount = ?";
    protected static final String UPDATE_SQL = "UPDATE " + TABLE_NAME + " SET cashInQuantity=?, cashOutQuantity=?, updatedAt=? WHERE unitAmount=?";
    protected static final String DELETE_BY_ID_SQL = "DELETE FROM " + TABLE_NAME + " WHERE unitAmount = ?";
    protected static final String SELECT_ALL_FROM_SQL = "SELECT * FROM " + TABLE_NAME + " WHERE createdAt >= ? ORDER BY createdAt DESC";
    protected static final String RESET_TO_ZERO_SQL = "UPDATE " + TABLE_NAME + " SET cashInQuantity=0, cashOutQuantity=0, updatedAt=?";

    protected abstract void createTableIfNotExists() throws SQLException;
    public abstract String save(NoteAmountEntity noteAmount);
    public abstract String save(List<NoteAmountEntity> noteAmount);
    public abstract NoteAmountEntity findById(String containerId);
    public abstract void updateToAdd(NoteAmountEntity noteAmount);
    public abstract void updateToAdd(List<NoteAmountEntity> noteAmount);
    public abstract void resetToZero();
    public abstract void deleteById(String unitAmount);
    public abstract List<NoteAmountEntity> findAllFrom(Timestamp from);
}
