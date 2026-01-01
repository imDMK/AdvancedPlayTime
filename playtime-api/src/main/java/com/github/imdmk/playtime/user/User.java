package com.github.imdmk.playtime.user;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class User {

    private final UUID uuid;
    private volatile String name;

    private final AtomicLong playtimeMillis;

    public User(@NotNull UUID uuid, @NotNull String name, @NotNull UserTime playtime) {
        this.uuid = uuid;
        this.name = name;
        this.playtimeMillis = new AtomicLong(playtime.millis());
    }

    public User(@NotNull UUID uuid, @NotNull String name) {
        this(uuid, name, UserTime.ZERO);
    }

    @NotNull
    public UUID getUuid() {
        return this.uuid;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public UserTime getPlaytime() {
        return UserTime.ofMillis(playtimeMillis.get());
    }

    public void setPlaytime(@NotNull UserTime playtime) {
        playtimeMillis.set(playtime.millis());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", playtimeMillis=" + playtimeMillis.get() +
                '}';
    }
}
