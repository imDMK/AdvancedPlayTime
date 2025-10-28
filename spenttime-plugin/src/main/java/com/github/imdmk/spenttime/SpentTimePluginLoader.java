package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.infrastructure.module.PluginModule;
import com.github.imdmk.spenttime.platform.gui.GuiModule;
import com.github.imdmk.spenttime.user.UserModule;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Primary Bukkit plugin entry point for <b>SpentTime</b>.
 *
 * <p>This class serves as the <strong>boundary adapter</strong> between the Bukkit lifecycle
 * ({@link JavaPlugin}) and the internal runtime layer represented by {@link SpentTime}.
 * It is deliberately minimal, performing only:</p>
 *
 * <ul>
 *   <li>Lifecycle delegation to {@link SpentTime} (enable/disable),</li>
 *   <li>Creation of the dedicated worker {@link ExecutorService},</li>
 *   <li>Graceful shutdown and cleanup of async resources.</li>
 * </ul>
 *
 * <h3>Design Principles</h3>
 * <ul>
 *   <li><b>Single Responsibility:</b> Coordinates lifecycle and resource management only.</li>
 *   <li><b>Fail-fast:</b> Crashes early if initialization fails, ensuring deterministic state.</li>
 *   <li><b>Reload-safe:</b> Nullifies references and shuts down threads on disable to prevent
 *   classloader retention after /reload.</li>
 * </ul>
 *
 * <h3>Threading Model</h3>
 * <p>Uses a <b>single-threaded daemon executor</b> for all asynchronous operations, typically
 * I/O, database queries, or background tasks. The thread is named {@code SpentTime-Worker} for
 * clarity in thread dumps and diagnostic tools.</p>
 *
 */
public final class SpentTimePluginLoader extends JavaPlugin {

    /**
     * Static registry of all top-level {@link PluginModule} implementations
     * that will be loaded and initialized by {@link SpentTime}.
     */
    private static final List<Class<? extends PluginModule>> MODULES = List.of(
            UserModule.class,
            GuiModule.class
    );

    /** Dedicated executor for non-blocking background work (I/O, DB, async utilities). */
    private ExecutorService worker;

    /** The core runtime responsible for bootstrapping and managing all subsystems. */
    private volatile SpentTime core;

    /**
     * Called by Bukkit when the plugin is being enabled.
     *
     * <p>Initializes the {@link SpentTime} runtime and starts all configured modules.
     * The async worker is created before initialization and remains active until
     * {@link #onDisable()} is called.</p>
     *
     * <p>In case of an initialization failure, a stack trace will be logged, and
     * Bukkit will automatically disable this plugin to prevent half-initialized state.</p>
     */
    @Override
    public void onEnable() {
        this.worker = newWorkerExecutor();

        this.core = new SpentTime(this, this.worker);
        this.core.enable(MODULES);
    }

    /**
     * Called by Bukkit when the plugin is being disabled, either on server shutdown
     * or via manual reload.
     *
     * <p>Ensures a <b>clean shutdown</b> by delegating to {@link SpentTime#disable()},
     * which tears down all modules, closes database connections, and releases
     * allocated resources. Finally, the executor is terminated and references
     * are nullified to support safe reloads.</p>
     */
    @Override
    public void onDisable() {
        if (this.core != null) {
            this.core.disable();
            this.core = null;
        }

        shutdownQuietly(this.worker);
        this.worker = null;
    }

    /**
     * Creates a dedicated single-threaded worker executor for asynchronous plugin operations.
     * <p>The executor uses a named daemon thread ({@code SpentTime-Worker})
     *
     * @return configured single-threaded executor service
     */
    private static ExecutorService newWorkerExecutor() {
        ThreadFactory factory = runnable -> {
            Thread thread = new Thread(runnable, "SpentTime-Worker");
            thread.setDaemon(true);
            return thread;
        };

        return Executors.newSingleThreadExecutor(factory);
    }

    /**
     * Shuts down the given executor quietly, awaiting termination for a short period.
     * If it fails to terminate gracefully, all running tasks are forcibly cancelled.
     *
     * @param executor the executor to shut down, may be {@code null}
     */
    private static void shutdownQuietly(@Nullable ExecutorService executor) {
        if (executor == null) {
            return;
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
