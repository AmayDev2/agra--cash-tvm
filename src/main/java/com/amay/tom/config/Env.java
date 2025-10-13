package com.amay.tom.config;

import com.amay.tom.config.dto.TicketConfigDTO;
import com.amay.tom.model.equipment.entity.EquipmentPrivilege;

public interface Env {

    // Project
    String PROJECT_NAME = "";

    // Database
    String DATABASE_URL = "jdbc:h2:file:C:/tvm-config/h2-db/tvm_db;DB_CLOSE_ON_EXIT=FALSE";
    String DATABASE_USERNAME = "root";
    String DATABASE_PASSWORD = "Admin@123";
    String DATABASE_PASSWORD2 = "Admin@123";

    // SQLite
    int SQLITE_DATABASE_CONNECTIONS = 1;
    String SQLITE_DATABASE_PATH = "";
    String SQLITE_DATABASE_NAME = "";

    // Equipment privileges file path
    String EQUIPMENT_PRIVILEGES = "";

    // Station and Equipment
    String STATION_ID_NEW = "";
    String EQUIPMENT_ID_NEW = "";
    String EQUIPMENT_SERIAL_NEW = "";
    String EQUIPMENT_TYPE = "";
    String LINE_NUMBER = "";

    // Hardware
    String QR_SCANNER_MODEL = "";
    String THERMAL_PRINTER_MODEL = "";
    boolean PRINTER_CHECK_BEFORE_PRINT = false;
    int SHIFT_TIME_PERIOD_IN_MINUTES = 0;

    // Redis
    String REDIS_HOST = "";
    int REDIS_PORT = 0;
    String REDIS_PASSWORD = "";

    // CCU Connection
    String CCU_IP_ADDRESS = "";
    int CCU_PORT = 0;
    String CCU_REST_PORT = "";

    // SCU Connection
    String SCU_IP_ADDRESS = "";
    int SCU_PORT = 0;

    // Ticket
    String TICKET_NAME = "";
    String TICKET_IMAGES = "";

    // Business rules
    int REFUND_TIME = 0;
    int CART_LIMIT = 0;
    int ENTRY_EXIT_PENALTY = 0;
    int ADMINISTRATIVE_CHARGE = 0;

    // Update flag
    boolean UPDATE = false;

    // Run command
    String RUN_COMMAN = "";

    // FTP Configuration
    String FTP_HOST = "";
    int FTP_PORT = 0;
    String FTP_USERNAME = "";
    String FTP_PASSWORD = "";
    String FTP_REMOTE_FILE_PATH = "";
    String FTP_LOCAL_PATH = "";
    String LOCAL_FILE_NAME = "";
    String LOCAL_ZIP_NAME = "";

    // Application Settings
    String APPLICATION_LAUNCHER_PATH = "";
    String APPLICATION_PATH = "";
    String CURRENT_FILE_NAME = "";
    int LAUNCH_DELAY = 0;
    String FXML_LIB = "";
    String COIN_MODULE = "";
    String COM_PORT = "COM2";
    String UPS_COM_PORT = "COM6";

    // Methods to return objects for config or privileges
    TicketConfigDTO getTicketConfig();
    EquipmentPrivilege getEquipmentPrivilege();
}
