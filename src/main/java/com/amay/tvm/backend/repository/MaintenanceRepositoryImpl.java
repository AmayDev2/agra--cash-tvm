package com.amay.tvm.backend.repository;

import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.backend.model.MaintenanceLog;
import org.tinylog.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceRepositoryImpl extends MaintenanceRepository {
    private final Connection connection;

    public MaintenanceRepositoryImpl(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating maintenance_log table", e);
        }
    }

    @Override
    public void insert(MaintenanceLog log) {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL)) {
            pstmt.setString(1, log.getEvent());
            pstmt.setString(2, log.getStatus());
            pstmt.setString(3, log.getTimestamp());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting maintenance log", e);
        }
    }

    @Override
    public List<MaintenanceLog> getAllLogs() {
        List<MaintenanceLog> logs = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_SQL)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                logs.add(new MaintenanceLog(
                        rs.getString("event"),
                        rs.getString("status"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            Logger.tag(LoggerTag.APP).error("Error fetching maintenance logs", e);
//            throw new RuntimeException("Error fetching maintenance logs", e);
        }
        return logs;
    }

    @Override
    public void deleteAll() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(DELETE_ALL_SQL);
        } catch (SQLException e) {
            Logger.error("Error deleting all maintenance logs", e);
        }
    }
}
