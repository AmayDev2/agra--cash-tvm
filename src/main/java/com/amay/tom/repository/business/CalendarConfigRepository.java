package com.amay.tom.repository.business;

import com.amay.tom.model.business.CalendarConfig;
import com.amay.tom.model.business.CalendarConfigEntity;

import java.util.List;

public abstract class CalendarConfigRepository {
    protected static final String TABLE_NAME = "CalendarConfig";

    protected static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "config_ver VARCHAR(50), " +
                    "special_day_name VARCHAR(100), " +
                    "status VARCHAR(50), " +
                    "special_date VARCHAR(10)" +
                    ")";

    protected static final String INSERT_SQL =
            "INSERT INTO " + TABLE_NAME +
                    " (config_ver, special_day_name, status, special_date) " +
                    "VALUES (?, ?, ?, ?)";

    protected static final String UPSERT_SQL =
            "MERGE INTO " + TABLE_NAME + " (special_date, config_ver, special_day_name, status) " +
                    "KEY (special_date) VALUES (?, ?, ?, ?)";

    protected static final String SELECT_ALL_SQL =
            "SELECT * FROM " + TABLE_NAME;

    protected static final String DELETE_ALL_SQL =
            "DELETE FROM " + TABLE_NAME;

    public abstract void deleteAll();

    public abstract void createTableIfNotExists();

    public abstract void insert(CalendarConfigEntity calenderConfig) throws RuntimeException;

    public abstract void insertOrUpdateCalendarConfig(CalendarConfigEntity config);

    public abstract List<CalendarConfigEntity> findAll();
}
