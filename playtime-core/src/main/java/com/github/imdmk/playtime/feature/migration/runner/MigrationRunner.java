package com.github.imdmk.playtime.feature.migration.runner;

import com.github.imdmk.playtime.feature.migration.listener.MigrationListener;

import java.util.List;

public interface MigrationRunner<T> {

    T execute();

    List<MigrationListener> listeners();

}