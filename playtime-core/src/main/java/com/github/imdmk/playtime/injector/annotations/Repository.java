package com.github.imdmk.playtime.injector.annotations;

import com.github.imdmk.playtime.injector.ComponentPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Repository {

    ComponentPriority priority() default ComponentPriority.LOW;

    int order() default 1;

}
