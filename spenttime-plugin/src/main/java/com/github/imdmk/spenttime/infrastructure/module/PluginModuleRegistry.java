package com.github.imdmk.spenttime.infrastructure.module;

import com.github.imdmk.spenttime.Validator;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Holds module types, instantiates them via DI and provides a deterministically sorted view.
 */
public final class PluginModuleRegistry {

    private static final Comparator<PluginModule> MODULE_ORDER = Comparator
            .comparingInt(PluginModule::order)              // Core → Feature → Others
            .thenComparing(m -> m.getClass().getName());    // tie-breaker

    private List<Class<? extends PluginModule>> moduleTypes = List.of();
    private List<PluginModule> modules = List.of();

    /**
     * Replace the current set of module classes (unsorted, uninitialized).
     */
    public <T extends PluginModule> void setModuleTypes(@NotNull List<Class<? extends T>> types) {
        Validator.notNull(types, "types cannot be null");
        // defensive copy
        this.moduleTypes = List.copyOf(types);
        // reset instances
        this.modules = List.of();
    }

    /**
     * Instantiate and sort modules deterministically. Idempotent with respect to the current types.
     * Previous instances are discarded.
     */
    public void instantiateAndSort(@NotNull Injector injector) {
        Validator.notNull(injector, "injector cannot be null");

        final List<PluginModule> created = new ArrayList<>(this.moduleTypes.size());
        for (Class<? extends PluginModule> type : this.moduleTypes) {
            created.add(injector.newInstanceWithFields(type));
        }
        created.sort(MODULE_ORDER);
        this.modules = List.copyOf(created);
    }

    /** Immutable, deterministically sorted view of module instances. */
    public List<PluginModule> modules() {
        return Collections.unmodifiableList(this.modules);
    }

    /** Clear types and instances. */
    public void clear() {
        this.moduleTypes = List.of();
        this.modules = List.of();
    }
}
