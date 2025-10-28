package com.github.imdmk.spenttime.shared.message;

import com.eternalcode.multification.adventure.PlainComponentSerializer;
import com.github.imdmk.spenttime.shared.Validator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Formats {@link Component} trees by replacing literal placeholder keys with provided values.
 * <p>
 * Supports both plain-text substitutions and rich {@link Component} substitutions
 * (without losing styling/hover/click events).
 * <p>
 * <strong>Thread-safety:</strong> This class is not thread-safe and is intended to be used
 * as a short-lived builder per message. Do not share instances across threads without external
 * synchronization.
 */
public final class MessageFormatter {

    private static final PlainComponentSerializer PLAIN_SERIALIZER = new PlainComponentSerializer();

    /** Plain-text substitutions only (literal placeholder → plain string). */
    private final Map<String, String> textPlaceholders = new LinkedHashMap<>();

    /** Rich-component substitutions (literal placeholder → component). */
    private final Map<String, Component> componentPlaceholders = new LinkedHashMap<>();

    /**
     * Adds a placeholder replacement using raw strings.
     *
     * @param from placeholder key to replace (matched literally)
     * @param to   replacement value (plain text)
     * @return this formatter instance for chaining
     * @throws NullPointerException if any argument is null
     */
    @Contract("_,_ -> this")
    public @NotNull MessageFormatter placeholder(@NotNull String from, @NotNull String to) {
        Validator.notNull(from, "from cannot be null");
        Validator.notNull(to, "to cannot be null");
        this.textPlaceholders.put(from, to);
        return this;
    }

    /**
     * Adds a placeholder replacement by joining a sequence with ", ".
     * <p>
     * Note: null elements are not allowed and will cause {@link NullPointerException}.
     *
     * @param from      placeholder key to replace
     * @param sequences values to join as replacement
     * @return this formatter instance for chaining
     * @throws NullPointerException if any argument or element is null
     */
    @Contract("_,_ -> this")
    public @NotNull MessageFormatter placeholder(@NotNull String from, @NotNull Iterable<? extends CharSequence> sequences) {
        Validator.notNull(sequences, "sequences cannot be null");
        return this.placeholder(from, String.join(", ", sequences));
    }

    /**
     * Adds a placeholder replacement for any object via its {@code toString()}.
     *
     * @param from placeholder key to replace
     * @param to   object whose string value will be used
     * @return this formatter instance for chaining
     * @throws NullPointerException if any argument is null
     */
    @Contract("_,_ -> this")
    public <T> @NotNull MessageFormatter placeholder(@NotNull String from, @NotNull T to) {
        Validator.notNull(to, "to cannot be null");
        return this.placeholder(from, to.toString());
    }

    /**
     * Adds a placeholder replacement using a {@link Component}, preserving its styling and events.
     * No plain serialization is performed.
     *
     * @param from placeholder key to replace (matched literally)
     * @param to   component used as replacement (inserted verbatim)
     * @return this formatter instance for chaining
     * @throws NullPointerException if any argument is null
     */
    @Contract("_,_ -> this")
    public @NotNull MessageFormatter placeholderComponent(@NotNull String from, @NotNull Component to) {
        Validator.notNull(from, "from cannot be null");
        Validator.notNull(to, "to cannot be null");
        this.componentPlaceholders.put(from, to);
        return this;
    }

    /**
     * Adds a placeholder replacement using a {@link Component} but first serializes it to plain text.
     * <p>
     * Use this when you explicitly want to drop formatting from {@code to}.
     *
     * @param from placeholder key to replace
     * @param to   component to serialize to plain text for replacement
     * @return this formatter instance for chaining
     * @throws NullPointerException if any argument is null
     */
    @Contract("_,_ -> this")
    public @NotNull MessageFormatter placeholderPlain(@NotNull String from, @NotNull Component to) {
        Validator.notNull(from, "from cannot be null");
        Validator.notNull(to, "to cannot be null");
        return this.placeholder(from, PLAIN_SERIALIZER.serialize(to));
    }

    /**
     * Applies all defined placeholder replacements to a given {@link Component}.
     * <p>
     * Replacement order is stabilized by applying keys in <strong>descending length</strong>
     * to avoid substring collisions (e.g., {@code {USER}} vs {@code {USERNAME}}).
     *
     * @param component input component
     * @return a new component with placeholders replaced
     * @throws NullPointerException if {@code component} is null
     */
    public @NotNull Component format(@NotNull Component component) {
        Validator.notNull(component, "component cannot be null");

        // Build a unified view: literal → replacement Component
        Map<String, Component> unified = new LinkedHashMap<>(this.componentPlaceholders);
        // convert all plain text placeholders to Components
        for (Map.Entry<String, String> e : this.textPlaceholders.entrySet()) {
            unified.put(e.getKey(), Component.text(e.getValue()));
        }

        if (unified.isEmpty()) {
            return component;
        }

        // Sort keys by descending length to minimize substring clashes
        List<Map.Entry<String, Component>> ordered = unified.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Component>>comparingInt(e -> e.getKey().length()).reversed())
                .collect(Collectors.toList());

        Component out = component;

        // Apply each replacement rule; each call traverses the tree once.
        for (Map.Entry<String, Component> e : ordered) {
            String literal = e.getKey();
            Component replacement = e.getValue();

            TextReplacementConfig config = TextReplacementConfig.builder()
                    .matchLiteral(literal)
                    .replacement(replacement)
                    .build();

            out = out.replaceText(config);
        }

        return out;
    }

    /**
     * Applies replacements to a list of {@link Component}s.
     *
     * @param components list of components to process
     * @return new list with replaced components
     * @throws NullPointerException if {@code components} or any element is null
     */
    public @NotNull List<Component> format(@NotNull List<Component> components) {
        Validator.notNull(components, "components cannot be null");

        List<Component> replaced = new ArrayList<>(components.size());
        for (Component component : components) {
            replaced.add(this.format(component));
        }
        return replaced;
    }

    /**
     * Clears all registered placeholders.
     *
     * @return this formatter instance for chaining
     */
    @Contract("-> this")
    public @NotNull MessageFormatter clear() {
        this.textPlaceholders.clear();
        this.componentPlaceholders.clear();
        return this;
    }
}
