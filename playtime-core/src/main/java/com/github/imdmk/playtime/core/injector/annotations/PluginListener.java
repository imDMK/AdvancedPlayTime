package com.github.imdmk.playtime.core.injector.annotations;

import com.github.imdmk.playtime.core.injector.ComponentPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PluginListener {

    ComponentPriority priority() default ComponentPriority.HIGHEST;

}

