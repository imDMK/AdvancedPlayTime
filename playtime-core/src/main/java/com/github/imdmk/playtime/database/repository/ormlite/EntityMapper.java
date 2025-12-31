package com.github.imdmk.playtime.database.repository.ormlite;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public interface EntityMapper<E, D> {

    E toEntity(@NotNull D domain);

    D toDomain(@NotNull E entity);

    default List<D> toDomainList(@NotNull List<E> entities) {
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    default List<E> toEntityList(@NotNull List<D> domains) {
        return domains.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
