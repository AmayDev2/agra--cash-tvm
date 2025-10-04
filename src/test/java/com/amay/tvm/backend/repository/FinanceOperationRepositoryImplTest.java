package com.amay.tvm.backend.repository;

import com.amay.tom.config.ENVURL;
import com.amay.tom.database.SQLConnector;
import com.amay.tom.utils.env.EnvLoader;
import com.amay.tvm.backend.entity.FinanceOperationEntity;
import com.amay.tvm.backend.enums.FinanceOperation;
import com.amay.tvm.backend.enums.LoggerTag;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FinanceOperationRepositoryImplTest {

    private static FinanceOperationRepository financeOperationRepository;

    @AfterAll
    static void cleanup() {
        financeOperationRepository.deleteAll();
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
            EnvLoader envLoader = new EnvLoader(ENVURL.CONFIG + ".env");
            String dbUrl = envLoader.getDatabaseUrl();
            String dbUsername = envLoader.getDatabaseUsername();
            String dbPassword = envLoader.getDatabasePassword2();
            int noOfConnections = envLoader.getSQLiteDatabaseConnections();

            SQLConnector sqlConnector = new SQLConnector(dbUrl, dbUsername, dbPassword, noOfConnections);
            sqlConnector.setConnection();

            financeOperationRepository = new FinanceOperationRepositoryImpl(sqlConnector.getConnection());

            Logger.tag(LoggerTag.APP).info("FinanceOperationRepository initialized");

        } catch (Exception e) {
            fail("Initialization failed: " + e.getMessage());
        }
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

        int quantity=financeOperationRepository.markCommited();
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

/*    @Test
    void upsert_shouldUpdateRecord() {
        FinanceOperationEntity entity = buildEntity();
        String shiftId = financeOperationRepository.upsert(entity);
        assertNotNull(shiftId);

        // Increase quantity for update test
        entity.setQuantity(5);
        String updatedId = financeOperationRepository.upsert(entity);
        assertEquals(shiftId, updatedId);

        // Fetch all entries for this shiftId and find matching record to check quantity
        List<FinanceOperationEntity> updatedEntities = financeOperationRepository.findByShiftId(shiftId);

        // Find the entity with the matching operationType and unitAmount
        FinanceOperationEntity updatedEntity = updatedEntities.stream()
                .filter(e -> e.getOperationType() == entity.getOperationType() && e.getUnitAmount() == entity.getUnitAmount())
                .findFirst()
                .orElse(null);

        assertNotNull(updatedEntity, "Updated entity should be found");
        assertEquals(6, updatedEntity.getQuantity(), "Quantity should be cumulative (1 + 5 = 6)");
    }

    @Test
    void findByShiftId_shouldReturnEntity() {
        FinanceOperationEntity entity = buildEntity();
        String shiftId = financeOperationRepository.upsert(entity);

        List<FinanceOperationEntity> found = financeOperationRepository.findByShiftId(shiftId);
        assertFalse(found.isEmpty(), "Should find at least one entity");
        found.forEach(System.out::println);
    }

    @Test
    void findAll_shouldReturnList() {
        FinanceOperationEntity entity1 = buildEntity();
        FinanceOperationEntity entity2 = buildEntity();
        financeOperationRepository.upsert(entity1);
        financeOperationRepository.upsert(entity2);

        List<FinanceOperationEntity> all = financeOperationRepository.findAll();
        assertFalse(all.isEmpty());
        all.forEach(System.out::println);
    }
//
    @Test
    void deleteByShiftId_shouldRemoveRecord() {
        FinanceOperationEntity entity = buildEntity();
        String shiftId = financeOperationRepository.upsert(entity);

        financeOperationRepository.deleteByShiftId(shiftId);
        List<FinanceOperationEntity> deleted = financeOperationRepository.findByShiftId(shiftId);
        assertEquals(0, deleted.size());
    }*/
//
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
