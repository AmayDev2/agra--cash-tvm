package com.amay.tom.repository.business;

import com.amay.tom.model.business.CalendarConfigEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CalendarConfigRepositoryImpl extends CalendarConfigRepository {
    private final Connection connection;

    public CalendarConfigRepositoryImpl(Connection connection) {
        this.connection = connection;
        this.createTableIfNotExists();
    }

    @Override
    public void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating CalendarConfig table", e);
        }
    }

    @Override
    public void insert(CalendarConfigEntity calenderConfig) throws RuntimeException {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL)) {
            pstmt.setString(1, calenderConfig.getConfigVer());
            pstmt.setString(2, calenderConfig.getSpecialDayName());
            pstmt.setString(3, calenderConfig.getStatus());
            pstmt.setString(4, calenderConfig.getSpecialDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting CalendarConfig", e);
        }
    }

    @Override
    public void insertOrUpdateCalendarConfig(CalendarConfigEntity config) {
        try (PreparedStatement stmt = connection.prepareStatement(UPSERT_SQL)) {
            stmt.setString(1, config.getSpecialDate());
            stmt.setString(2, config.getConfigVer());
            stmt.setString(3, config.getSpecialDayName());
            stmt.setString(4, config.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting/updating CalendarConfig", e);
        }
    }


    @Override
    public List<CalendarConfigEntity> findAll() {
        List<CalendarConfigEntity> configs = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                configs.add(mapResultSetToCalenderConfig(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all CalenderConfigs", e);
        }
        return configs;
    }

    @Override
    public void deleteAll() {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_ALL_SQL)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all CalendarConfig rows", e);
        }
    }


    private CalendarConfigEntity mapResultSetToCalenderConfig(ResultSet rs) throws SQLException {
        CalendarConfigEntity config = new CalendarConfigEntity();
        config.setConfigVer(rs.getString("config_ver"));
        config.setSpecialDayName(rs.getString("special_day_name"));
        config.setStatus(rs.getString("status"));
        config.setSpecialDate(rs.getString("special_date"));
        return config;
    }
}
