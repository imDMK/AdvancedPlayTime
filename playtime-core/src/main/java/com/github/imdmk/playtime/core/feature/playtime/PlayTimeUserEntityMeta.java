package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.core.database.repository.ormlite.EntityMeta;

interface PlayTimeUserEntityMeta extends EntityMeta {

    String TABLE = "advanced_playtime_users";

    interface Col {

        String UUID = "uuid";

        String PLAYTIME_MILLIS = "playtimeMillis";

    }
}
