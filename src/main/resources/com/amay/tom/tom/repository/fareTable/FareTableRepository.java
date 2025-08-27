package com.amay.tom.repository.fareTable;

import com.amay.tom.model.faretable.FareRowEntity;

import java.util.List;

public abstract class FareTableRepository {
    protected static final String TABLE_NAME = "fare_table";

    protected static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "source VARCHAR(50) NOT NULL, " +
                    "destination VARCHAR(50) NOT NULL, " +
                    "fare_amount DOUBLE, " +
                    "PRIMARY KEY (source, destination)" +
                    ")";

    protected static final String INSERT_SQL =
            "INSERT INTO fare_table (source, destination, fare_amount) VALUES (?, ?, ?)";


    // MySQL UPSERT (replace with PostgreSQL syntax if needed)
    protected static final String UPSERT_SQL =
            "MERGE INTO " + TABLE_NAME + " (source, destination, fare_amount) " +
                    "KEY (source, destination) VALUES (?, ?, ?)";


    protected static final String SELECT_BY_ID_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE source = ? AND destination = ?";

    protected static final String SELECT_ALL_SQL =
            "SELECT * FROM " + TABLE_NAME;

    protected static final String DELETE_ALL_SQL =
            "DELETE FROM " + TABLE_NAME;

    protected static final String GET_UNIQUE_SOURCE =
            "SELECT COUNT(DISTINCT source) AS source_count " +
                    "FROM " + TABLE_NAME;


    public abstract void insert(FareRowEntity fareRowEntity);

    public abstract void insertOrUpdate(FareRowEntity fareEntity);
    public abstract FareRowEntity findById(String source, String destination);

    public abstract int getUniqueSourceCount();

    public abstract List<FareRowEntity> findAll();
    public abstract void deleteAll();
}
