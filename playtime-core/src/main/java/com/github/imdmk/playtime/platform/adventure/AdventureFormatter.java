package com.github.imdmk.playtime.platform.adventure;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class AdventureFormatter {

    private AdventureFormatter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Component format(@NotNull String input, @NotNull AdventurePlaceholders placeholders) {
        return format(AdventureComponents.text(input), placeholders);
    }

    public static List<Component> format(@NotNull List<Component> components, @NotNull AdventurePlaceholders placeholders) {
        return components.stream()
                .map(component -> format(component, placeholders))
                .collect(Collectors.toList());
    }

    public static Component format(@NotNull Component input, @NotNull AdventurePlaceholders placeholders) {
        if (placeholders.isEmpty()) {
            return input;
        }

        // Sort keys by descending length to avoid substring overlap
        final List<Map.Entry<String, Component>> ordered = placeholders.asMap().entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Component>>comparingInt(e -> e.getKey().length()).reversed())
                .toList();

        return input.replaceText(builder -> {
            for (var entry : ordered) {
                builder.matchLiteral(entry.getKey()).replacement(entry.getValue());
            }
        });
    }
}
