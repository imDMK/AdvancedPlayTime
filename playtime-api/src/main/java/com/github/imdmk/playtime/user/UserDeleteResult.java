package com.github.imdmk.playtime.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record UserDeleteResult(@Nullable User user, @NotNull UserDeleteStatus status) {

    public boolean isSuccess() {
        return this.user != null && this.status == UserDeleteStatus.DELETED;
    }
}
