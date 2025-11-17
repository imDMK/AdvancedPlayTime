package com.github.imdmk.spenttime.feature.migration.runner;

import com.github.imdmk.spenttime.feature.migration.listener.MigrationListener;

import java.util.List;

public interface MigrationRunner<T> {

    T execute();

    List<MigrationListener> listeners();

}