package com.github.imdmk.playtime.infrastructure.module;

import com.github.imdmk.playtime.shared.Validator;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Maintains the registry of all {@link PluginModule} classes and their instantiated, sorted instances.
 * <p>
 * This registry is responsible for:
 * <ul>
 *     <li>holding the declared module types,</li>
 *     <li>instantiating them via dependency injection,</li>
 *     <li>sorting them deterministically according to {@link PluginModule#order()}.</li>
 * </ul>
 * <p>
 * The registry itself is stateless between runs: every call to
 * {@link #instantiateAndSort(Injector)} rebuilds the internal module list from the current types.
 * <p>
 * <b>Thread-safety:</b> This class is not thread-safe and must be accessed from the main server thread.
 */
public final class PluginModuleRegistry {

    /** Comparator defining deterministic module ordering: lower {@link PluginModule#order()} first, then by class name. */
    private static final Comparator<PluginModule> MODULE_ORDER = Comparator
            .comparingInt(PluginModule::order)
            .thenComparing(m -> m.getClass().getName());

    private List<Class<? extends PluginModule>> moduleTypes = List.of();
    private List<PluginModule> modules = List.of();

    /**
     * Replaces the current set of module types with a new, uninitialized list.
     * <p>
     * This method does not instantiate modules; call {@link #instantiateAndSort(Injector)} afterwards
     * to build and sort the instances.
     *
     * @param types the new list of module classes (must not be null)
     * @param <T>   the module type extending {@link PluginModule}
     * @throws NullPointerException if {@code types} is null
     */
    public <T extends PluginModule> void setModuleTypes(@NotNull List<Class<? extends T>> types) {
        Validator.notNull(types, "types cannot be null");
        // defensive copy
        moduleTypes = List.copyOf(types);
        // reset instances
        modules = List.of();
    }

    /**
     * Instantiates all declared module classes using the provided {@link Injector}
     * and sorts them deterministically by {@link PluginModule#order()} and class name.
     * <p>
     * This operation is idempotent for the current module types; previous instances are discarded.
     *
     * @param injector the dependency injector used to construct module instances (never null)
     * @throws NullPointerException if {@code injector} is null
     */
    public void instantiateAndSort(@NotNull Injector injector) {
        Validator.notNull(injector, "injector cannot be null");

        final List<PluginModule> created = new ArrayList<>(moduleTypes.size());
        for (Class<? extends PluginModule> type : moduleTypes) {
            created.add(injector.newInstance(type));
        }

        created.sort(MODULE_ORDER);
        modules = List.copyOf(created);
    }

    /**
     * Returns an immutable, deterministically sorted view of all instantiated modules.
     * <p>
     * The returned list is guaranteed to be ordered by {@link PluginModule#order()} ascending,
     * with a lexicographic tiebreaker on the class name for consistency across JVM runs.
     *
     * @return an unmodifiable list of module instances (never null, may be empty)
     */
    public List<PluginModule> modules() {
        return Collections.unmodifiableList(modules);
    }

    /**
     * Clears all registered module types and instances.
     * <p>
     * After calling this method, the registry returns to its initial empty state.
     */
    public void clear() {
        moduleTypes = List.of();
        modules = List.of();
    }
}
