package com.amay.tvm.backend.repository;

import com.amay.tvm.backend.entity.AmountSnapShotEntity;
import com.amay.tvm.backend.enums.ContainerId;
import com.amay.tvm.backend.enums.LoggerTag;
import org.tinylog.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AmountSnapShotRepositoryImpl extends AmountSnapShotRepository {

    public AmountSnapShotRepositoryImpl(Connection connection) {
        this.connection = connection;
        try {
            createTableIfNotExists();
        } catch (SQLException e) {
            Logger.tag(LoggerTag.APP).error("Error creating AmountSnapShot table: {}", e.getMessage());
        }
    }

    /**
     * Create table if not exists
     */
    private void createTableIfNotExists() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
            Logger.tag(LoggerTag.APP).info("Table '{}' ensured to exist.", TABLE_NAME);
        } catch (SQLException e) {
            Logger.tag(LoggerTag.APP).error("Error creating table {}: {}", TABLE_NAME, e.getMessage());
            throw e;
        }
    }

    /**
     * Insert one snapshot record
     */
    @Override
    public void save(AmountSnapShotEntity snapshot) {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT)) {
            pstmt.setString(1, snapshot.getShiftId());
            pstmt.setString(2, snapshot.getContainerId());
            pstmt.setInt(3, snapshot.getUnitAmount());
            pstmt.setInt(4, snapshot.getCurrentQuantity());
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
            Logger.tag(LoggerTag.BUSS).info("Snapshot inserted: Shift={}, Unit={}", snapshot.getShiftId(), snapshot.getUnitAmount());
        } catch (SQLException e) {
            Logger.tag(LoggerTag.APP).error("Error saving AmountSnapShotEntity: {}", e.getMessage());
        }
    }

    /**
     * Save multiple snapshots in a single transaction
     */
    @Override
    public void saveAll(List<AmountSnapShotEntity> snapshots) {
        try {
            connection.setAutoCommit(false);
            for (AmountSnapShotEntity entity : snapshots) {
                save(entity);
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                Logger.tag(LoggerTag.APP).error("Rollback failed: {}", ex.getMessage());
            }
            Logger.tag(LoggerTag.APP).error("Error inserting multiple AmountSnapShotEntities: {}", e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                Logger.tag(LoggerTag.APP).error("Failed to reset auto-commit: {}", e.getMessage());
            }
        }
    }

    /**
     * Find all snapshot records
     */
    @Override
    public List<AmountSnapShotEntity> findAll() {
        List<AmountSnapShotEntity> result = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(Find_All)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            Logger.tag(LoggerTag.APP).error("Error fetching AmountSnapShotEntity list: {}", e.getMessage());
        }
        return result;
    }

    /**
     * Delete all snapshots by shiftId
     */
    @Override
    public void deleteByShiftId(String shiftId) {
        try (PreparedStatement pstmt = connection.prepareStatement(Delete_SQL)) {
            pstmt.setString(1, shiftId);
            int rows = pstmt.executeUpdate();
            Logger.tag(LoggerTag.BUSS).info("Deleted {} snapshots for ShiftId={}", rows, shiftId);
        } catch (SQLException e) {
            Logger.tag(LoggerTag.APP).error("Error deleting AmountSnapShotEntity by shiftId: {}", e.getMessage());
        }
    }

    @Override
    public List<AmountSnapShotEntity> findAllByShiftId(String shiftId) {
        List<AmountSnapShotEntity> result = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_ALL_BY_SHIFT_ID)) {;
            pstmt.setString(1, shiftId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            Logger.tag(LoggerTag.APP).error("Error fetching AmountSnapShotEntity list by shiftId: {}", e.getMessage());
        }
        return result;
    }


    /**
     * Map ResultSet to AmountSnapShotEntity
     */
    private AmountSnapShotEntity mapRow(ResultSet rs) throws SQLException {
        AmountSnapShotEntity entity = new AmountSnapShotEntity();
        entity.setShiftId(rs.getString("shiftId"));
        entity.setContainerId(rs.getString("containerId"));
        entity.setUnitAmount(rs.getInt("unitAmount"));
        entity.setCurrentQuantity(rs.getInt("currentQuantity"));

        Timestamp createdAt = rs.getTimestamp("createdAt");
        Timestamp updatedAt = rs.getTimestamp("updatedAt");
        if (createdAt != null) entity.setCreatedAt(createdAt.toLocalDateTime());
        if (updatedAt != null) entity.setUpdatedAt(updatedAt.toLocalDateTime());
        return entity;
    }
}
