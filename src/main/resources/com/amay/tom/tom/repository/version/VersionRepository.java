package com.amay.tom.repository.version;

import com.amay.tom.model.version.MasterConfigInfoEntity;

public abstract class VersionRepository {

        protected static final String TABLE_NAME = "Master_Config_Info";

        protected static final String CREATE_TABLE_SQL =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        "configVer VARCHAR(50) PRIMARY KEY, " +
                        "tomConfig VARCHAR(255), " +
                        "agConfig VARCHAR(255), " +
                        "tvmConfig VARCHAR(255), " +
                        "trConfig VARCHAR(255), " +
                        "ticketConfig VARCHAR(255), " +
                        "businessDayVer VARCHAR(255), " +
                        "peakTimeVer VARCHAR(255), " +
                        "calenderConfig VARCHAR(255), " +
                        "fareConfig VARCHAR(255), " +
                        "topologyConfig VARCHAR(255), " +
                        "userVer VARCHAR(255), " +
                        "profileVer VARCHAR(255), " +
                        "tomSwVer VARCHAR(255), " +
                        "agSwVer VARCHAR(255), " +
                        "tvmSwVer VARCHAR(255), " +
                        "trSwVer VARCHAR(255), " +
                        "scSwVer VARCHAR(255), " +
                        "productConfig VARCHAR(255)"+
                        ")";

        protected static final String INSERT_SQL =
                "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    protected static final String SELECT_BY_ID_SQL =
                "SELECT * FROM " + TABLE_NAME + " WHERE configVer = ?";

        protected static final String SELECT_ALL_SQL =
                "SELECT * FROM " + TABLE_NAME;

        protected static final String DELETE_BY_ID_SQL =
                "DELETE FROM " + TABLE_NAME + " WHERE configVer = ?";

    protected static final String DELETE_ALL_SQL =
            "DELETE FROM master_config_info";


    public abstract void insert(MasterConfigInfoEntity entity) throws RuntimeException;

    public abstract MasterConfigInfoEntity findByConfigVer(String configVer);
        public abstract java.util.List<MasterConfigInfoEntity> findAll();
        public abstract void deleteByConfigVer(String configVer);

    public abstract void deleteAll();
}
