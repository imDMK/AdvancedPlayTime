package com.github.imdmk.playtime.platform.placeholder;

import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.injector.subscriber.event.PlayTimeShutdownEvent;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Service
public class PlaceholderService {

    private final Plugin plugin;
    private final PluginLogger logger;
    private final boolean supports;

    private final Map<PluginPlaceholder, PlaceholderExpansion> expansions = new HashMap<>();

    public PlaceholderService(@NotNull Plugin plugin, @NotNull PluginLogger logger) {
        this.plugin = plugin;
        this.logger = logger;
        this.supports = checkSupport();
    }

    public void register(@NotNull PluginPlaceholder placeholder) {
        if (!supports) {
            return;
        }

        if (expansions.containsKey(placeholder)) {
            logger.warn("Placeholder with name %s is already registered!", placeholder.identifier());
            return;
        }

        final PlaceholderExpansion expansion = new DelegatingExpansion(plugin, placeholder);
        if (expansion.register()) {
            expansions.put(placeholder, expansion);
        }
    }

    public void unregister(@NotNull PluginPlaceholder placeholder) {
        if (!supports) {
            return;
        }

        final PlaceholderExpansion expansion = expansions.remove(placeholder);
        if (expansion != null) {
            expansion.unregister();
        }
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    public void unregisterAll() {
        for (final PlaceholderExpansion expansion : expansions.values()) {
            expansion.unregister();
        }

        expansions.clear();
    }

    private boolean checkSupport() {
        return plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
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
