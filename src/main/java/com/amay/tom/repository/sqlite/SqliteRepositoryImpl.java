package com.amay.tom.repository.sqlite;

import com.amay.tom.database.SQLiteConnection;
import com.amay.tom.repository.sqlite.SqliteGlobalRepository;

import java.sql.Connection;

public class SqliteRepositoryImpl extends SqliteGlobalRepository {

    public SqliteRepositoryImpl(Connection connection) {
        super();
        this.connection = connection;
    }

    public void closeConnection() {
        SQLiteConnection.INSTANCE.releaseConnection(this.connection);
    }
}
