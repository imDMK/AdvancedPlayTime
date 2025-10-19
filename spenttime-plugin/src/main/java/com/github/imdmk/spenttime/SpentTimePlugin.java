package com.github.imdmk.spenttime;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

/**
 * Main Bukkit plugin entrypoint for SpentTime.
 *
 * <p>This class is intentionally minimal — it performs only lifecycle delegation
 * to {@link SpentTime}, without managing any internal logic or heavy services.
 * All runtime initialization and shutdown handling is encapsulated inside {@code SpentTime}.
 *
 * <p>Design goals:
 * <ul>
 *   <li>Keep Bukkit main thread initialization fast and fail-safe</li>
 *   <li>Ensure clean shutdown and prevent classloader leaks on reload</li>
 *   <li>Leave metrics, configuration, and other systems to {@code SpentTime}</li>
 * </ul>
 *
 * <p>Thread-safety: {@link #core} is declared {@code volatile} to guarantee
 * visibility across reload or async shutdown scenarios.
 */
public final class SpentTimePlugin extends JavaPlugin {

    /** Core runtime component responsible for initializing all plugin systems. */
    private volatile SpentTime core;

    /**
     * Called by Bukkit when the plugin is being enabled.
     *
     * <p>Creates and initializes the {@link SpentTime} core. All setup logic
     * (e.g. configuration, schedulers, metrics) is handled inside {@code SpentTime}.
     */
    @Override
    public void onEnable() {
        this.core = new SpentTime(this);
        this.core.enable();
    }

    /**
     * Called by Bukkit when the plugin is being disabled (server shutdown or reload).
     *
     * <p>Safely disables the core and nullifies the reference to help with
     * garbage collection and prevent classloader leaks during reloads.
     */
    @Override
    public void onDisable() {
        if (this.core != null) {
            this.core.disable();
            this.core = null;
        }
    }
}
