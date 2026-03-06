package com.github.imdmk.playtime.core.platform.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

final class PapiPlaceholderRegistry implements PlaceholderRegistry {

    private final Plugin plugin;
    private final Map<String, PlaceholderExpansion> expansions = new HashMap<>();

    PapiPlaceholderRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register(PluginPlaceholder placeholder) {
        final String id = normalizeId(placeholder.identifier());
        if (expansions.containsKey(id)) {
            throw new IllegalStateException("Placeholder with id " + id + " is already registered!");
        }

        final PlaceholderExpansion expansion = new PlaceholderExpansionAdapter(plugin, placeholder);
        if (expansion.register()) {
            expansions.put(id, expansion);
        }
    }

    @Override
    public void unregister(PluginPlaceholder placeholder) {
        final String id = normalizeId(placeholder.identifier());

        final PlaceholderExpansion expansion = expansions.remove(id);
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

    private String normalizeId(String rawId) {
        final String id = rawId.trim().toLowerCase(Locale.ROOT);
        if (id.isEmpty()) {
            throw new IllegalArgumentException("Placeholder identifier cannot be empty");
        }

        return id;
    }

    private static final class PlaceholderExpansionAdapter extends PlaceholderExpansion {

        private final Plugin plugin;
        private final PluginPlaceholder delegate;

        private PlaceholderExpansionAdapter(Plugin plugin, PluginPlaceholder delegate) {
            this.plugin = plugin;
            this.delegate = delegate;
        }

        @Override

        public String getIdentifier() {
            return delegate.identifier();
        }

        @Override

        public String getAuthor() {
            return String.join(", ", plugin.getDescription().getAuthors());
        }

        @Override

        public String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public String onPlaceholderRequest(Player player, String params) {
            if (player == null) {
                return null;
            }

            return delegate.request(player, params);
        }

        @Override
        public boolean persist() {
            return true;
        }
    }
}
