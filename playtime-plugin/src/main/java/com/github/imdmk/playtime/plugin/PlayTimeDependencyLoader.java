package com.github.imdmk.playtime.plugin;

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;

final class PlayTimeDependencyLoader {

    private static final Gson GSON = new Gson();
    private static final String CONFIG_FILE = "plugin-libraries.json";

    void load(Plugin plugin) {
        Logger logger = plugin.getLogger();
        logger.info("Loading plugin libraries...");

        Config config = loadConfig();
        BukkitLibraryManager manager = new BukkitLibraryManager(plugin);

        for (String repository : config.repositories) {
            manager.addRepository(repository);
        }

        for (Dependency dependency : config.dependencies) {
            Library library = Library.builder()
                    .groupId(dependency.group)
                    .artifactId(dependency.artifact)
                    .version(dependency.version)
                    .build();

            manager.loadLibrary(library);
        }

        logger.info("Plugin libraries loaded successfully.");
    }

    private Config loadConfig() {
        InputStream stream = getClass()
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE);

        if (stream == null) {
            throw new IllegalStateException(CONFIG_FILE + " not found in jar");
        }

        try (InputStreamReader reader = new InputStreamReader(stream)) {
            return GSON.fromJson(reader, Config.class);
        } catch (IOException | JsonParseException e) {
            throw new RuntimeException("Failed to parse " + CONFIG_FILE, e);
        }
    }

    private static final class Config {

        List<String> repositories = List.of();
        List<Dependency> dependencies = List.of();

    }

    private static final class Dependency {

        String group;
        String artifact;
        String version;

    }

}