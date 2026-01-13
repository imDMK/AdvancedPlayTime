package com.github.imdmk.playtime.database.repository;

public interface RepositoryBootstrap extends AutoCloseable {

    void start() throws RepositoryInitializationException;

    @Override
    void close();
}
