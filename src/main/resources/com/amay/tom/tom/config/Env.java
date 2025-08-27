package com.amay.tom.config;

import com.amay.tom.config.dto.TicketConfigDTO;
import com.amay.tom.model.equipment.entity.EquipmentPrivilege;

public interface Env {

    // Project
    String PROJECT_NAME = "567890";

    // Database
    String DATABASE_URL = "jdbc:h2:file:C:/tom-config/h2-db/tom_db;DB_CLOSE_ON_EXIT=FALSE";
    String DATABASE_USERNAME = "root";
    String DATABASE_PASSWORD = "Admin@123";
    String DATABASE_PASSWORD2 = "Admin@123";

    // SQLite
    int SQLITE_DATABASE_CONNECTIONS = 5;
    String SQLITE_DATABASE_PATH = "C:\\tom-config\\h2-db";
    String SQLITE_DATABASE_NAME = "tomsqlite.db";

    // Equipment privileges file path
    String EQUIPMENT_PRIVILEGES = "C:\\tom-config\\sle_privillage\\EquipmentPrivillages.json";

    // Station and Equipment
    String STATION_ID_NEW = "01";
    String EQUIPMENT_ID_NEW = "01010106";
    String EQUIPMENT_SERIAL_NEW = "06";
    String EQUIPMENT_TYPE = "01";
    String LINE_NUMBER = "01";

    // Hardware
    String QR_SCANNER_MODEL = "7161WP";
    String THERMAL_PRINTER_MODEL = "Posiflex PP8802 Printer";
    boolean PRINTER_CHECK_BEFORE_PRINT = true;
    int SHIFT_TIME_PERIOD_IN_MINUTES = 3;

    // Redis
    String REDIS_HOST = "redis-19373.c241.us-east-1-4.ec2.cloud.redislabs.com";
    int REDIS_PORT = 19373;
    String REDIS_PASSWORD = "3BDsrSjfNJ9MaBy4xIojO8G0NchEfKOK";

    // CCU Connection
    String CCU_IP_ADDRESS = "192.168.1.43";
    int CCU_PORT = 9000;
    String CCU_REST_PORT = "5000";

    // SCU Connection
    String SCU_IP_ADDRESS = "192.168.1.48";
    int SCU_PORT = 9000;


    // Ticket
    String TICKET_NAME = "MPMRC";
    String TICKET_IMAGES = "C:\\tom-config\\ticket_images";

    // Business rules
    int REFUND_TIME = 40;
    int CART_LIMIT = 5;
    int ENTRY_EXIT_PENALTY = 10;
    int ADMINISTRATIVE_CHARGE = 0;

    // Update flag
    boolean UPDATE = true;

    // Run command
    String RUN_COMMAN = "java --module-path \"C:\\Program Files\\javafx-sdk-21.0.7\\lib\" "
            + "--add-modules javafx.controls,javafx.fxml -jar  \"E:\\Amay Technosystems\\AFC\\Tom\\target\\Tom.jar\"";

    // FTP Configuration
    String FTP_HOST = "192.168.1.43";
    int FTP_PORT = 2222;
    String FTP_USERNAME = "mpmrc";
    String FTP_PASSWORD = "sw@mpmrc";
    String FTP_REMOTE_FILE_PATH = "tom/TOM.jar";
    String FTP_LOCAL_PATH = "C:\\Users\\aradh\\Downloads\\Project\\TOM\\TOM\\tom\\target";
    String LOCAL_FILE_NAME = "TOM_New.jar";

    // Application Settings
    String APPLICATION_LAUNCHER_PATH = "C:/tom-config/launcher.bat";
    String APPLICATION_PATH = "C:\\Users\\aradh\\Downloads\\Project\\TOM\\TOM\\tom\\target";
    String CURRENT_FILE_NAME = "TOM.jar";
    int LAUNCH_DELAY = 5;
    String FXML_LIB = "C:/Program Files/javafx-sdk-21.0.7/lib";

    // Methods to return objects for config or privileges
    TicketConfigDTO getTicketConfig();            // returns TicketConfigDTO from TICKET_CONFIG_FILE
    EquipmentPrivilege getEquipmentPrivilege();   // returns EquipmentPrivilege object from EQUIPMENT_PRIVILEGES file
}
