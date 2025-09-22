package com.amay.tvm.backend.repository;


import com.amay.tvm.backend.entity.TransactionEntity;
import com.amay.tvm.backend.enums.TransactionStatus;
import com.amay.tvm.backend.enums.TransactionSubStatus;
import org.tinylog.Logger;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepositoryImpl extends TransactionRepository {
    private Connection connection;

    public TransactionRepositoryImpl(Connection connection) {
        try {
            this.connection = connection;
            this.createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void createTableIfNotExists() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String save(TransactionEntity TransactionEntity) {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL)) {
            pstmt.setString(1, TransactionEntity.getId());
            pstmt.setString(2, TransactionEntity.getStatus().name());
            pstmt.setString(3, TransactionEntity.getSubStatus().name());
            pstmt.setString(4, TransactionEntity.getOrderId());
            pstmt.setString(5, TransactionEntity.getPaymentMode());
            pstmt.setString(6, TransactionEntity.getTransactionType());
            pstmt.setTimestamp(7, new Timestamp(Instant.now().toEpochMilli()));
            pstmt.setTimestamp(8, new Timestamp(Instant.now().toEpochMilli()));
            pstmt.setInt(9, TransactionEntity.getAmount());
            pstmt.executeUpdate();
            return TransactionEntity.getId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TransactionEntity findById(String transactionUniqueId) {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            pstmt.setString(1, transactionUniqueId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(TransactionEntity TransactionEntity) {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_SQL)) {
            pstmt.setString(1, TransactionEntity.getStatus().name());
            pstmt.setString(2, TransactionEntity.getSubStatus().name());
            pstmt.setString(3, TransactionEntity.getTransactionId());
            pstmt.setString(4, TransactionEntity.getOrderId());
            pstmt.setString(5, TransactionEntity.getPaymentMode());
            pstmt.setString(6, TransactionEntity.getTransactionType());
            pstmt.setTimestamp(7, new Timestamp(Instant.now().toEpochMilli()));
            pstmt.setLong(8, TransactionEntity.getTransactionCompleteTime());
            pstmt.setInt(9, TransactionEntity.getAmount());
            pstmt.setString(10, TransactionEntity.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void deleteById(String transactionUniqueId) {
//        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_BY_ID_SQL)) {
//            pstmt.setString(1, transactionUniqueId);
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public List<TransactionEntity> findAllFrom(Timestamp from) {
        List<TransactionEntity> transactions = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_FROM_SQL)) {
            pstmt.setTimestamp(1, from);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapRow(rs));
            }
        } catch (SQLException e) {
            Logger.debug("Error executing query: {}", e.getMessage());
        }
        return transactions;
    }

    @Override
    public boolean verify(String orderId, String transactionId, int amount) {
        boolean isValid = false;
        try (PreparedStatement pstmt = connection.prepareStatement(VERIFY_PAYMENT_TRANSACTION)) {
            // assuming your query has 4 placeholders: status, orderId, transactionId, amount
            pstmt.setString(1, TransactionStatus.SUCCESS.name());
            pstmt.setString(2, orderId);
            pstmt.setString(3, transactionId);
            pstmt.setInt(4, amount);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    isValid= rs.getBoolean("isValid"); // <-- column alias from query
                }
            }
        } catch (SQLException e) {
            Logger.debug("Error executing query: {}", e.getMessage(), e);
        }
        return isValid;
    }

    private TransactionEntity mapRow(ResultSet rs) throws SQLException {
        TransactionEntity TransactionEntity = new TransactionEntity();
        TransactionEntity.setId(rs.getString("transactionUniqueId"));
        TransactionEntity.setStatus(TransactionStatus.valueOf(rs.getString("status")));
        TransactionEntity.setSubStatus(TransactionSubStatus.valueOf(rs.getString("subStatus")));
        TransactionEntity.setTransactionId(rs.getString("transactionId"));
        TransactionEntity.setOrderId(rs.getString("orderId"));
        TransactionEntity.setPaymentMode(rs.getString("paymentMode"));
        TransactionEntity.setTransactionType(rs.getString("transactionType"));
        TransactionEntity.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
        TransactionEntity.setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
        TransactionEntity.setTransactionCompleteTime(rs.getLong("transactionCompleteTime"));
        TransactionEntity.setAmount(rs.getInt("amount"));
        return TransactionEntity;
    }
}
