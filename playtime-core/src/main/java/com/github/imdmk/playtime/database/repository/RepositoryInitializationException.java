package com.github.imdmk.playtime.database.repository;

public final class RepositoryInitializationException extends RuntimeException {

    public RepositoryInitializationException(Class<?> repositoryClass, Throwable cause) {
        super("Failed to initialize repository: " + repositoryClass.getName(), cause);
    }

}

