package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.api.PlayTime;

import java.util.Objects;
import java.util.UUID;

public final class PlayTimeUser {

    private final UUID uuid;
    private PlayTime playTime;

    PlayTimeUser(UUID uuid) {
        this.uuid = uuid;
        this.playTime = PlayTime.ZERO;
    }

    PlayTimeUser(UUID uuid, PlayTime playTime) {
        this.uuid = uuid;
        this.playTime = playTime;
    }

    public UUID getUuid() {
        return uuid;
    }

    public PlayTime getPlayTime() {
        return playTime;
    }

    public void setPlayTime(PlayTime playTime) {
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
