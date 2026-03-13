package com.github.imdmk.playtime.core.injector.processor.processors;

import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessorContext;
import org.panda_lang.utilities.inject.Resources;

public final class ServiceProcessor implements ComponentProcessor<Service> {

    @Override
    public Class<Service> annotation() {
        return Service.class;
    }

    @Override
    public void process(
            Object instance,
            Service annotation,
            ComponentProcessorContext context
    ) {
        Resources resources = context.injector().getResources();

        // bind
        resources.on(instance.getClass())
                .assignInstance(instance);

        // bind interfaces
        for (Class<?> interfaces : instance.getClass().getInterfaces()) {
            resources.on(interfaces).assignInstance(instance);
        }
    }
}
