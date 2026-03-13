package com.github.imdmk.playtime.core.injector.processor.processors.placeholder;

import com.github.imdmk.playtime.core.injector.annotations.placeholder.Placeholder;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessorContext;
import com.github.imdmk.playtime.core.platform.placeholder.PlaceholderService;
import com.github.imdmk.playtime.core.platform.placeholder.PluginPlaceholder;

public final class PlaceholderProcessor implements ComponentProcessor<Placeholder> {

    private final PlaceholderService placeholderService;

    public PlaceholderProcessor(PlaceholderService placeholderService) {
        this.placeholderService = placeholderService;
    }

    @Override
    public Class<Placeholder> annotation() {
        return Placeholder.class;
    }

    @Override
    public void process(
            Object instance,
            Placeholder annotation,
            ComponentProcessorContext context
    ) {
        PluginPlaceholder placeholder = requireInstance(
                instance,
                PluginPlaceholder.class,
                Placeholder.class
        );

        placeholderService.register(placeholder);
    }
}
