package com.github.imdmk.playtime.core.database.repository;

public interface RepositoryBootstrap extends AutoCloseable {

    void start() throws RepositoryInitializationException;

    @Override
    void close();
}
