package com.amay.tom.utils.env;

import com.amay.tom.model.equipment.dto.EquipmentPrivilegeDto;
import com.amay.tom.model.equipment.entity.EquipmentPrivilege;
import io.github.cdimascio.dotenv.Dotenv;

import static com.amay.tom.config.Env.*; // static import for constants

public class EnvLoader {

    private Dotenv dotenv = null;

    public EnvLoader(String envPath) {
        try {
            dotenv = Dotenv.configure().directory(envPath).load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getOrDefault(String key, String defaultValue) {
        String value = dotenv.get(key);
        return value != null ? value : defaultValue;
    }

    private int getOrDefaultInt(String key, int defaultValue) {
        String value = dotenv.get(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    private boolean getOrDefaultBoolean(String key, boolean defaultValue) {
        String value = dotenv.get(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public String getEnvValue(String key) {
        return dotenv.get(key);
    }

    public String getDatabaseUrl() {
        return getOrDefault("DATABASE_URL", DATABASE_URL);
    }

    public String getDatabaseUsername() {
        return getOrDefault("DATABASE_USERNAME", DATABASE_USERNAME);
    }

    public String getDatabasePassword() {
        return getOrDefault("DATABASE_PASSWORD", DATABASE_PASSWORD);
    }

    public String getDatabasePassword2() {
        return getOrDefault("DATABASE_PASSWORD2", DATABASE_PASSWORD2);
    }

    public EquipmentPrivilegeDto getEquipmentPrivileges() {
        return new EquipmentPrivilegeDto();
    }

    public String getStationIdNew() {
        return getOrDefault("STATION_ID_NEW", STATION_ID_NEW);
    }

    public String getEquipmentIdNew() {
        return getOrDefault("EQUIPMENT_ID_NEW", EQUIPMENT_ID_NEW);
    }

    public String getEquipmentSerialNew() {
        return getOrDefault("EQUIPMENT_SERIAL_NEW", EQUIPMENT_SERIAL_NEW);
    }

    public String getLineNumber() {
        return getOrDefault("LINE_NUMBER", LINE_NUMBER);
    }
//
//    public String getQrScannerModel() {
//        return getOrDefault("QR_SCANNER_MODEL", QR_SCANNER_MODEL);
//    }

    public String getThermalPrinterModel() {
        return getOrDefault("THERMAL_PRINTER_MODEL", THERMAL_PRINTER_MODEL);
    }

    public boolean isPrinterCheckBeforePrint() {
        return getOrDefaultBoolean("PRINTER_CHECK_BEFORE_PRINT", PRINTER_CHECK_BEFORE_PRINT);
    }

    public int getShiftTimePeriodInMinutes() {
        return getOrDefaultInt("SHIFT_TIME_PERIOD_IN_MINUTES", SHIFT_TIME_PERIOD_IN_MINUTES);
    }
//    public static String getStationsFile() {
//        return getO("CURRENT_METRO_LINE_STATIONS_FILE");
//    }MDEwNDAxMTI6OFcyOElMSEJLMDEwMDAzOjE3NTUxODI5MzA1NTQ6MDowMDIwOjAxOjAyOjAxOjAxOjA=

    public String getRedisHost() {
        return getOrDefault("REDIS_HOST", REDIS_HOST);
    }

    public int getRedisPort() {
        return getOrDefaultInt("REDIS_PORT", REDIS_PORT);
    }

    public String getRedisPassword() {
        return getOrDefault("REDIS_PASSWORD", REDIS_PASSWORD);
    }

    public String getScuIpAddress() {
        return getOrDefault("SCU_IP_ADDRESS", SCU_IP_ADDRESS);
    }

    public int getScuPort() {
        return getOrDefaultInt("SCU_PORT", SCU_PORT);
    }

    public String getCcuIpAddress() {
        return getOrDefault("CCU_IP_ADDRESS", CCU_IP_ADDRESS);
    }

    public int getCcuPort() {
        return getOrDefaultInt("CCU_PORT", CCU_PORT);
    }

    public String getSQLiteDatabasePath() {
        return getOrDefault("SQLITE_DATABASE_PATH", SQLITE_DATABASE_PATH);
    }

    public String getSQLiteDatabaseName() {
        return getOrDefault("SQLITE_DATABASE_NAME", SQLITE_DATABASE_NAME);
    }

    public int getSQLiteDatabaseConnections() {
        return getOrDefaultInt("SQLITE_DATABASE_CONNECTIONS", SQLITE_DATABASE_CONNECTIONS);
    }

    public String getRestPort() {
        return getOrDefault("CCU_REST_PORT", String.valueOf(CCU_REST_PORT));
    }

    public String getFTPHost() {
        return getOrDefault("FTP_HOST", FTP_HOST);
    }

    public int getFTPPort() {
        return getOrDefaultInt("FTP_PORT", FTP_PORT);
    }

    public String getFTPUsername() {
        return getOrDefault("FTP_USERNAME", FTP_USERNAME);
    }

    public String getFTPPassword() {
        return getOrDefault("FTP_PASSWORD", FTP_PASSWORD);
    }

    public String getFTPRemoteFilePath() {
        return getOrDefault("FTP_REMOTE_FILE_PATH", FTP_REMOTE_FILE_PATH);
    }

    public String getFTPLocalPath() {
        return getOrDefault("FTP_LOCAL_PATH", FTP_LOCAL_PATH);
    }

    public String getLocalZipName() {
        return getOrDefault("LOCAL_ZIP_NAME", LOCAL_ZIP_NAME);
    }

    public String getLocalFileName() {
        return getOrDefault("LOCAL_FILE_NAME", LOCAL_FILE_NAME);
    }

    public String getApplicationLauncherPath() {
        return getOrDefault("APPLICATION_LAUNCHER_PATH", APPLICATION_LAUNCHER_PATH);
    }

    public String getApplicationPath() {
        return getOrDefault("APPLICATION_PATH", APPLICATION_PATH);
    }

    public String getCurrentFileName() {
        return getOrDefault("CURRENT_FILE_NAME", CURRENT_FILE_NAME);
    }

    public int getLaunchDelay() {
        return getOrDefaultInt("LAUNCH_DELAY", LAUNCH_DELAY);
    }

    public String getFXMLLib() {
        return getOrDefault("FXML_LIB", FXML_LIB);
    }

    public boolean getIsUpdate() {
        return getOrDefaultBoolean("UPDATE", UPDATE);
    }

    public String getComPort() {
        return getOrDefault("COM_PORT", "COM2");
    }

    public boolean getEnvironment() {
        return Boolean.parseBoolean(getOrDefault("ENVIRONMENT", String.valueOf(true)));
    }
}
