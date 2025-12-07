package com.github.imdmk.playtime.shared.adventure;

import com.github.imdmk.playtime.shared.Validator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility for applying {@link AdventurePlaceholders} to {@link Component} trees or plain strings.
 * <p>Stateless and thread-safe.</p>
 */
public final class AdventureFormatter {

    private AdventureFormatter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Applies placeholders to a plain string and returns a formatted {@link Component}.
     *
     * @param input plain text input
     * @param placeholders placeholders to apply
     * @return formatted component
     */
    public static @NotNull Component format(@NotNull String input, @NotNull AdventurePlaceholders placeholders) {
        Validator.notNull(input, "input");
        return format(AdventureComponents.text(input), placeholders);
    }

    /**
     * Applies placeholders to each {@link Component} in a list.
     *
     * @param components list of components
     * @param placeholders placeholders to apply
     * @return formatted components
     */
    public static @NotNull List<Component> format(@NotNull List<Component> components, @NotNull AdventurePlaceholders placeholders) {
        Validator.notNull(components, "components");
        return components.stream()
                .map(component -> format(component, placeholders))
                .collect(Collectors.toList());
    }

    /**
     * Applies placeholders to a single {@link Component}.
     *
     * @param input component to format
     * @param placeholders placeholders to apply
     * @return formatted component
     */
    public static @NotNull Component format(@NotNull Component input, @NotNull AdventurePlaceholders placeholders) {
        Validator.notNull(input, "input");
        Validator.notNull(placeholders, "placeholders");

        if (placeholders.isEmpty()) {
            return input;
        }

        // Sort keys by descending length to avoid substring overlap
        List<Map.Entry<String, Component>> ordered = placeholders.asMap().entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Component>>comparingInt(e -> e.getKey().length()).reversed())
                .collect(Collectors.toList());

        Component out = input;
        for (final Map.Entry<String, Component> e : ordered) {
            var key = e.getKey();
            var replacement = e.getValue();

            var config = TextReplacementConfig.builder()
                    .matchLiteral(key)
                    .replacement(replacement)
                    .build();

            out = out.replaceText(config);
        }

        return out;
    }
}
