package com.amay.tom.repository.sql;

import com.amay.tom.database.SQLiteConnection;

import java.sql.Connection;

public class SqlRepositoryImpl extends SqlGlobalRepository {

    public SqlRepositoryImpl(Connection connection) {
        super();
        this.connection = connection;
    }

    public void closeConnection() {
        SQLiteConnection.INSTANCE.releaseConnection(this.connection);
    }
}
