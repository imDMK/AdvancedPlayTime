package com.github.imdmk.playtime.injector;

import com.github.imdmk.playtime.injector.processor.ComponentPostProcessor;
import com.github.imdmk.playtime.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.injector.processor.ComponentProcessorContext;
import com.github.imdmk.playtime.injector.processor.ProcessorContainer;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public final class ComponentManager {

    private final Injector injector;
    private final ComponentScanner scanner;
    private final ComponentSorter sorter;

    private final List<ProcessorContainer<?>> processors = new ArrayList<>();
    private final List<ComponentPostProcessor> postProcessors = new ArrayList<>();
    private final List<Component<?>> components = new ArrayList<>();

    public ComponentManager(@NotNull Injector injector, @NotNull String basePackage) {
        this.injector = injector;
        this.scanner = new ComponentScanner(basePackage);
        this.sorter = new ComponentSorter();
    }

    public ComponentManager addProcessor(@NotNull ProcessorContainer<?> container) {
        processors.add(container);
        return this;
    }

    public ComponentManager addProcessors(@NotNull List<ProcessorContainer<?>> containers) {
        containers.forEach(this::addProcessor);
        return this;
    }

    public ComponentManager addPostProcessor(@NotNull ComponentPostProcessor postProcessor) {
        postProcessors.add(postProcessor);
        return this;
    }

    public ComponentManager addPostProcessors(@NotNull List<ComponentPostProcessor> postProcessors) {
        postProcessors.forEach(this::addPostProcessor);
        return this;
    }

    public void scanAll() {
        for (final ProcessorContainer<?> container : processors) {
            components.addAll(scanner.scan(container.processor().annotation()));
        }
    }

    public void processAll() {
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

        for (final ProcessorContainer<?> container : processors) {
            if (container.annotationType() != component.annotation().annotationType()) {
                continue;
            }

            final ComponentProcessor<A> processor = (ComponentProcessor<A>) container.processor();

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


