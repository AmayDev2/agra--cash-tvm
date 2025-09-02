package com.amay.tom.repository.business;

import com.amay.tom.model.business.PeakTimeConfigEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeakTimeConfigRepositoryImpl extends PeakTimeConfigRepository {

    private final Connection connection;

    public PeakTimeConfigRepositoryImpl(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    @Override
    public void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating table " + TABLE_NAME, e);
        }
    }

    @Override
    public void insert(PeakTimeConfigEntity entity) {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL)) {
            pstmt.setString(1, entity.getConfigVer());
            pstmt.setString(2, entity.getPeakTimeName());
            pstmt.setString(3, entity.getStartTime());
            pstmt.setString(4, entity.getEndTime());
            pstmt.setDouble(5, entity.getFareMultiplier());
            pstmt.setString(6, entity.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting PeakTimeConfig", e);
        }
    }

    @Override
    public void insertOrUpdatePeakTimeConfig(PeakTimeConfigEntity config) {
        try (PreparedStatement stmt = connection.prepareStatement(UPSERT_SQL)) {
            stmt.setInt(1, config.getId());
            stmt.setString(2, config.getConfigVer());
            stmt.setString(3, config.getPeakTimeName());
            stmt.setString(4, config.getStartTime());
            stmt.setString(5, config.getEndTime());
            stmt.setDouble(6, config.getFareMultiplier());
            stmt.setString(7, config.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting/updating PeakTimeConfig", e);
        }
    }


    @Override
    public List<PeakTimeConfigEntity> findAll() {
        List<PeakTimeConfigEntity> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_SQL)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all PeakTimeConfig", e);
        }
        return list;
    }

    @Override
    public void deleteAll() {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_ALL_SQL)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all PeakTimeConfig", e);
        }
    }

    private PeakTimeConfigEntity mapRow(ResultSet rs) throws SQLException {
        PeakTimeConfigEntity config = new PeakTimeConfigEntity();
        config.setId(rs.getInt("id"));
        config.setConfigVer(rs.getString("configVer"));
        config.setPeakTimeName(rs.getString("peakTimeName"));
        config.setStartTime(rs.getString("startTime"));
        config.setEndTime(rs.getString("endTime"));
        config.setFareMultiplier(rs.getDouble("fareMultiplier"));
        config.setStatus(rs.getString("status"));
        return config;
    }
}

