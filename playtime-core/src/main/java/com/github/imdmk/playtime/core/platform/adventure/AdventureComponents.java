package com.github.imdmk.playtime.core.platform.adventure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class AdventureComponents {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private AdventureComponents() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static Component text(CharSequence text) {
        return MINI_MESSAGE.deserialize(text.toString());
    }

    public static List<Component> text(CharSequence... texts) {
        List<Component> out = new ArrayList<>(texts.length);
        for (CharSequence text : texts) {
            out.add(MINI_MESSAGE.deserialize(text.toString()));
        }

        return List.copyOf(out);
    }

    public static List<Component> text(Iterable<? extends CharSequence> texts) {
        List<Component> out = new ArrayList<>();
        for (CharSequence text : texts) {
            out.add(MINI_MESSAGE.deserialize(text.toString()));
        }

        return List.copyOf(out);
    }

    public static Component withoutItalics(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }

    public static Component withoutItalics(CharSequence text) {
        return withoutItalics(text(text));
    }

    public static Component withoutItalics(ComponentLike like) {
        return like.asComponent().decoration(TextDecoration.ITALIC, false);
    }

    public static List<Component> withoutItalics(String... strings) {
        List<Component> out = new ArrayList<>();
        for (String string : strings) {
            out.add(withoutItalics(string));
        }

        return List.copyOf(out);
    }

    public static String serialize(Component component) {
        return MINI_MESSAGE.serialize(component);
    }

    public static List<String> serialize(Collection<? extends ComponentLike> components) {
        List<String> out = new ArrayList<>(components.size());
        for (ComponentLike component : components) {
            out.add(MINI_MESSAGE.serialize(component.asComponent()));
        }

        return List.copyOf(out);
    }

    public static String serializeJoined(Collection<? extends ComponentLike> components, CharSequence delimiter) {
        List<String> serialized = new ArrayList<>(components.size());
        for (ComponentLike component : components) {
            serialized.add(MINI_MESSAGE.serialize(component.asComponent()));
        }

        return String.join(delimiter, serialized);
    }

    public static MiniMessage miniMessage() {
        return MINI_MESSAGE;
    }
}
