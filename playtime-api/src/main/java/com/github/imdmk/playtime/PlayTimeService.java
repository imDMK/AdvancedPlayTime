package com.github.imdmk.playtime;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayTimeService {

    PlayTime getTime(@NotNull UUID uuid);

    void setTime(@NotNull UUID uuid, @NotNull PlayTime time);

    void resetTime(@NotNull UUID uuid);
}
