package com.amay.tom.repository.tomConfig;
import com.amay.tom.model.tomConfig.TomConfigEntity;

import java.util.List;

public abstract class TomConfigRepository {

    protected static final String TABLE_NAME = "Tom_Config";

    protected static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY, " +
                    "configVer VARCHAR(50), " +
                    "shiftPauseDuration INT, " +
                    "cartLimit INT, " +
                    "maxDaysOffline INT, " +
                    "offlineRefund INT" +
                    ")";

    protected static final String INSERT_SQL =
            "INSERT INTO " + TABLE_NAME + " (id, configVer, shiftPauseDuration, cartLimit, maxDaysOffline, offlineRefund) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    protected static final String SELECT_BY_ID_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

    protected static final String SELECT_ALL_SQL =
            "SELECT * FROM " + TABLE_NAME;

    protected static final String DELETE_BY_ID_SQL =
            "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

    protected static final String DELETE_ALL_SQL =
            "DELETE FROM " + TABLE_NAME;

    // Abstract methods
    public abstract void insert(TomConfigEntity entity);

    public abstract TomConfigEntity findById(int id);

    public abstract List<TomConfigEntity> findAll();

    public abstract void deleteById(int id);

    public abstract void deleteAll();
}
