package com.amay.tom.repository.station;

import com.amay.tom.model.station.StationEntity;

import java.util.List;

public abstract class StationRepository {

    protected static final String TABLE_NAME = "Stations";

    protected static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "station_id VARCHAR(50) PRIMARY KEY, " +
                    "station_name VARCHAR(255)" +
                    ")";

    protected static final String INSERT_SQL =
            "INSERT INTO " + TABLE_NAME + " (station_id, station_name) VALUES (?, ?)";

    protected static final String UPSERT_SQL =
            "MERGE INTO stations (station_id, station_name) " +
                    "KEY (station_id) VALUES (?, ?)";

    protected static final String SELECT_BY_ID_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE station_id = ?";

    protected static final String SELECT_ALL_SQL =
            "SELECT * FROM " + TABLE_NAME;

    protected static final String DELETE_BY_ID_SQL =
            "DELETE FROM " + TABLE_NAME + " WHERE station_id = ?";

    protected static final String DELETE_ALL_SQL =
            "DELETE FROM " + TABLE_NAME;

    public abstract void insert(StationEntity station) throws RuntimeException;

    public abstract void insertOrUpdateStation(StationEntity station);

    public abstract StationEntity findById(String stationId);
    public abstract List<StationEntity> findAll();
    public abstract void deleteById(String stationId);
    public abstract void deleteAll();
}

