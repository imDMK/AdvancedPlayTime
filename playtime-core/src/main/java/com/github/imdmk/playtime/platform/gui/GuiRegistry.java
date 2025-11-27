package com.github.imdmk.playtime.platform.gui;

import com.github.imdmk.playtime.shared.Validator;
import com.github.imdmk.playtime.shared.gui.IdentifiableGui;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe registry of {@link IdentifiableGui}, keyed by normalized id.
 * Invariant: at most one GUI per concrete class (maintained class index).
 */
public final class GuiRegistry {

    private final Map<String, IdentifiableGui> byId = new ConcurrentHashMap<>();
    private final Map<Class<? extends IdentifiableGui>, IdentifiableGui> byClass = new ConcurrentHashMap<>();

    /**
     * Registers (or replaces) GUI by its normalized identifier.
     * Also updates class index (one instance per class).
     *
     * @return previously registered GUI under the same id, or {@code null}.
     */
    @Nullable
    public IdentifiableGui register(@NotNull IdentifiableGui gui) {
        Validator.notNull(gui, "gui cannot be null");
        final String id = normalize(Validator.notNull(gui.getId(), "gui identifier cannot be null"));

        final IdentifiableGui previous = byId.put(id, gui);

        // maintain class index (assume single instance per class)
        final Class<? extends IdentifiableGui> type = gui.getClass();
        byClass.put(type, gui);

        // if replaced id that pointed to different class instance, clean old class index
        if (previous != null && previous.getClass() != type) {
            byClass.compute(previous.getClass(), (k, current) -> current == previous ? null : current);
        }
        return previous;
    }

    /**
     * Registers GUI only if absent under the same id.
     *
     * @return {@code true} if registered, {@code false} if id existed.
     */
    public boolean registerIfAbsent(@NotNull IdentifiableGui gui) {
        Validator.notNull(gui, "gui cannot be null");
        final String id = normalize(Validator.notNull(gui.getId(), "gui identifier cannot be null"));

        final IdentifiableGui existing = byId.putIfAbsent(id, gui);
        if (existing == null) {
            // we won the race; update class index
            byClass.put(gui.getClass(), gui);
            return true;
        }
        return false;
    }

    /**
     * Unregisters GUI by id. Updates class index if pointing to same instance.
     */
    @Nullable
    public IdentifiableGui unregister(@NotNull String id) {
        final String key = normalize(Validator.notNull(id, "id cannot be null"));
        final IdentifiableGui removed = byId.remove(key);
        if (removed != null) {
            byClass.compute(removed.getClass(), (k, current) -> current == removed ? null : current);
        }
        return removed;
    }

    /**
     * Case-insensitive lookup by id (whitespace-insensitive).
     */
    @Nullable
    public IdentifiableGui getById(@NotNull String id) {
        final String key = normalize(Validator.notNull(id, "id cannot be null"));
        return byId.get(key);
    }

    /**
     * O(1) exact type lookup. Assumes at most one instance per class.
     */
    @Nullable
    public <T extends IdentifiableGui> T getByClass(@NotNull Class<T> type) {
        Validator.notNull(type, "type cannot be null");
        final IdentifiableGui gui = byClass.get(type);
        @SuppressWarnings("unchecked")
        final T cast = (T) gui;
        return cast;
    }

    public boolean isRegistered(@NotNull String id) {
        final String key = normalize(Validator.notNull(id, "id cannot be null"));
        return byId.containsKey(key);
    }

    /** Immutable snapshot of normalized ids. */
    @Unmodifiable
    public Set<String> ids() {
        return Set.copyOf(byId.keySet());
    }

    /** Current strategy: trim + lowercased (Locale.ROOT). */
    private static String normalize(@NotNull String id) {
        final String trimmed = id.trim();
        return trimmed.toLowerCase(Locale.ROOT);
    }
}
