package com.github.imdmk.playtime.user;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents an immutable-identity player aggregate containing all tracked
 * playtime-related metadata.
 *
 * <p>This class is the main domain model for player statistics and provides:
 * <ul>
 *     <li>stable identity via {@link UUID},</li>
 *     <li>thread-safe counters using {@link AtomicLong} and {@link AtomicInteger},</li>
 *     <li>mutable fields for name, join tracking, and playtime accumulation.</li>
 * </ul>
 *
 * All numerical fields are stored in atomic structures to allow safe concurrent
 * updates from asynchronous tasks (e.g., an async database writes). The name field
 * is {@code volatile}, ensuring safe publication across threads.
 * <p>
 * Two {@code User} instances are considered equal if and only if their UUIDs match.
 */
public final class User {

    /** Permanently immutable player UUID. */
    private final UUID uuid;

    /** Last known player name. Volatile for safe cross-thread publication. */
    private volatile String name;

    /** Total accumulated playtime in milliseconds. */
    private final AtomicLong playtimeMillis;

    /**
     * Creates a fully initialized {@code User} instance.
     *
     * @param uuid            unique player identifier (never null)
     * @param name            last known player name (never null or blank)
     * @param playtime        initial playtime value (never null)
     *
     * @throws NullPointerException     if UUID, name or playtime is null
     * @throws IllegalArgumentException if joinCount < 0 or timestamps < 0
     */
    public User(@NotNull UUID uuid, @NotNull String name, @NotNull UserTime playtime) {
        Objects.requireNonNull(playtime, "playtime cannot be null");

        this.uuid = Objects.requireNonNull(uuid, "uuid cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.playtimeMillis = new AtomicLong(playtime.millis());
    }

    /**
     * Convenience constructor for a new player with zero playtime.
     *
     * @param uuid unique player identifier
     * @param name last known player name
     */
    public User(@NotNull UUID uuid, @NotNull String name) {
        this(uuid, name, UserTime.ZERO);
    }

    /**
     * Returns the unique identifier of this user.
     *
     * @return player's UUID (never null)
     */
    @NotNull
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * Returns the last known player name.
     *
     * @return name as a non-null String
     */
    @NotNull
    public String getName() {
        return this.name;
    }

    /**
     * Updates the stored player name.
     *
     * @param name the new name (non-null, non-blank)
     * @throws NullPointerException     if name is null
     * @throws IllegalArgumentException if name is blank
     */
    public void setName(@NotNull String name) {
        Objects.requireNonNull(name, "name cannot be null");
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        this.name = name;
    }

    /**
     * Returns the total accumulated playtime as an immutable {@link UserTime} object.
     *
     * @return playtime value (never null)
     */
    @NotNull
    public UserTime getPlaytime() {
        return UserTime.ofMillis(playtimeMillis.get());
    }

    /**
     * Replaces the stored playtime with a new value.
     *
     * @param playtime the new playtime (must not be null)
     * @throws NullPointerException if playtime is null
     */
    public void setPlaytime(@NotNull UserTime playtime) {
        Objects.requireNonNull(playtime, "playtime cannot be null");
        playtimeMillis.set(playtime.millis());
    }

    /**
     * Users are equal if and only if their UUIDs match.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return uuid.equals(other.uuid);
    }

    /**
     * Hash code is based solely on UUID.
     */
    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    /**
     * Returns a concise diagnostic string representation.
     */
    @Override
    public String toString() {
        return "User{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", playtimeMillis=" + playtimeMillis.get() +
                '}';
    }
}
