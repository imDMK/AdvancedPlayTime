package com.github.imdmk.playtime.core.database;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public interface DatabaseManager {

    void start() throws SQLException;

    ConnectionSource getConnectionSource();
}
