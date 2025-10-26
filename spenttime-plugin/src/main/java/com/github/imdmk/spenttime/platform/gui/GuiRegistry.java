package com.github.imdmk.spenttime.platform.gui;

import com.github.imdmk.spenttime.Validator;
import com.github.imdmk.spenttime.shared.gui.IdentifiableGui;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A thread-safe registry for {@link IdentifiableGui} instances keyed by their identifiers.
 * <p>
 * Identifiers are normalized as {@code trim().toLowerCase(Locale.ROOT)} to avoid duplicates
 * caused by whitespace/casing differences.
 * <p>
 * This class relies on an internal {@link ConcurrentHashMap} and can be used from any thread.
 */
public final class GuiRegistry {

    private final Map<String, IdentifiableGui> registry = new ConcurrentHashMap<>();

    /**
     * Registers (or replaces) the given GUI under its normalized identifier.
     *
     * @param gui the GUI to register (its identifier must be non-null)
     * @return the previously registered GUI with the same normalized id, or {@code null} if none existed
     * @throws NullPointerException if {@code gui} or its identifier is {@code null}
     */
    public IdentifiableGui register(@NotNull IdentifiableGui gui) {
        Validator.notNull(gui, "gui cannot be null");
        final String id = normalize(Validator.notNull(gui.getIdentifier(), "gui identifier cannot be null"));
        return registry.put(id, gui);
    }

    /**
     * Registers the given GUI only if no GUI is registered under the same normalized id.
     *
     * @param gui the GUI to register
     * @return {@code true} if registration succeeded; {@code false} if an entry already existed
     * @throws NullPointerException if {@code gui} or its identifier is {@code null}
     */
    public boolean registerIfAbsent(@NotNull IdentifiableGui gui) {
        Validator.notNull(gui, "gui cannot be null");
        final String id = normalize(Validator.notNull(gui.getIdentifier(), "gui identifier cannot be null"));
        return registry.putIfAbsent(id, gui) == null;
    }

    /**
     * Unregisters a GUI by its normalized identifier.
     *
     * @param id the GUI identifier (any casing/whitespace accepted)
     * @return the removed GUI instance, or {@code null} if none was registered
     * @throws NullPointerException if {@code id} is {@code null}
     */
    public IdentifiableGui unregister(@NotNull String id) {
        final String key = normalize(Validator.notNull(id, "id cannot be null"));
        return registry.remove(key);
    }

    /**
     * Retrieves a GUI by its normalized identifier.
     *
     * @param id the GUI identifier (any casing/whitespace accepted)
     * @return the GUI instance, or {@code null} if not registered
     * @throws NullPointerException if {@code id} is {@code null}
     */
    public IdentifiableGui get(@NotNull String id) {
        final String key = normalize(Validator.notNull(id, "id cannot be null"));
        return registry.get(key);
    }

    /**
     * Checks whether a GUI with the given normalized identifier is registered.
     *
     * @param id the GUI identifier (any casing/whitespace accepted)
     * @return {@code true} if a GUI with that id exists
     * @throws NullPointerException if {@code id} is {@code null}
     */
    public boolean isRegistered(@NotNull String id) {
        final String key = normalize(Validator.notNull(id, "id cannot be null"));
        return registry.containsKey(key);
    }

    /**
     * Returns an immutable snapshot of all registered (normalized) identifiers.
     *
     * @return a set of normalized identifiers
     */
    public Set<String> ids() {
        return Set.copyOf(registry.keySet());
    }

    /**
     * Normalizes GUI identifiers in a locale-stable manner.
     * Current strategy: {@code trim().toLowerCase(Locale.ROOT)}.
     */
    private static String normalize(@NotNull String id) {
        final String trimmed = id.trim();
        return trimmed.toLowerCase(Locale.ROOT);
    }
}
