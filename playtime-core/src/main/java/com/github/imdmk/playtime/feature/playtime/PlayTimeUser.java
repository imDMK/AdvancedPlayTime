package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.PlayTime;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public final class PlayTimeUser {

    private final UUID uuid;
    private PlayTime playTime;

    PlayTimeUser(@NotNull UUID uuid) {
        this.uuid = uuid;
        this.playTime = PlayTime.ZERO;
    }

    PlayTimeUser(@NotNull UUID uuid, @NotNull PlayTime playTime) {
        this.uuid = uuid;
        this.playTime = playTime;
    }

    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    @NotNull
    public PlayTime getPlayTime() {
        return playTime;
    }

    public void setPlayTime(@NotNull PlayTime playTime) {
        this.playTime = playTime;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PlayTimeUser that)) {
            return false;
        }

        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "PlayTimeUser{" +
                "uuid=" + uuid +
                ", playTime=" + playTime +
                '}';
    }
}
