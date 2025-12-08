package com.github.imdmk.playtime.shared.adventure;

import com.github.imdmk.playtime.platform.adventure.AdventureFormatter;
import com.github.imdmk.playtime.platform.adventure.AdventurePlaceholders;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdventureFormatterTest {

    @Test
    void format_string_shouldApplyPlaceholder() {
        AdventurePlaceholders ph = AdventurePlaceholders.builder()
                .with("%name%", "DMK")
                .build();

        Component result = AdventureFormatter.format("Hello %name%", ph);

        assertThat(result.toString()).contains("DMK");
    }

    @Test
    void format_component_shouldApplyPlaceholder() {
        Component base = Component.text("XP: %xp%");
        AdventurePlaceholders ph = AdventurePlaceholders.builder()
                .with("%xp%", "1500")
                .build();

        Component out = AdventureFormatter.format(base, ph);

        assertThat(out.toString()).contains("1500");
    }

    @Test
    void format_list_shouldApplyPlaceholdersToAll() {
        AdventurePlaceholders ph = AdventurePlaceholders.builder()
                .with("%v%", "VALUE")
                .build();

        List<Component> out = AdventureFormatter.format(
                List.of(Component.text("A %v%"), Component.text("B %v%")),
                ph
        );

        assertThat(out).hasSize(2);
        assertThat(out.get(0).toString()).contains("VALUE");
        assertThat(out.get(1).toString()).contains("VALUE");
    }

    @Test
    void format_shouldHandleOverlappingKeysByLength() {
        AdventurePlaceholders ph = AdventurePlaceholders.builder()
                .with("%player%", "DOM")
                .with("%player_name%", "DMK")
                .build();

        Component base = Component.text("Hello %player_name% !");
        Component out = AdventureFormatter.format(base, ph);

        assertThat(out.toString()).contains("DMK");
        assertThat(out.toString()).doesNotContain("DOM");
    }

    @Test
    void format_emptyPlaceholders_shouldReturnSameComponent() {
        Component c = Component.text("Test");
        Component out = AdventureFormatter.format(c, AdventurePlaceholders.empty());

        assertThat(out).isSameAs(c);
    }
}

