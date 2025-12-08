package com.github.imdmk.playtime.platform.placeholder.adapter;

import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.platform.placeholder.PluginPlaceholder;
import com.github.imdmk.playtime.shared.validate.Validator;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

final class PlaceholderAPIAdapter implements PlaceholderAdapter {

    private final Plugin plugin;
    private final PluginLogger logger;

    private final Map<PluginPlaceholder, PlaceholderExpansion> expansions = new HashMap<>();

    PlaceholderAPIAdapter(@NotNull Plugin plugin, @NotNull PluginLogger logger) {
        this.plugin = Validator.notNull(plugin, "plugin cannot be null");
        this.logger = Validator.notNull(logger, "logger cannot be null");
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void register(@NotNull PluginPlaceholder placeholder) {
        Validator.notNull(placeholder, "placeholder cannot be null");

        if (expansions.containsKey(placeholder)) {
            logger.warn("Placeholder with name %s is already registered!", placeholder.identifier());
            return;
        }

        final PlaceholderExpansion expansion = new DelegatingExpansion(plugin, placeholder);
        if (expansion.register()) {
            expansions.put(placeholder, expansion);
        }
    }

    @Override
    public void unregister(@NotNull PluginPlaceholder placeholder) {
        Validator.notNull(placeholder, "placeholder cannot be null");

        final PlaceholderExpansion expansion = expansions.remove(placeholder);
        if (expansion != null) {
            expansion.unregister();
        }
    }

    @Override
    public void unregisterAll() {
        for (final PlaceholderExpansion expansion : expansions.values()) {
            expansion.unregister();
        }

        expansions.clear();
    }

    private static final class DelegatingExpansion extends PlaceholderExpansion {

        private final Plugin plugin;
        private final PluginPlaceholder delegate;

        private DelegatingExpansion(@NotNull Plugin plugin, @NotNull PluginPlaceholder delegate) {
            this.plugin = plugin;
            this.delegate = delegate;
        }

        @Override
        public @NotNull String getIdentifier() {
            return delegate.identifier();
        }

        @Override
        public @NotNull String getAuthor() {
            return String.join(", ", plugin.getDescription().getAuthors());
        }

        @Override
        public @NotNull String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
            if (player == null) {
                return null;
            }

            return delegate.onRequest(player, params);
        }

        @Override
        public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
            if (player == null) {
                return null;
            }

            return delegate.onRequest(player, params);
        }
    }
}

