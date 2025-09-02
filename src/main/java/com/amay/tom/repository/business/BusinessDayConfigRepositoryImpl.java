package com.amay.tom.repository.business;

import com.amay.tom.model.business.BusinessDayConfigEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BusinessDayConfigRepositoryImpl extends BusinessDayConfigRepository {

    private final Connection connection;

    public BusinessDayConfigRepositoryImpl(Connection connection) {
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
    public void insert(BusinessDayConfigEntity entity) {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL)) {
            pstmt.setString(1, entity.getConfigVer());
            pstmt.setString(2, entity.getBusinessDayName());
            pstmt.setString(3, entity.getDayType());
            pstmt.setString(4, entity.getStartTime());
            pstmt.setString(5, entity.getEndTime());
            pstmt.setDouble(6, entity.getFareMultiplier());
            pstmt.setString(7, entity.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting BusinessDayConfig", e);
        }
    }

    @Override
    public void insertOrUpdateBusinessDayConfig(BusinessDayConfigEntity config) {
        try (PreparedStatement stmt = connection.prepareStatement(UPSERT_SQL)) {
            stmt.setInt(1, config.getId());
            stmt.setString(2, config.getConfigVer());
            stmt.setString(3, config.getBusinessDayName());
            stmt.setString(4, config.getDayType());
            stmt.setString(5, config.getStartTime());
            stmt.setString(6, config.getEndTime());
            stmt.setDouble(7, config.getFareMultiplier());
            stmt.setString(8, config.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting/updating BusinessDayConfig", e);
        }
    }


    @Override
    public List<BusinessDayConfigEntity> findAll() {
        List<BusinessDayConfigEntity> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_SQL)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all BusinessDayConfig", e);
        }
        return list;
    }

    @Override
    public void deleteAll() {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_ALL_SQL)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all BusinessDayConfig rows", e);
        }
    }

    private BusinessDayConfigEntity mapRow(ResultSet rs) throws SQLException {
        return new BusinessDayConfigEntity()
                .setId(rs.getInt("id"))
                .setConfigVer(rs.getString("configVer"))
                .setBusinessDayName(rs.getString("businessDayName"))
                .setDayType(rs.getString("dayType"))
                .setStartTime(rs.getString("startTime"))
                .setEndTime(rs.getString("endTime"))
                .setFareMultiplier(rs.getDouble("fareMultiplier"))
                .setStatus(rs.getString("status"));
    }
}
