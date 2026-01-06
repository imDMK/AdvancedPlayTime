package com.github.imdmk.playtime.injector;

import com.github.imdmk.playtime.injector.postprocessor.ComponentPostProcessor;
import com.github.imdmk.playtime.injector.priority.Priority;
import com.github.imdmk.playtime.injector.priority.PriorityProvider;
import com.github.imdmk.playtime.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.injector.processor.ComponentProcessorContext;
import com.github.imdmk.playtime.injector.processor.ComponentProcessors;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ComponentManager {

    private final Injector injector;
    private final ComponentQueue queue;
    private final ComponentScanner scanner;

    private final Map<Class<? extends Annotation>, ComponentProcessor<?>> processors = new ConcurrentHashMap<>();
    private final List<ComponentPostProcessor> postProcessors = new ArrayList<>();

    public ComponentManager(@NotNull Injector injector, @NotNull String basePackage) {
        this.injector = injector;
        this.queue = new ComponentQueue(priority -> Priority.NORMAL);
        this.scanner = new ComponentScanner(basePackage);
    }

    public ComponentManager setPriorityProvider(@NotNull PriorityProvider priorityProvider) {
        queue.setPriorityProvider(priorityProvider);
        return this;
    }

    public ComponentManager addProcessor(@NotNull ComponentProcessor<?> processor) {
        processors.put(processor.annotation(), processor);
        return this;
    }

    public ComponentManager addProcessors(@NotNull List<ComponentProcessor<?>> processors) {
        processors.forEach(this::addProcessor);
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
        processors.keySet().forEach(annotation -> scanner.scan(annotation).forEach(queue::add));
    }

    public void processAll() {
        final ComponentProcessorContext context = new ComponentProcessorContext(injector);

        for (final Priority priority : Priority.values()) {
            for (final Map.Entry<Class<? extends Annotation>, ComponentProcessor<?>> entry : processors.entrySet()) {

                final Class<? extends Annotation> annotation = entry.getKey();
                final ComponentProcessor<?> processor = entry.getValue();

                for (final Component<?> component : queue.drain(priority, annotation)) {
                    System.out.println("creating instance componenet of class: " + component.type().getName());
                    component.create(injector);

                    processComponent(processor, component, context);

                    for (final ComponentPostProcessor post : postProcessors) {
                        post.postProcess(component.instance(), context);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends Annotation> void processComponent(
            ComponentProcessor<A> processor,
            Component<?> component,
            ComponentProcessorContext context
    ) {
        processor.process(
                component.instance(),
                (A) component.annotation(),
                context
        );
    }
}

