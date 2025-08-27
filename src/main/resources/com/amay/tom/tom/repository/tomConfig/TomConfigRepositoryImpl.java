package com.amay.tom.repository.tomConfig;

import com.amay.tom.model.tomConfig.TomConfigEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TomConfigRepositoryImpl extends TomConfigRepository {
    private final Connection connection;

    public TomConfigRepositoryImpl(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating table " + TABLE_NAME, e);
        }
    }

    @Override
    public void insert(TomConfigEntity entity) {
        try (PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            ps.setInt(1, entity.getId());
            ps.setString(2, entity.getConfigVer());
            ps.setInt(3, entity.getShiftPauseDuration());
            ps.setInt(4, entity.getCartLimit());
            ps.setInt(5, entity.getMaxDaysOffline());
            ps.setInt(6, entity.getOfflineRefund());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting TomConfig record", e);
        }
    }

    @Override
    public TomConfigEntity findById(int id) {
        try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding TomConfig by id", e);
        }
        return null;
    }

    @Override
    public List<TomConfigEntity> findAll() {
        List<TomConfigEntity> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all TomConfig records", e);
        }
        return list;
    }

    @Override
    public void deleteById(int id) {
        try (PreparedStatement ps = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting TomConfig by id", e);
        }
    }

    @Override
    public void deleteAll() {
        try (PreparedStatement ps = connection.prepareStatement(DELETE_ALL_SQL)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all TomConfig records", e);
        }
    }

    private TomConfigEntity mapRow(ResultSet rs) throws SQLException {
        TomConfigEntity entity = new TomConfigEntity();
        entity.setId(rs.getInt("id"));
        entity.setConfigVer(rs.getString("configVer"));
        entity.setShiftPauseDuration(rs.getInt("shiftPauseDuration"));
        entity.setCartLimit(rs.getInt("cartLimit"));
        entity.setMaxDaysOffline(rs.getInt("maxDaysOffline"));
        entity.setOfflineRefund(rs.getInt("offlineRefund"));
        return entity;
    }
}
