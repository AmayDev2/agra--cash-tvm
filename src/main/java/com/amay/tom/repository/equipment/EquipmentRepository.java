package com.amay.tom.repository.equipment;


import com.amay.tom.model.equipment.entity.Equipment;

public abstract class EquipmentRepository {
    protected static final String TABLE_NAME = "equipment";

    protected static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "config_key VARCHAR(50) PRIMARY KEY, " +
                    "equipment_id VARCHAR(50), " +
                    "line_id VARCHAR(50), " +
                    "station_id VARCHAR(50), " +
                    "equipment_type_id VARCHAR(50), " +
                    "equipment_serial VARCHAR(50), " +
                    "equipment_name VARCHAR(100), " +
                    "equipment_ip VARCHAR(50), " +
                    "status VARCHAR(20) "+
                    ")";

    protected static final String UPSERT_SQL =
            "MERGE INTO " + TABLE_NAME +
                    " (config_key, equipment_id, line_id, station_id, equipment_type_id, equipment_serial, equipment_name, equipment_ip, status) " +
                    "KEY(config_key) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    protected static final String SELECT_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE config_key = ?";

    protected static final String DELETE_ALL_SQL =
            "DELETE FROM " + TABLE_NAME;

    // === Abstract methods ===
    public abstract void insertIfEmpty(Equipment equipment);

    public abstract Equipment get();

    public abstract void deleteAll();
}