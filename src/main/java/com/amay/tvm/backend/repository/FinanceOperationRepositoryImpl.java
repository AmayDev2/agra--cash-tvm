package com.amay.tvm.backend.repository;

import com.amay.tvm.backend.entity.FinanceOperationEntity;
import com.amay.tvm.backend.entity.NoteAmountEntity;
import com.amay.tvm.backend.enums.ContainerId;
import com.amay.tvm.backend.enums.FinanceOperation;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.backend.mapper.NoteAmountMapper;
import org.tinylog.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FinanceOperationRepositoryImpl extends FinanceOperationRepository {

    public FinanceOperationRepositoryImpl(Connection connection) {
        try {
            this.connection = connection;
            this.createTableIfNotExists();
        } catch (SQLException e) {
            Logger.error("Error initializing FinanceOperationRepositoryImpl: {}", e.getMessage());
        }
    }


    public FinanceOperationRepositoryImpl(Connection connection,NoteAmountRepository noteAmountRepository) {
        this(connection);
        this.noteAmountRepository=noteAmountRepository;
    }



    @Override
    protected void createTableIfNotExists() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            Logger.error("Error creating table for FinanceOperationEntity: {}", e.getMessage());
            throw new SQLException("Error creating table for FinanceOperationEntity", e);
        }
    }

    @Override
    public String upsert(FinanceOperationEntity entity) {
        try {
            connection.setAutoCommit(false);
            // 1. Try to update existing record first
            try (PreparedStatement updateStmt = connection.prepareStatement(UPDATE_SQL)) {
                updateStmt.setInt(1, entity.getQuantity());
                updateStmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                updateStmt.setString(3, entity.getShiftId());
                updateStmt.setString(4, entity.getOperationType().name());
                updateStmt.setInt(5, entity.getUnitAmount());

                int affectedRows = updateStmt.executeUpdate();
                if (affectedRows > 0) {
                    // Updated existing record successfully
                    Logger.tag(LoggerTag.BUSS).info("Updated FinanceOperationEntity: {}", entity);
                    return entity.getShiftId();
                }
            }

            // 2. If update affected 0 rows, insert new record

            try (PreparedStatement insertStmt = connection.prepareStatement(INSERTED_SQL)) {
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());

                insertStmt.setString(1, entity.getShiftId());
                insertStmt.setString(2, entity.getOperationType().name());
                insertStmt.setInt(3, entity.getUnitAmount());
                insertStmt.setInt(4, entity.getQuantity());
                insertStmt.setTimestamp(5, now);
                insertStmt.setTimestamp(6, now);

                insertStmt.executeUpdate();
                Logger.tag(LoggerTag.BUSS).info("Inserted FinanceOperationEntity: {}", entity);
            }

            updateNoteAmountData(entity);
            connection.commit();

            return entity.getShiftId();
        } catch (SQLException e) {

            Logger.tag(LoggerTag.APP).error("Error performing upsert for FinanceOperationEntity: {}", e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException ignored) {

            }
            return null;
        }
    }

    private void updateNoteAmountData(FinanceOperationEntity entity) {
        NoteAmountEntity noteAmountEntity=switch (entity.getOperationType()) {
            case BNR_DEPOSIT, BNR_LOAD ->
                        new NoteAmountEntity()
                                .setContainerId(ContainerId.CB)
                                .setUnitAmount(entity.getUnitAmount())
                                .setCashInQuantity(entity.getQuantity()
                );

            case BNR_UNLOAD, BNR_DISPENSE ->
                        new NoteAmountEntity()
                                .setContainerId(ContainerId.CB)
                                .setUnitAmount(entity.getUnitAmount())
                                .setCashOutQuantity(entity.getQuantity()
                );

            case COIN_LOAD ->
                        new NoteAmountEntity()
                                .setContainerId(ContainerId.CM)
                                .setUnitAmount(entity.getUnitAmount())
                                .setCashInQuantity(entity.getQuantity()
                );

            case COIN_UNLOAD, COIN_DISPENSE ->
                            new NoteAmountEntity()
                                    .setContainerId(ContainerId.CM)
                                    .setUnitAmount(entity.getUnitAmount())
                                    .setCashOutQuantity(entity.getQuantity()
                    );
            default -> null;
        };

        Logger.tag(LoggerTag.BUSS).info("Note Amount Data To Update {}",noteAmountEntity);
        if(null==noteAmountEntity)return;
        noteAmountRepository.updateToAdd(List.of(noteAmountEntity));

    }


    @Override
    public String upsert(List<FinanceOperationEntity> entities) {
        for (FinanceOperationEntity entity : entities) {
            this.upsert(entity);
        }
        return "SUCCESS";
    }

    @Override
    public List<FinanceOperationEntity> findByShiftId(String shiftId) {
        List<FinanceOperationEntity> results = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_SHIFT_SQL)) {
            pstmt.setString(1, shiftId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error fetching FinanceOperationEntity by shiftId: {}", e.getMessage());
        }
        return results;
    }

    @Override
    public List<FinanceOperationEntity> findAllFrom(Timestamp from) {
        List<FinanceOperationEntity> results = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_FROM_SQL)) {
            pstmt.setTimestamp(1, from);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error fetching all FinanceOperationEntity records: {}", e.getMessage());
        }
        return results;
    }



    @Override
    public int markCommited(FinanceOperation financeOperation) {
        int processedCount = 0;
        try {
            connection.setAutoCommit(false);

            // 1. Select all rows with BNR_NOT_COMMITTED
            try (PreparedStatement selectStmt = connection.prepareStatement(SELECT_NOT_COMMITED)) {
                selectStmt.setString(1, FinanceOperation.BNR_NOT_COMMITTED.name());
//                List<NoteAmountEntity> noteAmountList=new ArrayList<>();

                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        FinanceOperationEntity entity = new FinanceOperationEntity();

                        entity.setShiftId(rs.getString("shiftId"));
                        entity.setOperationType(financeOperation); // Change to deposit status
                        entity.setUnitAmount(rs.getInt("unitAmount"));
                        entity.setQuantity(rs.getInt("quantity"));
                        entity.setUpdatedAt(rs.getTimestamp("updatedAt"));
//                        noteAmountList.add(new NoteAmountEntity().setUnitAmount(entity.getUnitAmount()).setCashInQuantity(entity.getQuantity()));

                        // 2. Upsert with quantity addition into BNR_DEPOSIT status
                        upsert(entity);
                        processedCount++;
                    }
                }
//                this.noteAmountRepository.updateToAdd(noteAmountList);
            }

            // 3. Delete all BNR_NOT_COMMITTED records now that they are "rolled back"
            rollback();

            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                Logger.tag(LoggerTag.APP).error("Rollback failed: {}", rollbackEx.getMessage());
            }
            Logger.tag(LoggerTag.APP).error("Rollback operation failed: {}", e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                Logger.tag(LoggerTag.APP).error("Failed to reset auto-commit: {}", e.getMessage());
            }
        }

        return processedCount;
    }

    @Override
    public int rollback() {
        int deletedCount = 0;
        try (PreparedStatement deleteStmt = connection.prepareStatement(ROLLBACK_SQL)) {
            deleteStmt.setString(1, FinanceOperation.BNR_NOT_COMMITTED.name());
            deletedCount = deleteStmt.executeUpdate();
        } catch (SQLException e) {
            Logger.tag(LoggerTag.APP).error("Rollback operation failed: {}", e.getMessage());
        }
        return deletedCount;
    }

    @Override
    public List<FinanceOperationEntity> findAll() {
        List<FinanceOperationEntity> results = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_SQL)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error fetching all FinanceOperationEntity records: {}", e.getMessage());
        }
        return results;
    }

    @Override
    public void deleteByShiftId(String shiftId) {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_BY_SHIFT_SQL)) {
            pstmt.setString(1, shiftId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Error deleting FinanceOperationEntity by shiftId: {}", e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_ALL)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Error deleting all FinanceOperationEntity records: {}", e.getMessage());
        }
    }

    @Override
    public void markEmpty(String shiftId) {
//SUG: MAY BE REQUIRED MANUAL COMMIT
            this.upsert(NoteAmountMapper.toFinanceOperationEntityList(NoteAmountMapper.toDtoList(this.noteAmountRepository.findAll()), shiftId));
            this.noteAmountRepository.resetToZero();
    }

    @Override
    public List<FinanceOperationEntity> getByShiftIdAndOperationType(String shiftId, String operationType) {
        List<FinanceOperationEntity> financeOperationEntityList = new ArrayList<>();
        try(PreparedStatement psmt = connection.prepareStatement(SELECT_ALL_BY_OPERATION_TYPE_AND_SHIFT_ID)) {
            psmt.setString(1,operationType);
            psmt.setString(2,shiftId);
            ResultSet resultSet = psmt.executeQuery();

            while(resultSet!=null && resultSet.next()) {
                FinanceOperationEntity financeOperationEntity = new FinanceOperationEntity();
                financeOperationEntity.setShiftId(resultSet.getString(1));
                financeOperationEntity.setOperationType(FinanceOperation.valueOf(resultSet.getString(2)));
                financeOperationEntity.setUnitAmount(resultSet.getInt(3));
                financeOperationEntity.setQuantity(resultSet.getInt(4));
                financeOperationEntity.setCreatedAt(resultSet.getTimestamp(5));
                financeOperationEntity.setUpdatedAt(resultSet.getTimestamp(6));
                financeOperationEntityList.add(financeOperationEntity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return financeOperationEntityList;
    }

    @Override
    public List<FinanceOperationEntity> getLoadUnloadOperationByShiftId(String shiftId) {
        List<FinanceOperationEntity> list = new ArrayList<>();

        try (PreparedStatement psmt = connection.prepareStatement(SELECT_ALL_LOAD_UNLOAD_OPERATION_BY_SHIFT_ID)) {
            psmt.setString(1, shiftId);
            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            Logger.error("Error fetching load/unload operations for shiftId={}", shiftId, e);
        }

        return list;
    }



    private FinanceOperationEntity mapRow(ResultSet rs) throws SQLException {
        return new FinanceOperationEntity()
                .setShiftId(rs.getString("shiftId"))
                .setOperationType(FinanceOperation.valueOf(rs.getString("operationType")))
                .setUnitAmount(rs.getInt("unitAmount"))
                .setQuantity(rs.getInt("quantity"))
                .setCreatedAt(rs.getTimestamp("createdAt"))
                .setUpdatedAt(rs.getTimestamp("updatedAt"));
    }
}
