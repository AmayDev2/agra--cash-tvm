package com.amay.tvm.backend.repository;

import com.amay.tvm.backend.entity.AmountSnapShotEntity;

import java.sql.Connection;
import java.util.List;

public abstract class AmountSnapShotRepository {
    protected static final String TABLE_NAME = "amount_snap_shot";
    protected Connection connection;

    protected static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            "shiftId VARCHAR(255) ," +
            "containerId VARCHAR(255) ," +
            "unitAmount INTEGER ," +
            "currentQuantity INTEGER," +
            "createdAt TIMESTAMP," +
            "updatedAt TIMESTAMP," +
            "UNIQUE (shiftId , unitAmount)"+
            ");";


    protected static final String INSERT = "INSERT INTO " + TABLE_NAME + " (shiftId, containerId, unitAmount, currentQuantity, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?);";
    protected static final String Find_All = "SELECT * FROM " + TABLE_NAME + ";";
    protected static final String Delete_SQL = "DELETE FROM " + TABLE_NAME + " WHERE shiftId = ?;";
    protected static final String FIND_ALL_BY_SHIFT_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE shiftId = ?";


    public abstract void save(AmountSnapShotEntity snapshot);

    public abstract void saveAll(List<AmountSnapShotEntity> snapshots);

    public abstract List<AmountSnapShotEntity> findAll();

    public abstract void deleteByShiftId(String shiftId);

    public abstract List<AmountSnapShotEntity> findAllByShiftId(String shiftId);
}
