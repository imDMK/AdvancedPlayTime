package com.github.imdmk.playtime.infrastructure.database.driver.dependency;

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import com.github.imdmk.playtime.infrastructure.database.DatabaseMode;
import com.github.imdmk.playtime.shared.Validator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Loads JDBC driver libraries dynamically at runtime using Libby.
 * <p>
 * Each {@link DatabaseMode} is mapped to a specific third-party JDBC driver
 * defined in {@link DriverLibraries}. This allows the plugin to ship without
 * any embedded JDBC drivers and load only the required one on demand.
 * <p>
 * This component is deliberately isolated from connection logic to keep the
 * database layer modular and compliant with SRP (single responsibility).
 */
public final class DriverDependencyLoader {

    /** Immutable lookup table mapping supported database modes to driver artifacts. */
    private static final Map<DatabaseMode, Library> LIBRARIES_BY_MODE = Map.of(
            DatabaseMode.MYSQL,      DriverLibraries.MYSQL,
            DatabaseMode.MARIADB,    DriverLibraries.MARIADB,
            DatabaseMode.SQLITE,     DriverLibraries.SQLITE,
            DatabaseMode.POSTGRESQL, DriverLibraries.POSTGRESQL,
            DatabaseMode.H2,         DriverLibraries.H2,
            DatabaseMode.SQL,        DriverLibraries.SQL
    );

    private final BukkitLibraryManager libraryManager;

    /**
     * Creates a new dependency loader using a pre-initialized {@link BukkitLibraryManager}.
     * Maven Central is automatically added as the default repository source.
     *
     * @param libraryManager the library manager used to load driver JARs dynamically
     */
    public DriverDependencyLoader(@NotNull BukkitLibraryManager libraryManager) {
        this.libraryManager = Validator.notNull(libraryManager, "libraryManager cannot be null");
        this.libraryManager.addMavenCentral();
    }

    /**
     * Convenience constructor that initializes a {@link BukkitLibraryManager} using the plugin instance.
     *
     * @param plugin the owning plugin instance
     */
    public DriverDependencyLoader(@NotNull Plugin plugin) {
        this(new BukkitLibraryManager(plugin));
    }

    /**
     * Loads the JDBC driver dependency associated with the given {@link DatabaseMode}.
     * <p>
     * If the driver is already loaded, Libby will skip re-loading it automatically.
     *
     * @param mode the database mode requesting its driver (never null)
     * @throws IllegalArgumentException if the mode has no registered driver
     */
    public void loadDriverFor(@NotNull DatabaseMode mode) {
        Validator.notNull(mode, "mode cannot be null");

        Library library = LIBRARIES_BY_MODE.get(mode);
        if (library == null) {
            throw new IllegalArgumentException("Unsupported database mode: " + mode);
        }

        libraryManager.loadLibrary(library);
    }
}
