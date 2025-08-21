package com.amay.tom.utils.env;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {

    private Dotenv dotenv=null;
    public EnvLoader(String envPath) {
        try {
            dotenv = Dotenv.configure().directory(envPath).load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getEnvValue(String key) {
        return dotenv.get(key);
    }

    public  String getDatabaseUrl() {
        return dotenv.get("DATABASE_URL");
    }

    public  String getDatabaseUsername() {
        return dotenv.get("DATABASE_USERNAME");
    }

    public  String getDatabasePassword() {
        return dotenv.get("DATABASE_PASSWORD");
    }

    public  String getDatabasePassword2() {
        return dotenv.get("DATABASE_PASSWORD2");
    }

    public  String getEquipmentPrivileges() {
        return dotenv.get("EQUIPMENT_PRIVILEGES");
    }

    public  String getStationIdNew() {
        return dotenv.get("STATION_ID_NEW");
    }

    public  String getEquipmentIdNew() {
        return dotenv.get("EQUIPMENT_ID_NEW");
    }

    public  String getEquipmentSerialNew() {
        return dotenv.get("EQUIPMENT_SERIAL_NEW");
    }

    public  String getLineNumber() {
        return dotenv.get("LINE_NUMBER");
    }

    public  String getQrScannerModel() {
        return dotenv.get("QR_SCANNER_MODEL2");
    }

    public  String getThermalPrinterModel() {
        return dotenv.get("THERMAL_PRINTER_MODEL2");
    }

    public  boolean isPrinterCheckBeforePrint() {
        return Boolean.parseBoolean(dotenv.get("PRINTER_CHECK_BEFORE_PRINT"));
    }

    public  int getShiftTimePeriodInMinutes() {
        return Integer.parseInt(dotenv.get("SHIFT_TIME_PERIOD_IN_MINUTES"));
    }

    public  String getRedisHost() {
        return dotenv.get("REDIS_HOST");
    }

    public  int getRedisPort() {
        return Integer.parseInt(dotenv.get("REDIS_PORT"));
    }

    public  String getRedisPassword() {
        return dotenv.get("REDIS_PASSWORD");
    }

    public  String getCcuIpAddress() {
        return dotenv.get("CCU_IP_ADDRESS");
    }

    public  int getCcuPort() {
        return Integer.parseInt(dotenv.get("CCU_PORT"));
    }

    public  String getCcuIpAddressActual() {
        return dotenv.get("CCU_IP_ADDRESS_ACTUAL");
    }

    public  int getCcuPortActual() {
        return Integer.parseInt(dotenv.get("CCU_PORT_ACTUAL"));
    }

    public String getSQLiteDatabasePath() {
        return dotenv.get("SQLITE_DATABASE_PATH");
    }

    public String getSQLiteDatabaseName() {
        return dotenv.get("SQLITE_DATABASE_NAME");
    }


    public int getSQLiteDatabaseConnections() {
        int count=1;
        try{
            String countStr=dotenv.get("SQLITE_DATABASE_CONNECTIONS");
          count=Integer.parseInt(countStr);
         System.out.println("Count: "+count);
        }catch(Exception e){
            System.out.println("Error in getting SQLITE_DATABASE_CONNECTIONS");
            e.printStackTrace();

        }
         return count;
    }

    public String getRestPort() {
        return dotenv.get("CCU_REST_PORT");
    }

    public String getFTPHost() {
        return dotenv.get("FTP_HOST");
    }

    public int getFTPPort() {
        return Integer.parseInt(dotenv.get("FTP_PORT"));
    }

    public String getFTPUsername() {
        return dotenv.get("FTP_USERNAME");
    }

    public String getFTPPassword() {
        return dotenv.get("FTP_PASSWORD");
    }

    public String getFTPRemoteFilePath() {
        return dotenv.get("FTP_REMOTE_FILE_PATH");
    }

    public String getFTPLocalPath() {
        return dotenv.get("FTP_LOCAL_PATH");
    }

    public String getLocalFileName() {
        return dotenv.get("LOCAL_FILE_NAME");
    }

    public String getApplicationLauncherPath() {
        return dotenv.get("APPLICATION_LAUNCHER_PATH");
    }

    public String getApplicationPath() {
        return dotenv.get("APPLICATION_PATH");
    }

    public String getCurrentFileName() {
        return dotenv.get("CURRENT_FILE_NAME");
    }

    public int getLaunchDelay() {
        return Integer.parseInt(dotenv.get("LAUNCH_DELAY"));
    }

    public String getFXMLLib() {
        return dotenv.get("FXML_LIB");
    }

    public boolean getIsUpdate() {
        String isUpdate = dotenv.get("UPDATE");
        return isUpdate != null && isUpdate.equalsIgnoreCase("false");
    }
}
