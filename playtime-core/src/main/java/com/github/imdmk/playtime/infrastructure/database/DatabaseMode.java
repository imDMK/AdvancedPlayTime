package com.github.imdmk.playtime.infrastructure.database;

/**
 * Enumerates all database engines supported by the plugin.
 * <p>
 * Each value represents a distinct JDBC provider and is used to:
 * <ul>
 *     <li>select and load the correct JDBC driver dynamically,</li>
 *     <li>apply engine-specific JDBC URL configuration,</li>
 *     <li>initialize the matching {@code DriverConfigurer} implementation.</li>
 * </ul>
 * <p>
 * Below each engine is annotated with practical recommendations
 * for typical Minecraft server environments.
 */
public enum DatabaseMode {

    /**
     * MySQL — recommended for most production servers.
     * <p>
     * Stable, well-supported, widely hosted, good performance under sustained load.
     * Best choice for: medium–large servers, networks, Bungee/Velocity setups.
     */
    MYSQL,

    /**
     * MariaDB — drop-in MySQL replacement.
     * <p>
     * Often faster for reads, lighter resource usage, very stable on Linux hosts.
     * Best choice for: self-hosted servers (VPS/dedicated), users preferring open-source MySQL alternatives.
     */
    MARIADB,

    /**
     * SQLite — file-based embedded database.
     * <p>
     * Zero configuration, no external server needed, safe for smaller datasets.
     * Best choice for: small servers, testing environments, local development.
     * Avoid it for large playtime tables or heavy concurrent write load.
     */
    SQLITE,

    /**
     * PostgreSQL — robust, enterprise-grade server engine.
     * <p>
     * Very strong consistency guarantees, excellent indexing, powerful features.
     * Best choice for: large datasets, advanced analytics, servers on modern hosting (e.g., managed PSQL).
     */
    POSTGRESQL,

    /**
     * H2 — lightweight embedded or file-based engine.
     * <p>
     * Faster than SQLite in many scenarios, supports MySQL compatibility mode.
     * Best choice for: plugin developers, embedded deployments, users wanting higher performance without external DB.
     * Not recommended for: huge datasets or multi-server networks.
     */
    H2,

    /**
     * SQL Server (MSSQL) — enterprise Microsoft database engine.
     * <p>
     * Works well on Windows hosts, strong enterprise tooling.
     * Best choice for: Windows-based servers, corporate networks using MSSQL by default.
     * Rarely needed for typical Minecraft environments.
     */
    SQL
}
