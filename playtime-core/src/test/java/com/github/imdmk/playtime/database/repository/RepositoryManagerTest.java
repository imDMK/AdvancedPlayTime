package com.github.imdmk.playtime.database.repository;

import com.github.imdmk.playtime.infrastructure.database.repository.Repository;
import com.github.imdmk.playtime.infrastructure.database.repository.RepositoryManager;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.j256.ormlite.support.ConnectionSource;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RepositoryManagerTest {

    @Test
    void registerShouldStoreRepository() {
        PluginLogger logger = mock(PluginLogger.class);
        Repository repo = mock(Repository.class);

        RepositoryManager manager = new RepositoryManager(logger);

        manager.register(repo);

        // Registering same repo twice should log warning
        manager.register(repo);

        verify(logger).warn(
                eq("Repository %s already registered — skipping"),
                eq(repo.getClass().getSimpleName())
        );
    }

    @Test
    void startAllShouldInvokeStartOnEachRepository() throws Exception {
        PluginLogger logger = mock(PluginLogger.class);
        Repository repo1 = mock(Repository.class);
        Repository repo2 = mock(Repository.class);
        ConnectionSource source = mock(ConnectionSource.class);

        RepositoryManager manager = new RepositoryManager(logger);
        manager.register(repo1, repo2);

        manager.startAll(source);

        verify(repo1).start(source);
        verify(repo2).start(source);
    }

    @Test
    void startAllShouldLogAndRethrowSQLException() throws Exception {
        PluginLogger logger = mock(PluginLogger.class);
        Repository faulty = mock(Repository.class);
        ConnectionSource source = mock(ConnectionSource.class);

        doThrow(new SQLException("boom")).when(faulty).start(source);

        RepositoryManager manager = new RepositoryManager(logger);
        manager.register(faulty);

        assertThatThrownBy(() -> manager.startAll(source))
                .isInstanceOf(SQLException.class);

        verify(logger).error(any(Exception.class),
                eq("Failed to start repository: %s"),
                eq(faulty.getClass().getSimpleName()));
    }

    @Test
    void closeShouldInvokeCloseOnEachRepository() {
        PluginLogger logger = mock(PluginLogger.class);
        Repository repo1 = mock(Repository.class);
        Repository repo2 = mock(Repository.class);

        RepositoryManager manager = new RepositoryManager(logger);
        manager.register(repo1, repo2);

        manager.close();

        verify(repo1).close();
        verify(repo2).close();
    }

    @Test
    void closeShouldLogWarningWhenRepositoryThrows() {
        PluginLogger logger = mock(PluginLogger.class);
        Repository repo = mock(Repository.class);

        doThrow(new RuntimeException("err")).when(repo).close();

        RepositoryManager manager = new RepositoryManager(logger);
        manager.register(repo);

        manager.close();

        verify(logger).warn(
                any(Exception.class),
                eq("Error while closing repository: %s"),
                eq(repo.getClass().getSimpleName())
        );
    }
}

