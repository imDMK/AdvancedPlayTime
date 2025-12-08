package com.github.imdmk.playtime.shared.adventure;

import com.github.imdmk.playtime.platform.adventure.AdventureComponents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdventureComponentsTest {

    @Test
    void text_single_shouldDeserialize() {
        Component c = AdventureComponents.text("<red>Hello");

        assertThat(c)
                .extracting(comp -> comp.decoration(TextDecoration.BOLD))
                .isNotNull();
    }

    @Test
    void text_varargs_shouldDeserializeList() {
        List<Component> list = AdventureComponents.text("<green>A", "<blue>B");

        assertThat(list).hasSize(2);
    }

    @Test
    void text_iterable_shouldDeserializeAll() {
        List<Component> list = AdventureComponents.text(List.of("<yellow>X", "<white>Y"));

        assertThat(list).hasSize(2);
    }

    @Test
    void withoutItalics_component_shouldDisableItalic() {
        Component c = AdventureComponents.text("<italic>Hello");
        Component result = AdventureComponents.withoutItalics(c);

        assertThat(result.decoration(TextDecoration.ITALIC)).isEqualTo(TextDecoration.State.FALSE);
    }

    @Test
    void withoutItalics_stringVarargs_shouldDisableItalic() {
        List<Component> out = AdventureComponents.withoutItalics("<italic>A", "<italic>B");

        assertThat(out).hasSize(2);
        out.forEach(c ->
                assertThat(c.decoration(TextDecoration.ITALIC)).isEqualTo(TextDecoration.State.FALSE));
    }

    @Test
    void serialize_shouldReturnMiniMessage() {
        Component c = Component.text("Test");
        String s = AdventureComponents.serialize(c);

        assertThat(s).isEqualTo("Test");
    }
}

