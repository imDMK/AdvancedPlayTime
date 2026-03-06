package com.github.imdmk.playtime.core.platform.adventure;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.text.Component;

public final class AdventureComponentSerializer implements ObjectSerializer<Component> {

    @Override
    public boolean supports(Class<? super Component> type) {
        return Component.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(Component component, SerializationData data, GenericsDeclaration generics) {
        data.setValue(AdventureComponents.serialize(component));
    }

    @Override
    public Component deserialize(DeserializationData data, GenericsDeclaration generics) {
        return AdventureComponents.text(data.getValue(String.class));
    }
}
