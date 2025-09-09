package com.amay.tom.utils.env;

import com.amay.tom.config.ENVURL;
import com.amay.tom.config.Env;
import io.github.cdimascio.dotenv.Dotenv;
import org.tinylog.Logger;

public class EnvFile {

        static Dotenv dotenv = null;

        static {
            dotenv = Dotenv.configure()
                    .directory(ENVURL.CONFIG + ".env")
                    .load();
        }

        public static void loadEnv() {
            dotenv = Dotenv.configure()
                    .directory(ENVURL.CONFIG + ".env")
                    .load();
        }

        private static String getValueOrDefault(String key, String defaultValue) {
            String value = dotenv.get(key);
            return (value == null || value.isEmpty()) ? defaultValue : value;
        }

    private static int getOrDefaultInt(String key, int defaultValue) {
        String value = dotenv.get(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    private static boolean getOrDefaultBoolean(String key, boolean defaultValue) {
        String value = dotenv.get(key);
        return value != null ? Boolean.getBoolean(value) : defaultValue;
    }

        public static String getEquipmentPrivileges() {
            return getValueOrDefault("EQUIPMENT_PRIVILEGES", Env.EQUIPMENT_PRIVILEGES);
        }

        public static String getDBUrl() {
            return getValueOrDefault("DATABASE_URL", Env.DATABASE_URL);
        }

        public static String getDBUsername() {
            return getValueOrDefault("DATABASE_USERNAME", Env.DATABASE_USERNAME);
        }

        public static String getDBPassword() {
            String pass = getValueOrDefault("DATABASE_PASSWORD2", Env.DATABASE_PASSWORD2);
            Logger.debug("Database password: {}", pass);
            return pass;
        }

        public static String getQRScannerModel() {
            return getValueOrDefault("QR_SCANNER_MODEL", Env.COIN_MODULE);
        }

    public static String getComPort() {
        return getValueOrDefault("COM_PORT", Env.COM_PORT);
    }

        public static String getEquipmentType() {
            return getValueOrDefault("EQUIPMENT_TYPE", Env.EQUIPMENT_TYPE);
        }

        public static String getThermalPrinterModel() {
            return getValueOrDefault("THERMAL_PRINTER_MODEL", Env.THERMAL_PRINTER_MODEL);
        }

        public static boolean getPrinterCheck() {
            return getOrDefaultBoolean("PRINTER_CHECK_BEFORE_PRINT", Env.PRINTER_CHECK_BEFORE_PRINT);
        }

        public static int getShiftTimePeriod() {
            return getOrDefaultInt("SHIFT_TIME_PERIOD_IN_MINUTES", Env.SHIFT_TIME_PERIOD_IN_MINUTES);
        }

        public static String getRedisHost() {
            return getValueOrDefault("REDIS_HOST", Env.REDIS_HOST);
        }

        public static int getRedisPort() {
            return getOrDefaultInt("REDIS_PORT", Env.REDIS_PORT);
        }

//        public static String getRedisChannel() {
//            return getValueOrDefault("REDIS_CHANNEL", Env.REDIS_CHANNEL);
//        }

        public static String getRedisPassword() {
            return getValueOrDefault("REDIS_PASSWORD", Env.REDIS_PASSWORD);
        }

        public static String getCCUIpAddress() {
            return getValueOrDefault("CCU_IP_ADDRESS", Env.CCU_IP_ADDRESS);
        }

        public static int getCCUPort() {
            return getOrDefaultInt("CCU_PORT",Env.CCU_PORT);
        }

        public static String getTicketImagesFolder() {
            return getValueOrDefault("TICKET_IMAGES", Env.TICKET_IMAGES);
        }

        public static long getRefundTime() {
            return getOrDefaultInt("REFUND_TIME", Env.REFUND_TIME);
        }

        public static long getCartLimit() {
            return getOrDefaultInt("CART_LIMIT", Env.CART_LIMIT);
        }

        public static int getEntryExitMismatchPenalty() {
            return getOrDefaultInt("ENTRY_EXIT_PENALTY", Env.ENTRY_EXIT_PENALTY);
        }

        public static String getAdministrativeCharge() {
            return String.valueOf(getOrDefaultInt("ADMINISTRATIVE_CHARGE", Env.ADMINISTRATIVE_CHARGE));
        }
//
//        public static String getStationsFile() {
//            return getValueOrDefault("CURRENT_METRO_LINE_STATIONS_FILE", Env.CURRENT_METRO_LINE_STATIONS_FILE);
//        }
//
//        public static String getCalendar() {
//            return getValueOrDefault("CALENDAR_FILE", Env.CALENDAR_FILE);
//        }

        public static String getTicketName() {
            return getValueOrDefault("TICKET_NAME", Env.TICKET_NAME);
        }

//        public static String getDistanceMatrixFilePath() {
//            return getValueOrDefault("DISTANCE_MATRIX_FILE_PATH", Env.DISTANCE_MATRIX_FILE_PATH);
//        }
//
//        public static String getTicketConfigFile() {
//            return getValueOrDefault("TICKET_CONFIG_FILE", Env.TICKET_CONFIG_FILE);
//        }

//        public static String getProductDefinitionFile() {
//            String value = dotenv.get("PRODUCT_DEFINITION_FILE");
//            if (value == null || value.isEmpty()) {
//                Logger.warn("PRODUCT_DEFINITION_FILE is not set in the environment file.");
//                return Env.PRODUCT_DEFINITION_FILE; // default from interface
//            }
//            return value;
//        }

        public static String getSQLiteDatabasePath() {
            return getValueOrDefault("SQLITE_DATABASE_PATH", Env.SQLITE_DATABASE_PATH);
        }

        public static String getSQLiteDatabaseName() {
            return getValueOrDefault("SQLITE_DATABASE_NAME", Env.SQLITE_DATABASE_NAME);
        }

        public static int getSQLiteDatabaseConnections() {
            int count = Integer.parseInt(String.valueOf(Env.SQLITE_DATABASE_CONNECTIONS));
            try {
                String countStr = dotenv.get("SQLITE_DATABASE_CONNECTIONS");
                if (countStr != null && !countStr.isEmpty()) {
                    count = Integer.parseInt(countStr);
                }
                System.out.println("Count: " + count);
            } catch (Exception e) {
                System.out.println("Error in getting SQLITE_DATABASE_CONNECTIONS");
                e.printStackTrace();
            }
            return count;
        }

        public static String getLastUpdatedFile() {
            String value = dotenv.get("LAST_UPDATED_FILE");
            if (value == null || value.isEmpty()) {
                Logger.warn("LAST_UPDATED_FILE is not set in the environment file.");
                return "";
            }
            return value;
        }

//        public static String getMasterConfigFile() {
//            String value = dotenv.get("MASTER_CONFIG_FILE");
//            if (value == null || value.isEmpty()) {
//                Logger.warn("MASTER_CONFIG_FILE is not set in the environment file.");
//                return Env.MASTER_CONFIG_FILE;
//            }
//            return value;
//        }
    }


