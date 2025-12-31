package com.github.imdmk.playtime;

import com.github.imdmk.playtime.user.UserTime;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayTimeService {

    UserTime getTime(@NotNull UUID uuid);

    void setTime(@NotNull UUID uuid, @NotNull UserTime time);

    void resetTime(@NotNull UUID uuid);
}
