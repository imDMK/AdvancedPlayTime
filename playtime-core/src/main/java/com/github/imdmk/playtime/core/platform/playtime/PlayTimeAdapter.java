package com.github.imdmk.playtime.core.platform.playtime;

import com.github.imdmk.playtime.api.PlayTime;

import java.util.UUID;

public interface PlayTimeAdapter {

    PlayTime read(UUID playerId);

    void write(UUID playerId, PlayTime playTime);

}

