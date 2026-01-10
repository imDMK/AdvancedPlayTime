package com.github.imdmk.playtime.platform.gui;

import com.github.imdmk.playtime.injector.ComponentPriority;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.injector.subscriber.event.PlayTimeShutdownEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service(priority = ComponentPriority.LOW)
public final class GuiRegistry {

    private final Map<String, IdentifiableGui> byId = new ConcurrentHashMap<>();
    private final Map<Class<? extends IdentifiableGui>, IdentifiableGui> byClass = new ConcurrentHashMap<>();

    public void register(@NotNull IdentifiableGui gui) {
        final String id = normalizeId(gui.getId());
        final IdentifiableGui previous = byId.put(id, gui);

        // maintain class index (assume single instance per class)
        final Class<? extends IdentifiableGui> type = gui.getClass();
        byClass.put(type, gui);

        // if replaced id that pointed to different class instance, clean old class index
        if (previous != null && previous.getClass() != type) {
            byClass.compute(previous.getClass(), (k, current) -> current == previous ? null : current);
        }
    }

    public boolean registerIfAbsent(@NotNull IdentifiableGui gui) {
        final String id = normalizeId(gui.getId());
        final IdentifiableGui existing = byId.putIfAbsent(id, gui);
        if (existing == null) {
            // we won the race; update class index
            byClass.put(gui.getClass(), gui);
            return true;
        }
        return false;
    }

    public boolean isRegistered(@NotNull String id) {
        return byId.containsKey(normalizeId(id));
    }

    public IdentifiableGui unregister(@NotNull String id) {
        final String key = normalizeId(id);
        final IdentifiableGui removed = byId.remove(key);
        if (removed != null) {
            byClass.compute(removed.getClass(), (k, current) -> current == removed ? null : current);
        }
        return removed;
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    public void unregisterAll() {
        byId.clear();
        byClass.clear();
    }

    @Nullable
    public IdentifiableGui getById(@NotNull String id) {
        return byId.get(normalizeId(id));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableGui> T getByClass(@NotNull Class<T> type) {
        return (T) byClass.get(type);
    }

    private static String normalizeId(String id) {
        return id.trim().toLowerCase(Locale.ROOT);
    }
}
