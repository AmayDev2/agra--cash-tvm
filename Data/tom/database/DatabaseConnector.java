package com.amay.tom.database;

import com.amay.tom.utils.env.EnvFile;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static Connection connection = null;

    public DatabaseConnector() throws SQLException {
        connection = getConnection();
    }


    public static Connection getConnection() throws SQLException {
        Logger.debug("Connecting to database: {} {} {}", EnvFile.getDBUrl(), EnvFile.getDBUsername(), EnvFile.getDBPassword());
        return DriverManager.getConnection(EnvFile.getDBUrl(), EnvFile.getDBUsername(), EnvFile.getDBPassword());
    }

    public static void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            Logger.error("Error closing database connection: {}", e);
        }
    }
}
