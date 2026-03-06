package com.github.imdmk.playtime.core.database.repository.ormlite;

import java.util.List;

public interface EntityMapper<E, D> {

    E toEntity(D domain);

    D toDomain(E entity);

    default List<D> toDomainList(List<E> entities) {
        return entities.stream().map(this::toDomain).toList();
    }

    default List<E> toEntityList(List<D> domains) {
        return domains.stream().map(this::toEntity).toList();
    }
}
