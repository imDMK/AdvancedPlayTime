package com.github.imdmk.playtime.platform.adventure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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

        final MiniMessage miniMessage = AdventureComponents.miniMessage();

        String raw = miniMessage.serialize(input);
        for (final var entry : placeholders.asMap().entrySet()) {
            raw = raw.replace(entry.getKey(), miniMessage.serialize(entry.getValue()));
        }

        return miniMessage.deserialize(raw);
    }

}
