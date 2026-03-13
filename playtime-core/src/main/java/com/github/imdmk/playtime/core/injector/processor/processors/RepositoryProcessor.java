package com.github.imdmk.playtime.core.injector.processor.processors;

import com.github.imdmk.playtime.core.database.repository.RepositoryInitializationException;
import com.github.imdmk.playtime.core.database.repository.ormlite.OrmLiteRepository;
import com.github.imdmk.playtime.core.injector.annotations.Repository;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessorContext;

public final class RepositoryProcessor implements ComponentProcessor<Repository> {

    @Override
    public Class<Repository> annotation() {
        return Repository.class;
    }

    @Override
    public void process(
            Object instance,
            Repository annotation,
            ComponentProcessorContext context
    ) {
        OrmLiteRepository<?, ?> repository = requireInstance(
                instance,
                OrmLiteRepository.class,
                Repository.class
        );

        try {
            repository.start();
        } catch (RepositoryInitializationException e) {
            throw new RuntimeException(e);
        }

        context.injector().getResources()
                .on(repository.getClass())
                .assignInstance(repository);
    }
}
