package com.github.imdmk.spenttime.shared.adventure;

import com.github.imdmk.spenttime.Validator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Small utility for working with Adventure {@link Component}s via MiniMessage.
 * Pure, platform-agnostic (no Bukkit types).
 */
public final class AdventureComponents {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private AdventureComponents() {
        throw new UnsupportedOperationException("This is utility class and cannot be instantiated.");
    }

    public static @NotNull Component notItalic(@NotNull String text) {
        Validator.notNull(text, "text cannot be null");
        return MINI_MESSAGE.deserialize(text).decoration(TextDecoration.ITALIC, false);
    }

    public static @NotNull Component notItalic(@NotNull Component component) {
        Validator.notNull(component, "component cannot be null");
        return component.decoration(TextDecoration.ITALIC, false);
    }

    public static @NotNull List<Component> notItalic(@NotNull String... texts) {
        Validator.notNull(texts, "texts cannot be null");
        return Arrays.stream(texts).map(AdventureComponents::notItalic).toList();
    }

    public static @NotNull List<Component> notItalic(@NotNull List<String> texts) {
        Validator.notNull(texts, "texts cannot be null");
        return texts.stream().map(AdventureComponents::notItalic).toList();
    }

    public static @NotNull Component text(@NotNull String text) {
        Validator.notNull(text, "text cannot be null");
        return MINI_MESSAGE.deserialize(text);
    }

    public static @NotNull List<Component> text(@NotNull String... texts) {
        Validator.notNull(texts, "texts cannot be null");
        return Arrays.stream(texts).map(MINI_MESSAGE::deserialize).toList();
    }

    public static @NotNull List<Component> text(@NotNull List<String> texts) {
        Validator.notNull(texts, "texts cannot be null");
        return texts.stream().map(MINI_MESSAGE::deserialize).toList();
    }

    public static @NotNull String serialize(@NotNull Component component) {
        Validator.notNull(component, "component cannot be null");
        return MINI_MESSAGE.serialize(component);
    }
}
