package com.github.imdmk.spenttime.infrastructure.database.repository.ormlite;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic mapper for converting between persistence entities and domain models.
 *
 * @param <E> the entity type (ORM/DB representation)
 * @param <D> the domain model type (business representation)
 */
public interface EntityMapper<E, D> {

    /** Converts a domain model object to its entity representation. */
    @NotNull E toEntity(@NotNull D domain);

    /** Converts an entity object to its domain model representation. */
    @NotNull D toDomain(@NotNull E entity);

    /** Converts a list of entities to domain models. */
    default @NotNull List<D> toDomainList(@NotNull List<E> entities) {
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    /** Converts a list of domain models to entities. */
    default @NotNull List<E> toEntityList(@NotNull List<D> domains) {
        return domains.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
