package com.github.imdmk.playtime.shared.adventure;

import com.github.imdmk.playtime.platform.adventure.AdventurePlaceholders;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdventurePlaceholdersTest {

    @Test
    void empty_shouldReturnSingleton() {
        AdventurePlaceholders p1 = AdventurePlaceholders.empty();
        AdventurePlaceholders p2 = AdventurePlaceholders.empty();

        assertThat(p1).isSameAs(p2);
        assertThat(p1.size()).isZero();
    }

    @Test
    void builder_withStringAndComponent_shouldStoreMapping() {
        Component value = Component.text("Hello");
        AdventurePlaceholders ph = AdventurePlaceholders.builder()
                .with("%player%", value)
                .build();

        assertThat(ph.asMap())
                .containsEntry("%player%", value);
    }

    @Test
    void builder_withStringStringShouldConvertToTextComponent() {
        AdventurePlaceholders ph = AdventurePlaceholders.builder()
                .with("%x%", "Hello")
                .build();

        Component comp = ph.asMap().get("%x%");
        assertThat(comp).isNotNull();
        assertThat(comp.toString()).contains("Hello");
    }

    @Test
    void builder_withObjectShouldConvertToStringComponent() {
        AdventurePlaceholders ph = AdventurePlaceholders.builder()
                .with("%n%", 123)
                .build();

        Component comp = ph.asMap().get("%n%");
        assertThat(comp.toString()).contains("123");
    }

    @Test
    void builder_withOtherPlaceholdersShouldMerge() {
        AdventurePlaceholders base = AdventurePlaceholders.builder()
                .with("%a%", "A")
                .build();

        AdventurePlaceholders merged = AdventurePlaceholders.builder()
                .with("%b%", "B")
                .with(base)
                .build();

        assertThat(merged.asMap())
                .containsEntry("%a%", Component.text("A"))
                .containsEntry("%b%", Component.text("B"));
    }

    @Test
    void asMap_shouldBeUnmodifiable() {
        AdventurePlaceholders ph = AdventurePlaceholders.builder()
                .with("%x%", "1")
                .build();

        Map<String, Component> map = ph.asMap();

        assertThrows(UnsupportedOperationException.class, () -> map.put("%y%", Component.text("2")));
    }
}

