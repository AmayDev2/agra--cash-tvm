package com.amay.tvm.backend.repository;

import com.amay.printer.BNRLoadUnload;
import com.amay.printer.PrinterCommandDispatcher;
import com.amay.tom.config.ENVURL;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.database.SQLConnector;
import com.amay.tom.utils.env.EnvLoader;
import com.amay.tom.utils.time.TimeUtil;
import com.amay.tvm.backend.dto.NoteAmountDTO;
import com.amay.tvm.backend.entity.FinanceOperationEntity;
import com.amay.tvm.backend.entity.NoteAmountEntity;
import com.amay.tvm.backend.enums.FinanceOperation;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.backend.mapper.NoteAmountMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FinanceOperationRepositoryImplTest {

    private static FinanceOperationRepository financeOperationRepository;
    private static NoteAmountRepository noteAmountRepository;

    @AfterAll
    static void cleanup() {
//        financeOperationRepository.deleteAll();
        if (financeOperationRepository instanceof FinanceOperationRepositoryImpl repoImpl) {
            try {
                repoImpl.connection.close();
                Logger.tag(LoggerTag.APP).info("Database connection closed.");
            } catch (Exception e) {
                Logger.tag(LoggerTag.APP).error("Error closing connection: " + e.getMessage());
            }
        }
        Logger.tag(LoggerTag.APP).info("Tests completed. Cleaning up resources.");
    }


    @BeforeAll
    static void init() {
        try {
            Connection connection = getConnection();

            noteAmountRepository=new NoteAmountRepositoryImpl(connection);

            financeOperationRepository = new FinanceOperationRepositoryImpl(connection,noteAmountRepository);

            Logger.tag(LoggerTag.APP).info("FinanceOperationRepository initialized");

        } catch (Exception e) {
            fail("Initialization failed: " + e.getMessage());
        }
    }

    private static Connection getConnection() throws InterruptedException {
        EnvLoader envLoader = new EnvLoader(ENVURL.CONFIG + ".env");
        String dbUrl = envLoader.getDatabaseUrl();
        String dbUsername = envLoader.getDatabaseUsername();
        String dbPassword = envLoader.getDatabasePassword2();
        int noOfConnections = envLoader.getSQLiteDatabaseConnections();

        SQLConnector sqlConnector = new SQLConnector(dbUrl, dbUsername, dbPassword, noOfConnections);
        sqlConnector.setConnection();
        return sqlConnector.getConnection();
    }

    @Test
    void upsert_shouldUpsertDeleteRecord() {
        String shiftIdValue = UUID.randomUUID().toString();
        FinanceOperationEntity entity = buildEntity(100, FinanceOperation.BNR_NOT_COMMITTED, 1,shiftIdValue);
        String shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId, "ShiftId should not be null after upsert");
        assertEquals(shiftIdValue, shiftId, "Returned shiftId should match the entity's shiftId");
        entity.setQuantity(2);
        shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId, "ShiftId should not be null after second upsert");
        assertEquals(shiftIdValue, shiftId, "Returned shiftId should match the entity's shiftId after second upsert");
        List<FinanceOperationEntity> entities = financeOperationRepository.findByShiftId(shiftId);
        assertFalse(entities.isEmpty(), "Should find entities for the given shiftId");
        FinanceOperationEntity fetchedEntity = entities.stream()
                .filter(e -> e.getOperationType() == entity.getOperationType() && e.getUnitAmount() == entity.getUnitAmount())
                .findFirst()
                .orElse(null);
        assertNotNull(fetchedEntity, "Fetched entity should not be null");
        assertEquals(3, fetchedEntity.getQuantity(), "Quantity should be cumulative (1 + 2 = 3)");
        financeOperationRepository.deleteByShiftId(shiftId);
        List<FinanceOperationEntity> deletedEntities = financeOperationRepository.findByShiftId(shiftId);
        assertTrue(deletedEntities.isEmpty(), "Entities should be deleted for the given shiftId");
    }


    @Test
    void upsert_shouldUpsertDeleteMultiRecordForSameShiftId() {
        String shiftIdValue = UUID.randomUUID().toString();
        FinanceOperationEntity entity = buildEntity(100, FinanceOperation.BNR_NOT_COMMITTED, 1,shiftIdValue);
        String shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId, "ShiftId should not be null after upsert");
        assertEquals(shiftIdValue, shiftId, "Returned shiftId should match the entity's shiftId");
        entity.setQuantity(2);
        entity.setUnitAmount(10);
        shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId, "ShiftId should not be null after second upsert");
        assertEquals(shiftIdValue, shiftId, "Returned shiftId should match the entity's shiftId after second upsert");


        //3rd entry with same unit amount and operation type as first entry
        entity.setQuantity(4);
        entity.setUnitAmount(50);
        shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId, "ShiftId should not be null after second upsert");
        assertEquals(shiftIdValue, shiftId, "Returned shiftId should match the entity's shiftId after second upsert");

        int quantity=financeOperationRepository.markCommited(FinanceOperation.BNR_DEPOSIT);
        assertEquals(3,quantity,"Total quantity should be 3");


        //3rd entry with same unit amount and operation type as first entry
        entity.setQuantity(4);
        entity.setUnitAmount(20);
        shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId, "ShiftId should not be null after second upsert");
        assertEquals(shiftIdValue, shiftId, "Returned shiftId should match the entity's shiftId after second upsert");


        quantity=financeOperationRepository.rollback();
        assertEquals(1,quantity,"Total quantity should be 3");


        List<FinanceOperationEntity> entities = financeOperationRepository.findByShiftId(shiftId);
        assertFalse(entities.isEmpty(), "Should find entities for the given shiftId");
        FinanceOperationEntity fetchedEntity = entities.stream()
                .filter(e -> e.getOperationType().equals(FinanceOperation.BNR_DEPOSIT) && e.getUnitAmount() == 10)
                .findFirst()
                .orElse(null);

        assertNotNull(fetchedEntity, "Fetched entity should not be null");
        assertEquals(2, fetchedEntity.getQuantity(), "Quantity should be cumulative (1 + 2 = 3)");
        financeOperationRepository.deleteByShiftId(shiftId);
        List<FinanceOperationEntity> deletedEntities = financeOperationRepository.findByShiftId(shiftId);
        assertTrue(deletedEntities.isEmpty(), "Entities should be deleted for the given shiftId");
    }

    @Test
    void upsert_shouldUpsertRecordForSameShiftId() {
        String shiftIdValue = UUID.randomUUID().toString();
        FinanceOperationEntity entity = buildEntity(100, FinanceOperation.BNR_LOAD, 1,shiftIdValue);
        String shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId, "ShiftId should not be null after upsert");
        assertEquals(shiftIdValue, shiftId, "Returned shiftId should match the entity's shiftId");
        entity.setQuantity(2);
        entity.setUnitAmount(10);
        shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId, "ShiftId should not be null after second upsert");
        assertEquals(shiftIdValue, shiftId, "Returned shiftId should match the entity's shiftId after second upsert");

        entity.setQuantity(2);
        entity.setUnitAmount(20);
       financeOperationRepository.upsert(entity);

        entity.setQuantity(2);
        entity.setUnitAmount(200);
        financeOperationRepository.upsert(entity);


        entity.setQuantity(2);
        entity.setUnitAmount(500);
        financeOperationRepository.upsert(entity);

        entity.setQuantity(2);
        entity.setUnitAmount(500);
        financeOperationRepository.upsert(entity);


        entity.setQuantity(2);
        entity.setUnitAmount(1);
        entity.setOperationType(FinanceOperation.COIN_LOAD);
        financeOperationRepository.upsert(entity);

        entity.setQuantity(2);
        entity.setUnitAmount(2);
        financeOperationRepository.upsert(entity);

        entity.setQuantity(2);
        entity.setUnitAmount(3);
        financeOperationRepository.upsert(entity);

        //3rd entry with same unit amount and operation type as first entry
        entity.setQuantity(4);
        entity.setUnitAmount(50);
        shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId, "ShiftId should not be null after second upsert");
        assertEquals(shiftIdValue, shiftId, "Returned shiftId should match the entity's shiftId after second upsert");
//
//        int quantity=financeOperationRepository.markCommited();
//        assertEquals(3,quantity,"Total quantity should be 3");


        //3rd entry with same unit amount and operation type as first entry
        entity.setQuantity(4);
        entity.setUnitAmount(20);
        shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId, "ShiftId should not be null after second upsert");
        assertEquals(shiftIdValue, shiftId, "Returned shiftId should match the entity's shiftId after second upsert");

//
//        quantity=financeOperationRepository.rollback();
//        assertEquals(1,quantity,"Total quantity should be 3");


        List<FinanceOperationEntity> entities = financeOperationRepository.findByShiftId(shiftId);
        assertFalse(entities.isEmpty(), "Should find entities for the given shiftId");
        FinanceOperationEntity fetchedEntity = entities.stream()
                .filter(e -> e.getOperationType().equals(FinanceOperation.BNR_LOAD) && e.getUnitAmount() == 10)
                .findFirst()
                .orElse(null);

//        assertNotNull(fetchedEntity, "Fetched entity should not be null");
//        assertEquals(2, fetchedEntity.getQuantity(), "Quantity should be cumulative (1 + 2 = 3)");
//        financeOperationRepository.deleteByShiftId(shiftId);

//        List<FinanceOperationEntity> deletedEntities = financeOperationRepository.findByShiftId(shiftId);
//        assertTrue(deletedEntities.isEmpty(), "Entities should be deleted for the given shiftId");
    }

    @Test
    void upsert_shouldUpsertDeleteMultiRecordForDiffShiftId() {
        String shiftIdValue = UUID.randomUUID().toString();
        FinanceOperationEntity entity = buildEntity(100, FinanceOperation.BNR_NOT_COMMITTED, 1,shiftIdValue);
        String shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId, "ShiftId should not be null after upsert");
        assertEquals(shiftIdValue, shiftId, "Returned shiftId should match the entity's shiftId");
        entity.setQuantity(2);
        entity.setUnitAmount(10);
        shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId, "ShiftId should not be null after second upsert");
        assertEquals(shiftIdValue, shiftId, "Returned shiftId should match the entity's shiftId after second upsert");


        //3rd entry with same unit amount and operation type as first entry
        entity.setQuantity(4);
        entity.setUnitAmount(50);
        shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId, "ShiftId should not be null after second upsert");
        assertEquals(shiftIdValue, shiftId, "Returned shiftId should match the entity's shiftId after second upsert");



        int quantity=financeOperationRepository.markCommited(FinanceOperation.BNR_DEPOSIT);
        assertEquals(3,quantity,"Total quantity should be 3");


        //3rd entry with same unit amount and operation type as first entry
        entity.setQuantity(4);
        entity.setUnitAmount(20);
        shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId, "ShiftId should not be null after second upsert");
        assertEquals(shiftIdValue, shiftId, "Returned shiftId should match the entity's shiftId after second upsert");


        quantity=financeOperationRepository.rollback();
        assertEquals(1,quantity,"Total quantity should be 3");


        List<FinanceOperationEntity> entities = financeOperationRepository.findByShiftId(shiftId);
        assertFalse(entities.isEmpty(), "Should find entities for the given shiftId");
        FinanceOperationEntity fetchedEntity = entities.stream()
                .filter(e -> e.getOperationType().equals(FinanceOperation.BNR_DEPOSIT) && e.getUnitAmount() == 10)
                .findFirst()
                .orElse(null);

        assertNotNull(fetchedEntity, "Fetched entity should not be null");
        assertEquals(2, fetchedEntity.getQuantity(), "Quantity should be cumulative (1 + 2 = 3)");
        financeOperationRepository.deleteByShiftId(shiftId);
        List<FinanceOperationEntity> deletedEntities = financeOperationRepository.findByShiftId(shiftId);
        assertTrue(deletedEntities.isEmpty(), "Entities should be deleted for the given shiftId");
    }


    @Test
    void upsert_shouldHandleLargeQuantitiesWithLoadDispenseDeposit_usingDTOs() {
        String shiftIdValue = UUID.randomUUID().toString();
        noteAmountRepository.resetToZero();

        // --- 1) LOAD large quantities ---
        FinanceOperationEntity entity = buildEntity(100, FinanceOperation.BNR_LOAD, 1000, shiftIdValue);
        String shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId);
        assertEquals(shiftIdValue, shiftId);

        entity.setUnitAmount(200);
        entity.setQuantity(800);
        financeOperationRepository.upsert(entity);

        entity.setUnitAmount(500);
        entity.setQuantity(600);
        financeOperationRepository.upsert(entity);

        financeOperationRepository.markCommited(FinanceOperation.BNR_LOAD);

        // Map to DTOs for assertions
        NoteAmountDTO dto100 = NoteAmountMapper.toDto(noteAmountRepository.findById(100));
        NoteAmountDTO dto200 = NoteAmountMapper.toDto(noteAmountRepository.findById(200));
        NoteAmountDTO dto500 = NoteAmountMapper.toDto(noteAmountRepository.findById(500));

        Logger.tag(LoggerTag.APP).info("After LOAD: {} {} {}", dto100, dto200, dto500);

        assertEquals(1000, dto100.getCurrentQuantity(), "100 notes should have been loaded");
        assertEquals(800, dto200.getCurrentQuantity(), "200 notes should have been loaded");
        assertEquals(600, dto500.getCurrentQuantity(), "500 notes should have been loaded");

        // --- 2) DISPENSE large quantities ---
        entity.setOperationType(FinanceOperation.BNR_DISPENSE);
        entity.setUnitAmount(100);
        entity.setQuantity(400);
        financeOperationRepository.upsert(entity);

        entity.setUnitAmount(200);
        entity.setQuantity(300);
        financeOperationRepository.upsert(entity);

        entity.setUnitAmount(500);
        entity.setQuantity(200);
        financeOperationRepository.upsert(entity);

        financeOperationRepository.markCommited(FinanceOperation.BNR_DISPENSE);

        // Re-fetch DTOs
        dto100 = NoteAmountMapper.toDto(noteAmountRepository.findById(100));
        dto200 = NoteAmountMapper.toDto(noteAmountRepository.findById(200));
        dto500 = NoteAmountMapper.toDto(noteAmountRepository.findById(500));

        Logger.tag(LoggerTag.APP).info("After DISPENSE: {} {} {}", dto100, dto200, dto500);

        assertEquals(600, dto100.getCurrentQuantity(), "100 notes should reduce after dispense");
        assertEquals(500, dto200.getCurrentQuantity(), "200 notes should reduce after dispense");
        assertEquals(400, dto500.getCurrentQuantity(), "500 notes should reduce after dispense");

        // --- 3) DEPOSIT large quantities ---
        entity.setOperationType(FinanceOperation.BNR_DEPOSIT);
        entity.setUnitAmount(100);
        entity.setQuantity(150);
        financeOperationRepository.upsert(entity);

        entity.setUnitAmount(200);
        entity.setQuantity(100);
        financeOperationRepository.upsert(entity);

        entity.setUnitAmount(500);
        entity.setQuantity(250);
        financeOperationRepository.upsert(entity);

        financeOperationRepository.markCommited(FinanceOperation.BNR_DEPOSIT);

        // Re-fetch DTOs
        dto100 = NoteAmountMapper.toDto(noteAmountRepository.findById(100));
        dto200 = NoteAmountMapper.toDto(noteAmountRepository.findById(200));
        dto500 = NoteAmountMapper.toDto(noteAmountRepository.findById(500));

        Logger.tag(LoggerTag.APP).info("After DEPOSIT: {} {} {}", dto100, dto200, dto500);

        assertEquals(750, dto100.getCurrentQuantity(), "100 notes should increase after deposit");
        assertEquals(600, dto200.getCurrentQuantity(), "200 notes should increase after deposit");
        assertEquals(650, dto500.getCurrentQuantity(), "500 notes should increase after deposit");

        // --- Final consistency check using DTOs ---
        int totalAmount =
                dto100.getCurrentQuantity() * dto100.getUnitAmount()
                        + dto200.getCurrentQuantity() * dto200.getUnitAmount()
                        + dto500.getCurrentQuantity() * dto500.getUnitAmount();

        Logger.tag(LoggerTag.APP).info("Final Total Amount: {}", totalAmount);
        assertTrue(totalAmount > 0, "Total amount should be positive after all operations");

        financeOperationRepository.markEmpty(shiftId);


    }


    // Helper to build a test entity
    private FinanceOperationEntity buildEntity(int unitAmount, FinanceOperation operationType, int quantity, String shiftId) {
        Timestamp now = Timestamp.from(Instant.now());
        FinanceOperationEntity entity = new FinanceOperationEntity();
        entity.setShiftId(shiftId);
        entity.setUnitAmount(unitAmount);
        entity.setOperationType(operationType);
        entity.setQuantity(quantity);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return entity;
    }
}
