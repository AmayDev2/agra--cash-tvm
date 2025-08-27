package com.amay.tom.repository.business;

import com.amay.tom.model.business.BusinessDayConfigEntity;

import java.util.List;

public abstract class BusinessDayConfigRepository {

    protected static final String TABLE_NAME = "BusinessDayConfig";

    protected static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "configVer VARCHAR(50), " +
                    "businessDayName VARCHAR(100), " +
                    "dayType VARCHAR(50), " +
                    "startTime VARCHAR(20), " +
                    "endTime VARCHAR(20), " +
                    "fareMultiplier DOUBLE, " +
                    "status VARCHAR(20)" +
                    ")";

    protected static final String INSERT_SQL =
            "INSERT INTO " + TABLE_NAME + " (configVer, businessDayName, dayType, startTime, endTime, fareMultiplier, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    protected static final String UPSERT_SQL =
            "MERGE INTO " + TABLE_NAME + " (id, config_ver, business_day_name, day_type, start_time, end_time, fare_multiplier, status) " +
                    "KEY (id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


    protected static final String SELECT_ALL_SQL =
            "SELECT * FROM " + TABLE_NAME;

    protected static final String DELETE_ALL_SQL =
            "DELETE FROM " + TABLE_NAME;

    public abstract void createTableIfNotExists();

    public abstract void insert(BusinessDayConfigEntity entity);

    public abstract void insertOrUpdateBusinessDayConfig(BusinessDayConfigEntity config);

    public abstract List<BusinessDayConfigEntity> findAll();

    public abstract void deleteAll();
}
