package com.amay.tvm.backend.repository;


import com.amay.tvm.Env;
import com.amay.tvm.backend.entity.CoinAmountEntity;
import org.tinylog.Logger;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CoinAmountRepositoryImpl extends CoinAmountRepository {

    public CoinAmountRepositoryImpl(Connection connection) {
        try {
            this.connection = connection;
            this.createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void createTableIfNotExists() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        }
        this.createHoppersEntry();
    }

    private void createHoppersEntry() {
        List<CoinAmountEntity> hoppersList=new ArrayList<>();
        hoppersList.add(new CoinAmountEntity().setUnitAmount(Env.hop1).setQuantity(0).setContainerId("1"));
        hoppersList.add(new CoinAmountEntity().setUnitAmount(Env.hop2).setQuantity(0).setContainerId("2"));
        hoppersList.add(new CoinAmountEntity().setUnitAmount(Env.hop3).setQuantity(0).setContainerId("3"));
        for(CoinAmountEntity coinAmount:hoppersList){
            save(coinAmount);
        }
    }

    @Override
    public String save(CoinAmountEntity coinAmount) {

        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL)) {
            pstmt.setString(1, coinAmount.getContainerId());
            pstmt.setInt(2, coinAmount.getUnitAmount());
            pstmt.setInt(3, coinAmount.getQuantity());
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
            return coinAmount.getContainerId();
        } catch (SQLException e) {
            Logger.error("Error inserting CoinAmountEntity: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public CoinAmountEntity findById(String containerId) {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            pstmt.setString(1, containerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            Logger.error("Error finding CoinAmountEntity by id: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void update(CoinAmountEntity coinAmount) {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_SQL)) {
            pstmt.setInt(1, coinAmount.getUnitAmount());
            pstmt.setInt(2, coinAmount.getQuantity());
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(4, coinAmount.getContainerId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Error updating CoinAmountEntity: {}", e.getMessage());
        }
    }

    @Override
    public void deleteById(String containerId) {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            pstmt.setString(1, containerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Error deleting CoinAmountEntity: {}", e.getMessage());
        }
    }

    @Override
    public List<CoinAmountEntity> findAllFrom(Timestamp from) {
        List<CoinAmountEntity> coins = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_FROM_SQL)) {
            pstmt.setTimestamp(1, from);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                coins.add(mapRow(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error fetching CoinAmountEntity list: {}", e.getMessage());
        }
        return coins;
    }

    private CoinAmountEntity mapRow(ResultSet rs) throws SQLException {
        return new CoinAmountEntity()
                .setContainerId(rs.getString("containerId"))
                .setUnitAmount(rs.getInt("unitAmount"))
                .setQuantity(rs.getInt("quantity"))
                .setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime())
                .setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
    }
}
