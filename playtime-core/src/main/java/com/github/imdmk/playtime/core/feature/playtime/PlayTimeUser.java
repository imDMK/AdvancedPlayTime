package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.api.PlayTime;

import java.util.UUID;

public final class PlayTimeUser {

    private final UUID uuid;

    private String name;
    private PlayTime playTime;

    public PlayTimeUser(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.playTime = PlayTime.ZERO;
    }

    public PlayTimeUser(UUID uuid, String name, PlayTime playTime) {
        this.uuid = uuid;
        this.name = name;
        this.playTime = playTime;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "PlayTimeUser{" +
                "uuid=" + uuid +
                ", name='" + name +
                ", playTime=" + playTime +
                '}';
    }
}
