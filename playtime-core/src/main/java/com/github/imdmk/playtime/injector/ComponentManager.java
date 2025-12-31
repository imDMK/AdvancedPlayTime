package com.github.imdmk.playtime.injector;

import com.github.imdmk.playtime.injector.annotations.NoneAnnotation;
import com.github.imdmk.playtime.injector.priority.Priority;
import com.github.imdmk.playtime.injector.priority.PriorityProvider;
import com.github.imdmk.playtime.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.injector.processor.ComponentProcessorContext;
import com.github.imdmk.playtime.injector.processor.FunctionalComponentProcessor;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ComponentManager {

    private static final PriorityProvider DEFAULT_PRIORITY = (priority -> Priority.NORMAL);

    private final Injector injector;

    private final ComponentQueue container;
    private final ComponentScanner scanner;

    private final Map<Class<? extends Annotation>, ComponentProcessor<?>> processors = new ConcurrentHashMap<>();

    public ComponentManager(@NotNull Injector injector, @NotNull String basePackage) {
        this.injector = injector;

        this.container = new ComponentQueue(DEFAULT_PRIORITY);
        this.scanner = new ComponentScanner(basePackage);
    }

    public ComponentManager setPriorityProvider(@NotNull PriorityProvider provider) {
        container.setPriorityProvider(provider);
        return this;
    }

    public ComponentManager addProcessor(@NotNull ComponentProcessor<?> processor) {
        processors.put(processor.annotation(), processor);
        return this;
    }

    public ComponentManager addProcessor(@NotNull Class<? extends ComponentProcessor<?>> processorClass) {
        return addProcessor(injector.newInstance(processorClass));
    }

    public <A extends Annotation> ComponentManager onProcess(
            @NotNull Class<A> annotation,
            @NotNull ComponentFunctional<Object, A> consumer
    ) {
        return onProcess(annotation, Object.class, consumer);
    }

    public <T, A extends Annotation> ComponentManager onProcess(
            @NotNull Class<A> annotation,
            @NotNull Class<T> targetType,
            @NotNull ComponentFunctional<T, A> consumer
    ) {
        return addProcessor(new FunctionalComponentProcessor<>(annotation, targetType, consumer));
    }

    public ComponentManager onProcess(
            @NotNull ComponentFunctional<Object, NoneAnnotation> consumer
    ) {
        return onProcess(NoneAnnotation.class, Object.class, consumer);
    }

    public void scanAll() {
        for (final Class<? extends Annotation> annotation : processors.keySet()) {
            scanner.scan(annotation).forEach(container::add);
        }
    }

    public void processAll() {
        for (final ComponentProcessor<?> processor : processors.values()) {
            container.drain(processor.annotation())
                    .forEach(component -> process(processor, component));
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends Annotation> void process(
            ComponentProcessor<A> processor,
            Component<?> component
    ) {
        processor.process((Component<A>) component, new ComponentProcessorContext(injector));
    }

}
