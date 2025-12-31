package com.github.imdmk.playtime.database.repository;

import com.j256.ormlite.support.ConnectionSource;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface Repository extends AutoCloseable {

    void start(@NotNull ConnectionSource source) throws SQLException;

    @Override
    void close();
}
