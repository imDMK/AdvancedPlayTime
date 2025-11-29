package com.github.imdmk.playtime.infrastructure.di;

import org.panda_lang.utilities.inject.annotations.Injectable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a core
 * dependency that should be automatically registered into the DI container.
 * <p>
 * Fields annotated with {@code @BindCore} are discovered by
 * PlayTimeCoreBinder and exposed to
 * the Panda DI {@link org.panda_lang.utilities.inject.Resources} as singleton instances.
 * <p>
 * Only non-static fields are eligible. A {@code null} value at binding time
 * results in a bootstrap failure.
 */
@Injectable
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BindCore {
}
