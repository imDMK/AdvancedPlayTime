package com.github.imdmk.playtime.injector.processor;

import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

public record ComponentProcessorContext(@NotNull Injector injector) {
}
