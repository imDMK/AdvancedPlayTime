package com.github.imdmk.playtime.database.repository;

import java.sql.SQLException;

public interface RepositoryBootstrap extends AutoCloseable {

    void start() throws SQLException;

    @Override
    void close();
}
