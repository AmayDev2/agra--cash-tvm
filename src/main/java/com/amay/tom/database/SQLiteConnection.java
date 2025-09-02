package com.amay.tom.database;

import com.amay.tom.config.Env;
import com.amay.tom.utils.env.EnvFile;
import org.tinylog.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public enum SQLiteConnection {
    INSTANCE;

    private  String JDBC_URL;
    private static  int MAX_CONNECTIONS = 10;

    private BlockingQueue<Connection> pool;

    public void setSQLiteConnection() {
        String dbUrl = Env.SQLITE_DATABASE_PATH;
        String dbFileName = Env.SQLITE_DATABASE_NAME;
        int noOfConnections = Env.SQLITE_DATABASE_CONNECTIONS;
        this.JDBC_URL = "jdbc:sqlite:" + dbUrl + File.separator + dbFileName;
        MAX_CONNECTIONS=noOfConnections;
        pool = new ArrayBlockingQueue<>(MAX_CONNECTIONS);
        initializePool();
    }

    private void initializePool() {
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            try {
                Connection connection = DriverManager.getConnection(JDBC_URL);
                pool.offer(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    static int count = 0;


    public Connection getConnection() throws InterruptedException {
        count++;
        Logger.debug("Getting connection from pool {} times", count);

        return pool.take();
    }

    public void releaseConnection(Connection connection) {
        count--;
        Logger.debug("Releasing connection to pool {} times", count);
        if (connection != null) {
            pool.offer(connection);
        }
    }

    public void closeAllConnections() {
        for (Connection connection : pool) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        pool.clear();
    }
}

