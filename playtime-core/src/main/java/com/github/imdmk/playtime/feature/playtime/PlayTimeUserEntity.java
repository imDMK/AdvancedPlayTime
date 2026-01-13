package com.github.imdmk.playtime.feature.playtime;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@DatabaseTable(tableName = PlayTimeUserEntityMeta.TABLE)
final class PlayTimeUserEntity {

    @DatabaseField(id = true, canBeNull = false, columnName = PlayTimeUserEntityMeta.Col.UUID)
    private UUID uuid;

    @DatabaseField(canBeNull = false, columnName = PlayTimeUserEntityMeta.Col.PLAYTIME_MILLIS)
    private long playtimeMillis;

    PlayTimeUserEntity() {}

    PlayTimeUserEntity(
            @NotNull UUID uuid,
            long playtimeMillis
    ) {
        this.uuid = uuid;
        this.playtimeMillis = playtimeMillis;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public long getPlayTimeMillis() {
        return playtimeMillis;
    }

    public void setPlayTimeMillis(long playtimeMillis) {
        this.playtimeMillis = playtimeMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlayTimeUserEntity other)) {
            return false;
        }

        return uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "uuid=" + this.uuid +
                ", spentMillis=" + this.playtimeMillis +
                '}';
    }
}
