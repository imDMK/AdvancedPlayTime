package com.github.imdmk.playtime.database.library;

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import com.github.imdmk.playtime.database.DatabaseMode;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class DriverLibraryLoader {

    private static final Map<DatabaseMode, Library> LIBRARIES_BY_MODE = Map.of(
            DatabaseMode.MYSQL,      DriverLibraries.MYSQL,
            DatabaseMode.MARIADB,    DriverLibraries.MARIADB,
            DatabaseMode.SQLITE,     DriverLibraries.SQLITE,
            DatabaseMode.POSTGRESQL, DriverLibraries.POSTGRESQL,
            DatabaseMode.H2,         DriverLibraries.H2,
            DatabaseMode.SQL,        DriverLibraries.SQL
    );

    private final BukkitLibraryManager libraryManager;

    public DriverLibraryLoader(@NotNull Plugin plugin) {
        this.libraryManager = new BukkitLibraryManager(plugin);
        this.libraryManager.addMavenCentral();
    }

    public void loadFor(@NotNull DatabaseMode mode) {
        final Library library = LIBRARIES_BY_MODE.get(mode);
        if (library == null) {
            throw new IllegalArgumentException("Unsupported database mode: " + mode);
        }

        libraryManager.loadLibrary(library);
    }
}
