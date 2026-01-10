package com.github.imdmk.playtime.user;

import com.github.imdmk.playtime.PlayTime;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public final class User {

    private final UUID uuid;
    private String name;

    private PlayTime playtime;

    public User(
            @NotNull UUID uuid,
            @NotNull String name,
            @NotNull PlayTime playtime
    ) {
        this.uuid = uuid;
        this.name = name;
        this.playtime = playtime;
    }

    public User(
            @NotNull UUID uuid,
            @NotNull String name
    ) {
        this.uuid = uuid;
        this.name = name;
        this.playtime = PlayTime.ZERO;
    }

    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public PlayTime getPlaytime() {
        return playtime;
    }

    public void setPlaytime(@NotNull PlayTime playtime) {
        this.playtime = playtime;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(uuid, user.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", time=" + playtime.millis() +
                '}';
    }
}
