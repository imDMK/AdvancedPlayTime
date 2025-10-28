package com.github.imdmk.spenttime.shared.adventure;

import com.github.imdmk.spenttime.shared.Validator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utilities for working with Adventure {@link Component}s via MiniMessage.
 * Platform-agnostic (no Bukkit types). Thread-safe and stateless.
 *
 * <p>Notes:
 * <ul>
 *   <li>All returned collections are unmodifiable.</li>
 *   <li>Accepts {@link CharSequence} for flexibility.</li>
 * </ul>
 *
 * <pre>
 *   Component c = AdventureComponents.text("&lt;red&gt;Hello");
 *   Component plain = AdventureComponents.withoutItalics(c);
 * </pre>
 */
public final class AdventureComponents {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private AdventureComponents() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Deserializes a MiniMessage-formatted text into a {@link Component}.
     *
     * @param text the MiniMessage-formatted text
     * @return the deserialized component
     */
    public static @NotNull Component text(@NotNull CharSequence text) {
        Validator.notNull(text, "text cannot be null");
        return MINI_MESSAGE.deserialize(text.toString());
    }

    /**
     * Deserializes multiple MiniMessage-formatted texts into a list of {@link Component}s.
     *
     * @param texts array of MiniMessage-formatted texts
     * @return an unmodifiable list of deserialized components
     */
    public static @NotNull List<Component> text(@NotNull CharSequence... texts) {
        Validator.notNull(texts, "texts cannot be null");

        final List<Component> out = new ArrayList<>(texts.length);
        for (CharSequence text : texts) {
            out.add(MINI_MESSAGE.deserialize(text.toString()));
        }

        return List.copyOf(out);
    }

    /**
     * Deserializes a collection of MiniMessage-formatted texts into {@link Component}s.
     *
     * @param texts iterable of MiniMessage-formatted texts
     * @return an unmodifiable list of deserialized components
     */
    public static @NotNull List<Component> text(@NotNull Iterable<? extends CharSequence> texts) {
        Validator.notNull(texts, "texts cannot be null");

        final List<Component> out = new ArrayList<>();
        for (CharSequence text : texts) {
            Validator.notNull(text, "texts contains null element");
            out.add(MINI_MESSAGE.deserialize(text.toString()));
        }

        return List.copyOf(out);
    }

    /**
     * Returns a copy of the given component with italics disabled.
     *
     * @param component the source component
     * @return a new component without italics
     */
    public static @NotNull Component withoutItalics(@NotNull Component component) {
        Validator.notNull(component, "component cannot be null");
        return component.decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Deserializes a MiniMessage-formatted text and removes italics.
     *
     * @param text the MiniMessage-formatted text
     * @return a deserialized component without italics
     */
    public static @NotNull Component withoutItalics(@NotNull CharSequence text) {
        return withoutItalics(text(text));
    }

    /**
     * Converts a {@link ComponentLike} into a {@link Component} and removes italics.
     *
     * @param like the source component-like object
     * @return a new component without italics
     */
    public static @NotNull Component withoutItalics(@NotNull ComponentLike like) {
        Validator.notNull(like, "component cannot be null");
        return like.asComponent().decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Disables italics for all given components.
     *
     * @param components iterable of component-like objects
     * @return an unmodifiable list of components with italics disabled
     */
    public static @NotNull List<Component> withoutItalics(@NotNull Iterable<? extends ComponentLike> components) {
        Validator.notNull(components, "components cannot be null");

        final List<Component> out = new ArrayList<>();
        for (final ComponentLike component : components) {
            Validator.notNull(component, "components contains null element");
            out.add(withoutItalics(component));
        }

        return List.copyOf(out);
    }

    /**
     * Serializes a {@link Component} into a MiniMessage-formatted string.
     *
     * @param component the component to serialize
     * @return the serialized MiniMessage string
     */
    public static @NotNull String serialize(@NotNull Component component) {
        Validator.notNull(component, "component cannot be null");
        return MINI_MESSAGE.serialize(component);
    }

    /**
     * Serializes multiple components into MiniMessage-formatted strings.
     *
     * @param components collection of component-like objects
     * @return an unmodifiable list of serialized strings
     */
    public static @NotNull List<String> serialize(@NotNull Collection<? extends ComponentLike> components) {
        Validator.notNull(components, "components cannot be null");

        final List<String> out = new ArrayList<>(components.size());
        for (final ComponentLike component : components) {
            Validator.notNull(component, "components contains null element");
            out.add(MINI_MESSAGE.serialize(component.asComponent()));
        }

        return List.copyOf(out);
    }

    /**
     * Serializes multiple components and joins them with the given delimiter.
     *
     * @param components collection of component-like objects
     * @param delimiter  string separator between serialized components
     * @return a single joined MiniMessage string
     */
    public static @NotNull String serializeJoined(@NotNull Collection<? extends ComponentLike> components,
                                                  @NotNull CharSequence delimiter) {
        Validator.notNull(components, "components cannot be null");
        Validator.notNull(delimiter, "delimiter cannot be null");

        final List<String> serialized = new ArrayList<>(components.size());
        for (final ComponentLike component : components) {
            Validator.notNull(component, "components contains null element");
            serialized.add(MINI_MESSAGE.serialize(component.asComponent()));
        }

        return String.join(delimiter, serialized);
    }
}
