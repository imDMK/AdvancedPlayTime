package com.github.imdmk.playtime.injector;

import com.github.imdmk.playtime.injector.processor.ComponentPostProcessor;
import com.github.imdmk.playtime.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.injector.processor.ComponentProcessorContext;
import com.github.imdmk.playtime.injector.processor.ProcessorContainer;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ComponentManager {

    private final Injector injector;
    private final ComponentScanner scanner;
    private final ComponentSorter sorter;

    // annotation -> processor container
    private final Map<Class<? extends Annotation>, ProcessorContainer<?>> processors = new HashMap<>();
    private final List<ComponentPostProcessor> postProcessors = new ArrayList<>();
    private final List<Component<?>> components = new ArrayList<>();

    private boolean scanned = false;

    public ComponentManager(@NotNull Injector injector, @NotNull String basePackage) {
        this.injector = injector;
        this.scanner = new ComponentScanner(basePackage);
        this.sorter = new ComponentSorter();
    }

    public ComponentManager addProcessor(@NotNull ProcessorContainer<?> container) {
        if (scanned) {
            throw new IllegalStateException("Cannot add processors after scanAll()");
        }

        final Class<? extends Annotation> type = container.annotationType();
        if (processors.containsKey(type)) {
            throw new IllegalStateException(
                    "Processor already registered for annotation: " + type.getName()
            );
        }

        processors.put(type, container);
        return this;
    }

    public ComponentManager addProcessors(@NotNull Collection<ProcessorContainer<?>> containers) {
        containers.forEach(this::addProcessor);
        return this;
    }

    public ComponentManager addPostProcessor(@NotNull ComponentPostProcessor postProcessor) {
        postProcessors.add(postProcessor);
        return this;
    }

    public void scanAll() {
        if (scanned) {
            throw new IllegalStateException("scanAll() already called");
        }

        for (final ProcessorContainer<?> container : processors.values()) {
            components.addAll(scanner.scan(container.annotationType()));
        }

        scanned = true;
    }

    public void processAll() {
        if (!scanned) {
            throw new IllegalStateException("scanAll() must be called before processAll()");
        }

        final ComponentProcessorContext context = new ComponentProcessorContext(injector);

        sorter.sort(components);

        for (final Component<?> component : components) {
            processComponent(component, context);
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends Annotation> void processComponent(
            Component<A> component,
            ComponentProcessorContext context
    ) {
        component.createInstance(injector);

        final ProcessorContainer<?> raw = processors.get(component.annotation().annotationType());
        if (raw != null) {
            final ProcessorContainer<A> container = (ProcessorContainer<A>) raw;
            final ComponentProcessor<A> processor = container.processor();

            processor.process(
                    component.instance(),
                    component.annotation(),
                    context
            );
        }

        for (final ComponentPostProcessor post : postProcessors) {
            post.postProcess(component.instance(), context);
        }
    }
}
