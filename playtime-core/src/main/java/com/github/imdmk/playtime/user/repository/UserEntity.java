package com.github.imdmk.playtime.user.repository;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

@DatabaseTable(tableName = UserEntityMeta.TABLE)
public final class UserEntity {

    @DatabaseField(id = true, canBeNull = false, columnName = UserEntityMeta.Col.UUID)
    private UUID uuid;

    @DatabaseField(index = true, canBeNull = false, columnName = UserEntityMeta.Col.NAME)
    private String name;

    @DatabaseField(canBeNull = false, columnName = UserEntityMeta.Col.PLAYTIME_MILLIS)
    private long playtimeMillis;

    public UserEntity() {}

    public UserEntity(
            @NotNull UUID uuid,
            @NotNull String name,
            long playtimeMillis
    ) {
        this.uuid = uuid;
        this.name = name;
        this.playtimeMillis = playtimeMillis;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public long getPlaytimeMillis() {
        return playtimeMillis;
    }

    public void setPlaytimeMillis(long playtimeMillis) {
        this.playtimeMillis = playtimeMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity other)) return false;
        return this.uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "uuid=" + this.uuid +
                ", name='" + this.name + '\'' +
                ", spentMillis=" + this.playtimeMillis +
                '}';
    }
}
