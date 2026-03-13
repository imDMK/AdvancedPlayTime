package com.github.imdmk.playtime.core.injector.processor.processors;

import com.github.imdmk.playtime.core.database.DatabaseManager;
import com.github.imdmk.playtime.core.injector.annotations.Database;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessorContext;

import java.sql.SQLException;

public final class DatabaseProcessor implements ComponentProcessor<Database> {

    @Override
    public Class<Database> annotation() {
        return Database.class;
    }

    @Override
    public void process(
            Object instance,
            Database annotation,
            ComponentProcessorContext context
    ) {
        DatabaseManager bootstrap = requireInstance(
                instance,
                DatabaseManager.class,
                Database.class
        );

        try {
            bootstrap.start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        context.injector().getResources()
                .on(DatabaseManager.class)
                .assignInstance(bootstrap);
    }
}
