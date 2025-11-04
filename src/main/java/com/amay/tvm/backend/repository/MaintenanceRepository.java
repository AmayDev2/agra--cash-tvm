package com.amay.tvm.backend.repository;

import com.amay.tvm.backend.model.MaintenanceLog;

import java.util.List;

public abstract class MaintenanceRepository {
    protected static final String CREATE_TABLE_SQL = """
    CREATE TABLE IF NOT EXISTS maintenance_log (
        event VARCHAR(255) NOT NULL,
        status VARCHAR(255) NOT NULL,
        timestamp TIMESTAMP NOT NULL
    )
    """;


    protected static final String INSERT_SQL = """
        INSERT  INTO maintenance_log(event, status, timestamp)
        VALUES (?, ?, ?)
        """;

    protected static final String SELECT_ALL_SQL = """
        SELECT DISTINCT event, status, timestamp
        FROM maintenance_log
        ORDER BY timestamp ASC
        """;

    protected static final String DELETE_ALL_SQL = "DELETE FROM maintenance_log";

    public abstract void insert(MaintenanceLog log);

    public abstract List<MaintenanceLog> getAllLogs();

    public abstract void deleteAll();
}
