package com.github.imdmk.playtime.user;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Domain model representing a single player and their total recorded playtime.
 *
 * <p>Playtime is stored internally as a millisecond counter and exposed only through
 * the immutable {@link UserTime} value object. Identity is defined solely by UUID.</p>
 */
public final class User {

    /** Unique immutable player identifier. */
    private final UUID uuid;

    /** Last known player name; volatile for safe publication across threads. */
    private volatile String name;

    /** Total accumulated playtime in milliseconds, stored atomically. */
    private final AtomicLong playtime = new AtomicLong(0L);

    /**
     * Creates a new user with a given UUID, name and initial playtime.
     *
     * @param uuid      non-null player UUID
     * @param name      non-null, non-blank player name
     * @param playtime  non-null initial playtime value
     */
    public User(@NotNull UUID uuid, @NotNull String name, @NotNull UserTime playtime) {
        this.uuid = Objects.requireNonNull(uuid, "uuid cannot be null");
        this.setName(name);
        this.setPlaytime(playtime);
    }

    /**
     * Creates a new user with zero playtime.
     *
     * @param uuid non-null player UUID
     * @param name non-null, non-blank player name
     */
    public User(@NotNull UUID uuid, @NotNull String name) {
        this(uuid, name, UserTime.ZERO);
    }

    /**
     * Returns the player's unique identifier.
     */
    @NotNull
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * Returns the last known player name.
     */
    @NotNull
    public String getName() {
        return this.name;
    }

    /**
     * Updates the player's name.
     *
     * @param name non-null, non-blank new name
     */
    public void setName(@NotNull String name) {
        Objects.requireNonNull(name, "name cannot be null");
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        this.name = name;
    }

    /**
     * Returns the total accumulated playtime as an immutable value object.
     */
    @NotNull
    public UserTime getPlaytime() {
        return UserTime.ofMillis(playtime.get());
    }

    /**
     * Replaces the stored playtime value.
     *
     * @param playtime non-null playtime value
     */
    public void setPlaytime(@NotNull UserTime playtime) {
        Objects.requireNonNull(playtime, "playtime cannot be null");
        this.playtime.set(playtime.millis());
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
     * Hash code based solely on the UUID.
     */
    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    /**
     * Returns a concise diagnostic string representation.
     */
    @Override
    public @NotNull String toString() {
        return "User{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", playtime=" + playtime.get() + "ms" +
                '}';
    }
}
