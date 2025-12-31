package com.github.imdmk.playtime.injector.annotations;

import java.lang.annotation.Annotation;

public class NoneAnnotation implements Annotation {

    public static final NoneAnnotation INSTANCE = new NoneAnnotation();

    @Override
    public Class<? extends Annotation> annotationType() {
        return NoneAnnotation.class;
    }
}

