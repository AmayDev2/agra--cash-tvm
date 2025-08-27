package com.amay.tom.repository.station;

import com.amay.tom.model.station.StationEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StationRepositoryImpl extends StationRepository {

    private final Connection connection;

    public StationRepositoryImpl(Connection connection) {
        this.connection = connection;
        this.createTableIfNotExists();
    }


    private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating Stations table", e);
        }
    }

    @Override
    public void insert(StationEntity station) throws RuntimeException {
        try (PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            ps.setString(1, station.getStationId());
            ps.setString(2, station.getStationName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting station", e);
        }
    }

    @Override
    public void insertOrUpdateStation(StationEntity station) {
        try (PreparedStatement stmt = connection.prepareStatement(UPSERT_SQL)) {
            stmt.setString(1, station.getStationId());
            stmt.setString(2, station.getStationName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting/updating station", e);
        }
    }


    @Override
    public StationEntity findById(String stationId) {
        try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setString(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StationEntity station = new StationEntity();
                    station.setStationId(rs.getString("station_id"));
                    station.setStationName(rs.getString("station_name"));
                    return station;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding station by ID", e);
        }
        return null;
    }

    @Override
    public List<StationEntity> findAll() {
        List<StationEntity> stations = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                StationEntity station = new StationEntity();
                station.setStationId(rs.getString("station_id"));
                station.setStationName(rs.getString("station_name"));
                stations.add(station);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all stations", e);
        }
        return stations;
    }

    @Override
    public void deleteById(String stationId) {
        try (PreparedStatement ps = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            ps.setString(1, stationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting station by ID", e);
        }
    }

    @Override
    public void deleteAll() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(DELETE_ALL_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all stations", e);
        }
    }
}
