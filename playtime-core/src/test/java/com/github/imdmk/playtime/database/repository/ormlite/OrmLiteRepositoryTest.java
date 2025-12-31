package com.github.imdmk.playtime.database.repository.ormlite;

import com.github.imdmk.playtime.database.repository.RepositoryContext;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.j256.ormlite.dao.Dao;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrmLiteRepositoryTest {

    public static class TestDaoRepository extends OrmLiteRepository<String, Integer> {

        public TestDaoRepository(PluginLogger logger, RepositoryContext context) {
            super(logger, context);
        }

        @Override
        protected Class<String> entityClass() {
            return String.class;
        }

        @Override
        protected List<Class<?>> entitySubClasses() {
            return List.of();
        }

        public <R> R callWithDao(Supplier<R> supplier) {
            return withDao(supplier);
        }

        public <R> CompletableFuture<R> callExecuteSupplier(Supplier<R> supplier) {
            return executeAsync(supplier);
        }

        public <R> CompletableFuture<R> callExecuteSupplierTimeout(Supplier<R> supplier, Duration timeout) {
            return executeAsync(supplier, timeout);
        }

        public CompletableFuture<Void> callExecuteRunnable(Runnable runnable) {
            return executeAsync(runnable);
        }

        public CompletableFuture<Void> callExecuteRunnableTimeout(Runnable runnable, Duration timeout) {
            return executeAsync(runnable, timeout);
        }

        public void setDaoForTest() {
            this.dao = mockDao();
        }

        @SuppressWarnings("unchecked")
        private Dao<String, Integer> mockDao() {
            return mock(Dao.class);
        }
    }

    private ExecutorService executor() {
        return Executors.newSingleThreadExecutor();
    }

    @Test
    void executeAsyncSupplierShouldReturnValue() {
        PluginLogger logger = mock(PluginLogger.class);
        RepositoryContext ctx = new RepositoryContext(executor());

        TestDaoRepository repo = new TestDaoRepository(logger, ctx);

        CompletableFuture<Integer> future =
                repo.callExecuteSupplier(() -> 42);

        assertThat(future.join()).isEqualTo(42);
    }

    @Test
    void executeAsyncSupplierShouldWrapException() {
        PluginLogger logger = mock(PluginLogger.class);
        RepositoryContext ctx = new RepositoryContext(executor());

        TestDaoRepository repo = new TestDaoRepository(logger, ctx);

        CompletableFuture<Integer> f =
                repo.callExecuteSupplier(() -> {
                    throw new IllegalStateException("boom");
                });

        assertThatThrownBy(f::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(IllegalStateException.class);

        verify(logger).error(any(Throwable.class), eq("Async DAO operation failed"));
    }

    @Test
    void executeAsyncSupplierShouldTimeout() {
        PluginLogger logger = mock(PluginLogger.class);
        RepositoryContext ctx = new RepositoryContext(executor());

        TestDaoRepository repo = new TestDaoRepository(logger, ctx);

        CompletableFuture<Integer> f =
                repo.callExecuteSupplierTimeout(() -> {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {}
                    return 1;
                }, Duration.ofMillis(50));

        assertThatThrownBy(f::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(TimeoutException.class);

        verify(logger).warn(
                eq("Async DAO operation timed out after %s ms"),
                eq(50L)
        );
    }

    @Test
    void executeAsyncRunnableShouldRunSuccessfully() {
        PluginLogger logger = mock(PluginLogger.class);
        RepositoryContext ctx = new RepositoryContext(executor());
        TestDaoRepository repo = new TestDaoRepository(logger, ctx);

        CompletableFuture<Void> f =
                repo.callExecuteRunnable(() -> {});

        assertThat(f).succeedsWithin(Duration.ofSeconds(1));
    }

    @Test
    void withDaoShouldThrowIfDaoNotInitialized() {
        PluginLogger logger = mock(PluginLogger.class);
        RepositoryContext ctx = new RepositoryContext(executor());
        TestDaoRepository repo = new TestDaoRepository(logger, ctx);

        assertThatThrownBy(() -> repo.callWithDao(() -> "x"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DAO not initialized");
    }

    @Test
    void withDaoShouldRunWhenDaoIsAvailable() {
        PluginLogger logger = mock(PluginLogger.class);
        RepositoryContext ctx = new RepositoryContext(executor());
        TestDaoRepository repo = new TestDaoRepository(logger, ctx);

        repo.setDaoForTest();

        String result = repo.callWithDao(() -> "OK");

        assertThat(result).isEqualTo("OK");
    }
}

