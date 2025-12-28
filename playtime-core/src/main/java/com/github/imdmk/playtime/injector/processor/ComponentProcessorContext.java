package com.github.imdmk.playtime.injector.processor;

import com.github.imdmk.playtime.shared.validate.Validator;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

public record ComponentProcessorContext(@NotNull Injector injector) {

    public ComponentProcessorContext {
        Validator.notNull(injector, "injector");
    }
}
