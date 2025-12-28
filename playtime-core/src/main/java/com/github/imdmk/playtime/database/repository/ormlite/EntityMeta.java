package com.github.imdmk.playtime.database.repository.ormlite;

/**
 * Marker interface for database entity metadata containers.
 *
 * <p>All metadata interfaces (e.g. {@code UserEntityMeta}) should extend this
 * interface to indicate that they define static constants describing database
 * schema elements such as table and column names.</p>
 *
 * <p>This provides a unified contract for schema metadata used by ORMLite
 * entities, repositories, and migration utilities.</p>
 */
public interface EntityMeta {
}
