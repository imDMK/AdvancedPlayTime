package com.github.imdmk.playtime.platform.gui;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface IdentifiableGui {

    @NotNull String getId();

}