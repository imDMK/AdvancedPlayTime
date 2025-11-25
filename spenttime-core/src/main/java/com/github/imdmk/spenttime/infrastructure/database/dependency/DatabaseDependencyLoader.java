package com.github.imdmk.spenttime.infrastructure.database.dependency;

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import com.github.imdmk.spenttime.infrastructure.database.DatabaseMode;
import com.github.imdmk.spenttime.shared.Validator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class DatabaseDependencyLoader {

    private static final Map<DatabaseMode, Library> LIBRARIES_BY_MODE = Map.of(
            DatabaseMode.MYSQL,      DatabaseLibraries.MYSQL,
            DatabaseMode.MARIADB,    DatabaseLibraries.MARIADB,
            DatabaseMode.SQLITE,     DatabaseLibraries.SQLITE,
            DatabaseMode.POSTGRESQL, DatabaseLibraries.POSTGRESQL,
            DatabaseMode.H2,         DatabaseLibraries.H2,
            DatabaseMode.SQL,        DatabaseLibraries.SQL
    );

    private final BukkitLibraryManager libraryManager;

    public DatabaseDependencyLoader(@NotNull BukkitLibraryManager libraryManager) {
        this.libraryManager = Validator.notNull(libraryManager, "libraryManager cannot be null");
        this.libraryManager.addMavenCentral();
    }

    public DatabaseDependencyLoader(@NotNull Plugin plugin) {
        this(new BukkitLibraryManager(plugin));
    }

    public void loadDriverFor(@NotNull DatabaseMode mode) {
        Validator.notNull(mode, "mode cannot be null");

        Library library = LIBRARIES_BY_MODE.get(mode);
        if (library == null) {
            throw new IllegalArgumentException("Unsupported database mode: " + mode);
        }

        libraryManager.loadLibrary(library);
    }
}

