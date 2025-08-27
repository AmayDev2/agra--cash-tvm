package com.amay.tom.database;


import org.tinylog.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
//new
public class SQLiteConnector {
    private  final String JDBC_URL ;
    private  final int MAX_CONNECTIONS ;
    private BlockingQueue<Connection> pool;
    private static int count = 0;
    public SQLiteConnector(String path, String name,int noOfConnections) {
        this.JDBC_URL = "jdbc:sqlite:" + path + File.separator + name;
        this.MAX_CONNECTIONS = noOfConnections>0?noOfConnections:1;
        pool = new ArrayBlockingQueue<>(MAX_CONNECTIONS);
    }

    public void setSQLiteConnection() {
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

