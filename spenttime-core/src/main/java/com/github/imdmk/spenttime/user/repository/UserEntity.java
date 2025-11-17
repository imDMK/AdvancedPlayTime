package com.github.imdmk.spenttime.user.repository;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Persistent representation of a user stored in the database.
 * <p>
 * This entity is managed by ORMLite and maps directly to the "spent_time_users" table.
 * It mirrors the in-memory {@code User} object used in runtime logic.
 */
@DatabaseTable(tableName = UserEntityMeta.TABLE)
public final class UserEntity {

    /** Primary key — unique player UUID. */
    @DatabaseField(id = true, canBeNull = false, columnName = UserEntityMeta.Col.UUID)
    private UUID uuid;

    /** Last known player name. */
    @DatabaseField(canBeNull = false, index = true, columnName = UserEntityMeta.Col.NAME)
    private String name;

    /** Total spent time in milliseconds. */
    @DatabaseField(canBeNull = false, columnName = UserEntityMeta.Col.SPENT_MILLIS)
    private long spentMillis;

    /** No-arg constructor required by ORMLite. */
    public UserEntity() {}

    public UserEntity(@NotNull UUID uuid, @NotNull String name, long spentMillis) {
        this.uuid = uuid;
        this.name = name;
        this.spentMillis = spentMillis;
    }

    public @NotNull UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public long getSpentMillis() {
        return spentMillis;
    }

    public void setSpentMillis(long spentMillis) {
        this.spentMillis = spentMillis;
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
                ", spentMillis=" + this.spentMillis +
                '}';
    }
}
