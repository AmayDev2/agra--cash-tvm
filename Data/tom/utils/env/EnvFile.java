package com.amay.tom.utils.env;

import com.amay.tom.config.ENVURL;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.repository.StationData;
import io.github.cdimascio.dotenv.Dotenv;
import org.tinylog.Logger;

public class EnvFile {

    static Dotenv dotenv=null;

    static {
        dotenv = Dotenv.configure().directory(ENVURL.CONFIG+"\\tvm-config.env").load();
    }
     public static void loadEnv(){
         // Load the values from .env file

         dotenv = Dotenv.configure().directory(ENVURL.CONFIG+"\\tvm-config.env").load();

         String currentStationId = dotenv.get("STATION_ID_NEW");

         StationData.getInstance();
         String currentStationName = StationData.getInstance().getStation(currentStationId).getStationName();
//                 dotenv.get("STATION_NAME");

         String currentEquipmentId = dotenv.get("EQUIPMENT_ID_NEW");
         String currentEquipmentSerial = dotenv.get("EQUIPMENT_SERIAL_NEW");

         String lineNumber = dotenv.get("LINE_NUMBER");
//

         SystemConfig.getInstance(currentStationId, currentStationName, currentEquipmentId, currentEquipmentSerial,lineNumber);
     }

     public static String getEquipmentPrivileges() {
         return dotenv.get("EQUIPMENT_PRIVILEGES");
     }
        public static String getDBUrl(){
         return dotenv.get("DATABASE_URL");
     }

        public static String getDBUsername(){
            return dotenv.get("DATABASE_USERNAME");
        }

        public static String getDBPassword(){
            String pass= dotenv.get("DATABASE_PASSWORD2");
            Logger.debug("Database password: {}", pass);
            return pass;
        }


    public static String getQRScannerModel() {
        return dotenv.get("QR_SCANNER_MODEL");
    }

    public static String getEquipmentType() {return dotenv.get("EQUIPMENT_TYPE");}

    public static String getThermalPrinterModel() {
        return dotenv.get("THERMAL_PRINTER_MODEL");
    }

    public static boolean getPrinterCheck() {
        return Boolean.parseBoolean(dotenv.get("PRINTER_CHECK_BEFORE_PRINT"));

    }

    public static int getShiftTimePeriod() {
        return Integer.parseInt(dotenv.get("SHIFT_TIME_PERIOD_IN_MINUTES"));
    }

    public static String getRedisHost() {
        return dotenv.get("REDIS_HOST");
    }

    public static int getRedisPort() {
        return Integer.parseInt(dotenv.get("REDIS_PORT"));
    }

    public static String getRedisChannel() {
        return dotenv.get("REDIS_CHANNEL");
    }

    public static String getRedisPassword() {
        return dotenv.get("REDIS_PASSWORD");
    }

    public static String getCCUIpAddress(){ return dotenv.get("CCU_IP_ADDRESS");}

    public static int getCCUPort(){return Integer.parseInt(dotenv.get("CCU_PORT"));}

    public static String getTicketImagesFolder() {
       return dotenv.get("TICKET_IMAGES");
    }

    public static long getRefundTime() {
        return Long.parseLong(dotenv.get("REFUND_TIME"));
    }

    public static long getCartLimit() {
        return Long.parseLong(dotenv.get("CART_LIMIT"));
    }

    public static int getEntryExitMismatchPenalty() {
        return Integer.parseInt(dotenv.get("ENTRY_EXIT_PENALTY")==null? String.valueOf(0) :dotenv.get("ENTRY_EXIT_PENALTY"));
    }

    public static String getAdministrativeCharge() {
        return dotenv.get("ADMINISTRATIVE_CHARGE")==null? String.valueOf(0) :dotenv.get("ADMINISTRATIVE_CHARGE");
    }

    public static String getStationsFile() {
        return dotenv.get("CURRENT_METRO_LINE_STATIONS_FILE");
    }

    public static String getCalendar(){
        return dotenv.get("CALENDAR_FILE");
    }

    public static String getTicketName() {
        return dotenv.get("TICKET_NAME");
    }

    public static String getDistanceMatrixFilePath() {
        return dotenv.get("DISTANCE_MATRIX_FILE_PATH");
    }

    public static String getTicketConfigFile() {
        return dotenv.get("TICKET_CONFIG_FILE");
    }

    public static String getSQLiteDatabasePath() {
        return dotenv.get("SQLITE_DATABASE_PATH");
    }

    public static String getSQLiteDatabaseName() {
        return dotenv.get("SQLITE_DATABASE_NAME");
    }


    public static int getSQLiteDatabaseConnections() {
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

    public static String getLastUpdatedFile() {
        String lastUpdatedFile = dotenv.get("LAST_UPDATED_FILE");
        if (lastUpdatedFile == null || lastUpdatedFile.isEmpty()) {
            Logger.warn("LAST_UPDATED_FILE is not set in the environment file.");
            return "C:\\tomConfig"; // Default value if not set
        }
        return lastUpdatedFile;
    }
}


