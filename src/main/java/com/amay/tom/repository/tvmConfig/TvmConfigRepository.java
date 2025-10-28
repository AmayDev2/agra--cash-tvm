package com.amay.tom.repository.tvmConfig;

import com.amay.tom.model.tvmConfig.TvmConfigEntity;
import java.util.List;

public abstract class TvmConfigRepository {

    protected static final String TABLE_NAME = "Tvm_Config";

    protected static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY, " +
                    "configVer VARCHAR(50), " +
                    "maxChangeDispense INT, " +
                    "maxCoinDispensedQuantity INT, " +
                    "maxCoinDispensedTotalQuantity INT, " +
                    "maxBnrAcceptAmount INT, " +
                    "maxBnrDispensedAmount INT, " +
                    "maxBnrDispensedQuantity INT, " +
                    "maxBnrDispensedTotalQuantity INT, " +
                    "hopper1UnitAmount INT, " +
                    "hopper2UnitAmount INT, " +
                    "hopper3UnitAmount INT, " +
                    "transactionTimeout INT, " +
                    "paymentScreenTimeout INT, " +
                    "idleScreenTimeout INT, " +
                    "bnrEnabled BOOLEAN, " +
                    "coinDispenserEnabled BOOLEAN, " +
                    "posEnabled BOOLEAN, " +
                    "upiEnabled BOOLEAN" +
                    ")";

    protected static final String INSERT_SQL =
            "INSERT INTO " + TABLE_NAME + " (" +
                    "id, configVer, maxChangeDispense, maxCoinDispensedQuantity, maxCoinDispensedTotalQuantity, " +
                    "maxBnrAcceptAmount, maxBnrDispensedAmount, maxBnrDispensedQuantity, maxBnrDispensedTotalQuantity, " +
                    "hopper1UnitAmount, hopper2UnitAmount, hopper3UnitAmount, " +
                    "transactionTimeout, paymentScreenTimeout, idleScreenTimeout, " +
                    "bnrEnabled, coinDispenserEnabled, posEnabled, upiEnabled" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    protected static final String SELECT_BY_ID_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

    protected static final String SELECT_ALL_SQL =
            "SELECT * FROM " + TABLE_NAME;

    protected static final String DELETE_BY_ID_SQL =
            "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

    protected static final String DELETE_ALL_SQL =
            "DELETE FROM " + TABLE_NAME;

    // Abstract methods
    public abstract void insert(TvmConfigEntity entity);

    public abstract TvmConfigEntity findById(int id);

    public abstract TvmConfigEntity findTVMConfig();

    public abstract List<TvmConfigEntity > findAll();

    public abstract void deleteById(int id);

    public abstract void deleteAll();
}
