package com.amay.tom.repository.business;

import com.amay.tom.model.business.PeakTimeConfigEntity;

import java.util.List;

public abstract class PeakTimeConfigRepository {

    protected static final String TABLE_NAME = "Peak_Time_Config";

    protected static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "configVer VARCHAR(50), " +
                    "peakTimeName VARCHAR(100), " +
                    "startTime VARCHAR(20), " +
                    "endTime VARCHAR(20), " +
                    "fareMultiplier DOUBLE, " +
                    "status VARCHAR(20)" +
                    ")";

    protected static final String INSERT_SQL =
            "INSERT INTO " + TABLE_NAME + " (configVer, peakTimeName, startTime, endTime, fareMultiplier, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";


    protected static final String UPSERT_SQL =
            "MERGE INTO " + TABLE_NAME + " (id, config_ver, peak_time_name, start_time, end_time, fare_multiplier, status) " +
                    "KEY (id) VALUES (?, ?, ?, ?, ?, ?, ?)";

    protected static final String SELECT_ALL_SQL =
            "SELECT * FROM " + TABLE_NAME;

    protected static final String DELETE_ALL_SQL =
            "DELETE FROM " + TABLE_NAME;

    public abstract void createTableIfNotExists();

    public abstract void insert(PeakTimeConfigEntity entity);

    public abstract void insertOrUpdatePeakTimeConfig(PeakTimeConfigEntity config);

    public abstract List<PeakTimeConfigEntity> findAll();

    public abstract void deleteAll();
}

