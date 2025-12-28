package com.github.imdmk.playtime.database.repository.ormlite;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Defines a bidirectional mapper between persistence-layer entities (ORM objects)
 * and domain-layer models (business objects).
 * <p>
 * This abstraction keeps the repository layer decoupled from the domain layer,
 * allowing storage representations to evolve independently of business logic.
 *
 * @param <E> entity type used for persistence (ORM/DB representation)
 * @param <D> domain model type used in business logic
 */
public interface EntityMapper<E, D> {

    /**
     * Maps a domain model instance into its persistence-layer entity representation.
     *
     * @param domain the domain object to convert (never null)
     * @return the corresponding persistence entity
     */
    @NotNull E toEntity(@NotNull D domain);

    /**
     * Maps a persistence-layer entity into its domain model representation.
     *
     * @param entity the persistence entity to convert (never null)
     * @return the corresponding domain model
     */
    @NotNull D toDomain(@NotNull E entity);

    /**
     * Converts a list of persistence entities to domain model objects.
     * <p>
     * This is a convenience method for bulk transformations.
     *
     * @param entities list of entities to convert (never null)
     * @return list of mapped domain models
     */
    default @NotNull List<D> toDomainList(@NotNull List<E> entities) {
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    /**
     * Converts a list of domain model objects to persistence entities.
     * <p>
     * This is a convenience method for bulk transformations.
     *
     * @param domains list of domain objects to convert (never null)
     * @return list of mapped persistence entities
     */
    default @NotNull List<E> toEntityList(@NotNull List<D> domains) {
        return domains.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
