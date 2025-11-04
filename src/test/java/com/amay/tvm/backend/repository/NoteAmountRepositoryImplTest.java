package com.amay.tvm.backend.repository;

import com.amay.tom.config.ENVURL;
import com.amay.tom.database.SQLConnector;
import com.amay.tom.utils.env.EnvLoader;
import com.amay.tvm.backend.entity.NoteAmountEntity;
import com.amay.tvm.backend.enums.ContainerId;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.backend.mapper.NoteAmountMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoteAmountRepositoryImplTest {


    private static NoteAmountRepository noteAmountRepository;

    @AfterAll
    static void cleanup() {
        noteAmountRepository.resetToZero(); // Ensure a clean state before the test
        if (noteAmountRepository instanceof NoteAmountRepository repoImpl) {
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

            noteAmountRepository= new NoteAmountRepositoryImpl(sqlConnector.getConnection());

            Logger.tag(LoggerTag.APP).info("NoteAmountRepository initialized");

        } catch (Exception e) {
            fail("Initialization failed: " + e.getMessage());
        }
    }

    @Test
    void testSaveAndFindByDenomination() {
        try {
            noteAmountRepository.deleteAll(); // Ensure a clean state before the test
            NoteAmountEntity entity = createSampleEntity(100, 0, 0);
            noteAmountRepository.updateToAdd(entity);
            entity = createSampleEntity(50, 0, 0);
            noteAmountRepository.updateToAdd(entity);
            entity = createSampleEntity(20, 0, 0);
            noteAmountRepository.updateToAdd(entity);
            entity = createSampleEntity(10, 0, 0);
            noteAmountRepository.updateToAdd(entity);
            entity = createSampleEntity(500, 0, 0);
            noteAmountRepository.updateToAdd(entity);
            entity = createSampleEntity(200, 0, 0);
            noteAmountRepository.updateToAdd(entity);


            noteAmountRepository.updateToAdd(entity);
            NoteAmountEntity retrieved = noteAmountRepository.findById(100);
            assertNotNull(retrieved, "Retrieved entity should not be null");
            assertEquals(100, retrieved.getUnitAmount(), "Denomination should match");
            assertEquals(0, retrieved.getCashInQuantity(), "Cash-in quantity should match");
            assertEquals(0, retrieved.getCashOutQuantity(), "Cash-out quantity should match");
            Logger.tag(LoggerTag.APP).info(" {}", NoteAmountMapper.toDto(retrieved));
        } catch (Exception e) {
            fail("Exception during testSaveAndFindByDenomination: " + e.getMessage());
        }
    }


    @Test
    void testMultipleSaveAndFindByDenomination() {
        try {
            noteAmountRepository.resetToZero(); // Ensure a clean state before the test
            List<NoteAmountEntity> allEntities = new ArrayList<>(List.of());
            allEntities.add(createSampleEntity(100, 5, 2));
            allEntities.add(createSampleEntity(200, 4, 1));
            allEntities.add(createSampleEntity(500, 3, 0));
            allEntities.add(createSampleEntity(1000, 2, 1));
            allEntities.add(createSampleEntity(2000, 1, 0));
            allEntities.add(createSampleEntity(5000, 0, 0));
            allEntities.add(createSampleEntity(10000, 0, 0));
            allEntities.add(createSampleEntity(5000, 2, 0));
            allEntities.add(createSampleEntity(10000, 0, 9));
            noteAmountRepository.updateToAdd(allEntities);

            NoteAmountEntity retrieved = noteAmountRepository.findById(1000);
            assertNotNull(retrieved, "Retrieved entity should not be null");
            assertEquals(1000, retrieved.getUnitAmount(), "Denomination should match");
            assertEquals(2, retrieved.getCashInQuantity(), "Cash-in quantity should match");
            assertEquals(1, retrieved.getCashOutQuantity(), "Cash-out quantity should match");
            Logger.tag(LoggerTag.APP).info(" {}", NoteAmountMapper.toDto(retrieved));
        } catch (Exception e) {
            fail("Exception during testSaveAndFindByDenomination: " + e.getMessage());
        }
    }

    @Test
    void testSaveAndFindAll() {
        try {
            noteAmountRepository.deleteAll(); // Ensure a clean state before the test
            NoteAmountEntity entity = createSampleEntity(100, 5, 2);
            noteAmountRepository.updateToAdd(entity);
            List<NoteAmountEntity> retrieved = noteAmountRepository.findAll();
            assertNotNull(retrieved, "Retrieved entity should not be null");
            assertFalse(retrieved.isEmpty(), "Retrieved list should not be empty");
            assertEquals(1, retrieved.size(), "There should be one entity in the list");
            assertEquals(100, retrieved.getFirst().getUnitAmount(), "Denomination should match");
            assertEquals(5, retrieved.getFirst().getCashInQuantity(), "Cash-in quantity should match");
            assertEquals(2, retrieved.getFirst().getCashOutQuantity(), "Cash-out quantity should match");
            Logger.tag(LoggerTag.APP).info(" {}", NoteAmountMapper.toDtoList(retrieved));
        } catch (Exception e) {
            fail("Exception during testSaveAndFindByDenomination: " + e.getMessage());
        }
    }

    private NoteAmountEntity createSampleEntity(int denomination, int cashInQuantity, int cashOutQuantity) {
        NoteAmountEntity entity = new NoteAmountEntity();
        entity.setContainerId(ContainerId.CB);
        entity.setUnitAmount(denomination);
        entity.setCashInQuantity(cashInQuantity);
        entity.setCashOutQuantity(cashOutQuantity);
        return entity;
    }

}