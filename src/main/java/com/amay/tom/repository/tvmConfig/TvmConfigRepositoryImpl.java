package com.amay.tom.repository.tvmConfig;

import com.amay.tom.model.tvmConfig.TvmConfigEntity;
import com.amay.tom.model.tvmConfig.TvmConfigMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TvmConfigRepositoryImpl extends TvmConfigRepository {

    private final Connection connection;

    public TvmConfigRepositoryImpl(Connection connection) {
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
    public void insert(TvmConfigEntity entity) {
        try (PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            ps.setInt(1, entity.getId());
            ps.setString(2, entity.getConfigVer());
            ps.setInt(3, entity.getMaxChangeDispense());
            ps.setInt(4, entity.getMaxCoinDispensedQuantity());
            ps.setInt(5, entity.getMaxCoinDispensedTotalQuantity());
            ps.setInt(6, entity.getMaxBnrAcceptAmount());
            ps.setInt(7, entity.getMaxBnrDispensedAmount());
            ps.setInt(8, entity.getMaxBnrDispensedQuantity());
            ps.setInt(9, entity.getMaxBnrDispensedTotalQuantity());
            ps.setInt(10, entity.getHopper1UnitAmount());
            ps.setInt(11, entity.getHopper2UnitAmount());
            ps.setInt(12, entity.getHopper3UnitAmount());
            ps.setInt(13, entity.getTransactionTimeout());
            ps.setInt(14, entity.getPaymentScreenTimeout());
            ps.setInt(15, entity.getIdleScreenTimeout());
            ps.setBoolean(16, entity.isBnrEnabled());
            ps.setBoolean(17, entity.isCoinDispenserEnabled());
            ps.setBoolean(18, entity.isPosEnabled());
            ps.setBoolean(19, entity.isUpiEnabled());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting TvmConfig record", e);
        }
    }

    @Override
    public TvmConfigEntity findById(int id) {
        try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding TvmConfig by id", e);
        }
        return null;
    }

    @Override
    public List<TvmConfigEntity> findAll() {
        List<TvmConfigEntity> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all TvmConfig records", e);
        }
        return list;
    }

    @Override
    public void deleteById(int id) {
        try (PreparedStatement ps = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting TvmConfig by id", e);
        }
    }

    @Override
    public void deleteAll() {
        try (PreparedStatement ps = connection.prepareStatement(DELETE_ALL_SQL)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all TvmConfig records", e);
        }
    }

    private TvmConfigEntity mapRow(ResultSet rs) throws SQLException {
        TvmConfigEntity entity = new TvmConfigEntity();
        entity.setId(rs.getInt("id"));
        entity.setConfigVer(rs.getString("configVer"));
        entity.setMaxChangeDispense(rs.getInt("maxChangeDispense"));
        entity.setMaxCoinDispensedQuantity(rs.getInt("maxCoinDispensedQuantity"));
        entity.setMaxCoinDispensedTotalQuantity(rs.getInt("maxCoinDispensedTotalQuantity"));
        entity.setMaxBnrAcceptAmount(rs.getInt("maxBnrAcceptAmount"));
        entity.setMaxBnrDispensedAmount(rs.getInt("maxBnrDispensedAmount"));
        entity.setMaxBnrDispensedQuantity(rs.getInt("maxBnrDispensedQuantity"));
        entity.setMaxBnrDispensedTotalQuantity(rs.getInt("maxBnrDispensedTotalQuantity"));
        entity.setHopper1UnitAmount(rs.getInt("hopper1UnitAmount"));
        entity.setHopper2UnitAmount(rs.getInt("hopper2UnitAmount"));
        entity.setHopper3UnitAmount(rs.getInt("hopper3UnitAmount"));
        entity.setTransactionTimeout(rs.getInt("transactionTimeout"));
        entity.setPaymentScreenTimeout(rs.getInt("paymentScreenTimeout"));
        entity.setIdleScreenTimeout(rs.getInt("idleScreenTimeout"));
        entity.setBnrEnabled(rs.getBoolean("bnrEnabled"));
        entity.setCoinDispenserEnabled(rs.getBoolean("coinDispenserEnabled"));
        entity.setPosEnabled(rs.getBoolean("posEnabled"));
        entity.setUpiEnabled(rs.getBoolean("upiEnabled"));
        return entity;
    }
}
