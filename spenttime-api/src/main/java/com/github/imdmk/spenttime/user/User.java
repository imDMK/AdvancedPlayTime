package com.github.imdmk.spenttime.user;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a player and their total time spent on the server in a thread-safe manner.
 *
 * <p><b>Public API:</b> Exposes time only through {@link UserTime}. No raw milliseconds or {@link java.time.Duration}.</p>
 *
 * <p><b>Thread safety:</b> The time field uses {@link AtomicLong}, and the name field is {@code volatile}.
 * Each field guarantees atomic visibility individually; compound operations must be externally synchronized.</p>
 */
public final class User {

    /** Unique player identifier (immutable). */
    private final UUID uuid;

    /** Last known player name; visibility ensured via {@code volatile}. */
    private volatile String name;

    /** Total spent time in milliseconds (never negative), stored atomically. */
    private final AtomicLong spentMillis = new AtomicLong(0L);

    /**
     * Constructs a new user with zero spent time.
     *
     * @param uuid non-null player UUID
     * @param name non-null, non-blank player name
     * @throws NullPointerException if {@code uuid} or {@code name} is null
     * @throws IllegalArgumentException if {@code name} is blank
     */
    public User(@NotNull UUID uuid, @NotNull String name) {
        this(uuid, name, UserTime.ZERO);
    }

    /**
     * Constructs a new user with the specified initial spent time.
     *
     * @param uuid     non-null player UUID
     * @param name     non-null, non-blank player name
     * @param userTime non-null, non-negative initial time
     * @throws NullPointerException if any argument is null
     * @throws IllegalArgumentException if {@code name} is blank or {@code userTime} is negative
     */
    public User(@NotNull UUID uuid, @NotNull String name, @NotNull UserTime userTime) {
        this.uuid = Objects.requireNonNull(uuid, "uuid cannot be null");
        this.setName(name);
        this.setSpentTime(userTime);
    }

    /** @return the player's unique UUID (never null) */
    public @NotNull UUID getUuid() {
        return this.uuid;
    }

    /** @return the current player name (never null or blank) */
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Updates the player's name.
     *
     * @param name non-null, non-blank name
     * @throws NullPointerException if {@code name} is null
     * @throws IllegalArgumentException if {@code name} is blank
     */
    public void setName(@NotNull String name) {
        Objects.requireNonNull(name, "name cannot be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        this.name = name;
    }

    /** @return total spent time as a {@link UserTime} instance (non-negative) */
    public @NotNull UserTime getSpentTime() {
        return UserTime.ofMillis(this.spentMillis.get());
    }

    /**
     * Replaces the total spent time.
     *
     * @param userTime non-null, non-negative {@link UserTime}
     * @throws NullPointerException if {@code userTime} is null
     * @throws IllegalArgumentException if {@code userTime} is negative
     */
    public void setSpentTime(@NotNull UserTime userTime) {
        Objects.requireNonNull(userTime, "userTime cannot be null");
        this.spentMillis.set(userTime.millis());
    }

    /**
     * Two users are considered equal if they share the same UUID.
     *
     * @param o the object to compare
     * @return true if both users share the same UUID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return this.uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }

    @Override
    public @NotNull String toString() {
        return "User{" +
                "uuid=" + this.uuid +
                ", name='" + this.name + '\'' +
                ", spentMillis=" + this.spentMillis +
                '}';
    }
}
