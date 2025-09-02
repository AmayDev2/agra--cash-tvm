package com.amay.tom.database;


import com.amay.tom.utils.env.EnvFile;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SQLConnector {

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final int maxConnections;
    private final BlockingQueue<Connection> pool;
    private static int count = 0;


    public SQLConnector(String jdbcUrl, String username, String password, int noOfConnections) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.maxConnections = Math.max(noOfConnections, 1); // Ensure at least one connection
        this.pool = new ArrayBlockingQueue<>(maxConnections);

    }

    public void setConnection() {
        initializePool();
    }

    private void initializePool() {
        for (int i = 0; i < maxConnections; i++) {
            try {
                Class.forName("org.h2.Driver");
                Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                pool.offer(connection);
            } catch (SQLException | ClassNotFoundException e) {
                Logger.error("Error initializing database connection pool: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }

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
                Logger.error("Error closing database connection: {}", e.getMessage());
                e.printStackTrace();
            }
        }
        pool.clear();
    }


}
