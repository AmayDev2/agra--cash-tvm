package com.amay.tvm.backend.repository;

import com.amay.tvm.backend.entity.NoteAmountEntity;
import org.tinylog.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NoteAmountRepositoryImpl extends NoteAmountRepository {

    public NoteAmountRepositoryImpl(Connection connection) {
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
            stmt.execute(CREATE_TABLE_SQL); // Create table if it does not exist
        } catch (SQLException e) {
            Logger.error("Error creating table for NoteAmountEntity: {}", e.getMessage());
            throw new SQLException("Error creating table for NoteAmountEntity", e);
        }
    }

    @Override
    public String save(NoteAmountEntity noteAmount) {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL)) {
            pstmt.setString(1, noteAmount.getContainerId());
            pstmt.setInt(2, noteAmount.getUnitAmount());
            pstmt.setInt(3, noteAmount.getCashInQuantity());
            pstmt.setInt(4, noteAmount.getCashOutQuantity());
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
            return noteAmount.getContainerId();
        } catch (SQLException e) {
            Logger.error("Error inserting NoteAmountEntity: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String save(List<NoteAmountEntity> noteAmount) {
        for (NoteAmountEntity entity : noteAmount) {
            this.save(entity);
        }
        return "SUCCESS";
    }

    @Override
    public NoteAmountEntity findById(String containerId) {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            pstmt.setString(1, containerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            Logger.error("Error finding NoteAmountEntity by containerId: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void updateToAdd(NoteAmountEntity noteAmount) {
        String selectQuery = "SELECT cashInQuantity, cashOutQuantity FROM " + TABLE_NAME + " WHERE unitAmount = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            // Retrieve current cashInQuantity and cashOutQuantity for the specified unitAmount
            selectStmt.setInt(1, noteAmount.getUnitAmount());
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int currentCashIn = rs.getInt("cashInQuantity");
                int currentCashOut = rs.getInt("cashOutQuantity");

                // Add the passed quantities to the current values
                int newCashIn = currentCashIn + noteAmount.getCashInQuantity();
                int newCashOut = currentCashOut + noteAmount.getCashOutQuantity();
                noteAmount.setCashInQuantity(newCashIn);
                noteAmount.setCashOutQuantity(newCashOut);

                // Prepare updateToAdd query to set the new values
                this.update(noteAmount);
            }
        } catch (SQLException e) {
            Logger.error("Error updating NoteAmountEntity: {}", e.getMessage());
        }
    }

    @Override
    public void updateToAdd(List<NoteAmountEntity> noteAmount) {
        for (NoteAmountEntity entity : noteAmount) {
            this.updateToAdd(entity);
        }
    }


    private void update(NoteAmountEntity noteAmount) {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_SQL)) {
            pstmt.setInt(1, noteAmount.getCashInQuantity());
            pstmt.setInt(2, noteAmount.getCashOutQuantity());
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(4, noteAmount.getUnitAmount());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Error updating NoteAmountEntity: {}", e.getMessage());
        }
    }

    @Override
    public void resetToZero() {
        try (PreparedStatement pstmt = connection.prepareStatement(RESET_TO_ZERO_SQL)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Error resetting NoteAmountEntity quantities to zero: {}", e.getMessage());
        }

    }

    @Override
    public void deleteById(String unitAmount) {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            pstmt.setString(1, unitAmount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Error deleting NoteAmountEntity by unitAmount: {}", e.getMessage());
        }
    }

    @Override
    public List<NoteAmountEntity> findAllFrom(Timestamp from) {
        List<NoteAmountEntity> noteAmounts = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_FROM_SQL)) {
            pstmt.setTimestamp(1, from);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                noteAmounts.add(mapRow(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error fetching NoteAmountEntity list: {}", e.getMessage());
        }
        return noteAmounts;
    }

    // Map ResultSet to NoteAmountEntity
    private NoteAmountEntity mapRow(ResultSet rs) throws SQLException {
        return new NoteAmountEntity()
                .setContainerId(rs.getString("containerId"))
                .setUnitAmount(rs.getInt("unitAmount"))
                .setCashInQuantity(rs.getInt("cashInQuantity"))
                .setCashOutQuantity(rs.getInt("cashOutQuantity"))
                .setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime())
                .setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
    }
}
