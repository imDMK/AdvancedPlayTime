package com.github.imdmk.playtime.core.injector;

import com.github.imdmk.playtime.core.injector.processor.ComponentPostProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessorContext;
import com.github.imdmk.playtime.core.injector.processor.ProcessorContainer;
import org.panda_lang.utilities.inject.DependencyInjectionException;
import org.panda_lang.utilities.inject.Injector;

import java.lang.annotation.Annotation;
import java.util.*;

public final class ComponentManager {

    private final Injector injector;
    private final ComponentScanner scanner;
    private final ComponentSorter sorter;

    // annotation -> processor container
    private final Map<Class<? extends Annotation>, ProcessorContainer<?>> processors = new LinkedHashMap<>();
    private final List<ComponentPostProcessor> postProcessors = new ArrayList<>();
    private final List<Component<?>> components = new ArrayList<>();

    private boolean scanned = false;

    public ComponentManager(Injector injector, String basePackage) {
        this.injector = injector;
        this.scanner = new ComponentScanner(basePackage);
        this.sorter = new ComponentSorter();
    }

    public ComponentManager addProcessor(ProcessorContainer<?> container) {
        if (scanned) {
            throw new IllegalStateException("Cannot add processors after scanAll()");
        }

        Class<? extends Annotation> type = container.annotationType();
        if (processors.containsKey(type)) {
            throw new IllegalStateException(
                    "Processor already registered for annotation: " + type.getName()
            );
        }

        processors.put(type, container);
        return this;
    }

    public ComponentManager addProcessors(Collection<ProcessorContainer<?>> containers) {
        containers.forEach(this::addProcessor);
        return this;
    }

    public ComponentManager addPostProcessor(ComponentPostProcessor postProcessor) {
        postProcessors.add(postProcessor);
        return this;
    }

    public ComponentManager addPostProcessors(Collection<ComponentPostProcessor> postProcessors) {
        postProcessors.forEach(this::addPostProcessor);
        return this;
    }

    public void scanAll() {
        if (scanned) {
            throw new IllegalStateException("scanAll() already called");
        }

        for (ProcessorContainer<?> container : processors.values()) {
            components.addAll(scanner.scan(container.annotationType()));
        }

        scanned = true;
    }

    public void processAll() {
        if (!scanned) {
            throw new IllegalStateException("scanAll() must be called before processAll()");
        }

        ComponentProcessorContext context = new ComponentProcessorContext(injector);

        sorter.sort(components);

        for (Component<?> component : components) {
            processComponent(component, context);
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends Annotation> void processComponent(
            Component<A> component,
            ComponentProcessorContext context
    ) {
        try {
            component.createInstance(injector);
        } catch (DependencyInjectionException e) {
            throw new IllegalStateException(
                    "Failed to create instance of " + component.type().getName(), e
            );
        }

        ProcessorContainer<?> raw = processors.get(component.annotation().annotationType());
        if (raw == null) {
            throw new IllegalStateException(
                    "No processor found for annotation type: " + component.annotation().annotationType().getName()
            );
        }

        ProcessorContainer<A> container = (ProcessorContainer<A>) raw;
        ComponentProcessor<A> processor = container.processor();

        processor.process(
                component.instance(),
                component.annotation(),
                context
        );

        for (ComponentPostProcessor post : postProcessors) {
            post.postProcess(component.instance(), context);
        }
    }
}