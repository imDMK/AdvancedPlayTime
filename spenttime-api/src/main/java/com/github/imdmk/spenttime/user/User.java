package com.github.imdmk.spenttime.user;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Domain model representing a single player and the total time they have spent on the server.
 *
 * <p><strong>API note:</strong> Time is exposed exclusively via {@link UserTime}. Raw millisecond values
 * or {@link java.time.Duration} are intentionally not part of the public API.</p>
 *
 * @implNote
 * <ul>
 *   <li><strong>Thread-safety:</strong> The time counter uses an {@link AtomicLong}. The {@code name} field
 *   is {@code volatile}. Each field provides atomic visibility on its own; compound/combined operations
 *   still require external synchronization.</li>
 *   <li><strong>Identity:</strong> Equality and hash code are based solely on {@link #uuid}.</li>
 *   <li><strong>Invariants:</strong> {@link #name} is non-null and non-blank; time is never negative.</li>
 * </ul>
 *
 * @see UserTime
 */
public final class User {

    /** Immutable unique player identifier. */
    private final UUID uuid;

    /** Last known player name; visibility is guaranteed via {@code volatile}. */
    private volatile String name;

    /** Total time spent in milliseconds (non-negative), stored atomically. */
    private final AtomicLong spentMillis = new AtomicLong(0L);

    /**
     * Creates a new user with zero spent time.
     *
     * @param uuid the player's UUID, must not be null
     * @param name the player's name, must not be null or blank
     * @throws NullPointerException if {@code uuid} or {@code name} is null
     * @throws IllegalArgumentException if {@code name} is blank
     */
    public User(@NotNull UUID uuid, @NotNull String name) {
        this(uuid, name, UserTime.ZERO);
    }

    /**
     * Creates a new user with the provided initial spent time.
     *
     * @param uuid     the player's UUID, must not be null
     * @param name     the player's name, must not be null or blank
     * @param userTime the initial spent time, must not be null or negative
     * @throws NullPointerException if any argument is null
     * @throws IllegalArgumentException if {@code name} is blank or {@code userTime} represents a negative value
     */
    public User(@NotNull UUID uuid, @NotNull String name, @NotNull UserTime userTime) {
        this.uuid = Objects.requireNonNull(uuid, "uuid cannot be null");
        this.setName(name);
        this.setSpentTime(userTime);
    }

    /**
     * Returns the player's unique identifier.
     *
     * @return non-null UUID
     */
    public @NotNull UUID getUuid() {
        return this.uuid;
    }

    /**
     * Returns the last known player name.
     *
     * @return non-null, non-blank name
     */
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Updates the player's name.
     *
     * @param name new name, must not be null or blank
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

    /**
     * Returns the total spent time.
     *
     * @return a non-negative {@link UserTime} instance
     */
    public @NotNull UserTime getSpentTime() {
        return UserTime.ofMillis(this.spentMillis.get());
    }

    /**
     * Replaces the total spent time.
     *
     * @param userTime non-null, non-negative {@link UserTime}
     * @throws NullPointerException if {@code userTime} is null
     * @throws IllegalArgumentException if {@code userTime} represents a negative value
     */
    public void setSpentTime(@NotNull UserTime userTime) {
        Objects.requireNonNull(userTime, "userTime cannot be null");
        this.spentMillis.set(userTime.millis());
    }

    /**
     * Users are equal if and only if their UUIDs are equal.
     *
     * @param o the object to compare
     * @return {@code true} if {@code o} is a {@code User} with the same UUID; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return this.uuid.equals(other.uuid);
    }

    /**
     * Hash code derived solely from the UUID.
     *
     * @return hash code consistent with {@link #equals(Object)}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }

    /**
     * Returns a diagnostic representation intended for logging/debugging.
     *
     * @return string containing uuid, name and the atomic counter reference
     */
    @Override
    public @NotNull String toString() {
        return "User{" +
                "uuid=" + this.uuid +
                ", name='" + this.name + '\'' +
                ", spentMillis=" + this.spentMillis +
                '}';
    }
}
