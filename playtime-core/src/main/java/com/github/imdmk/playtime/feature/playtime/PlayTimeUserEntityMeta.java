package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.database.repository.ormlite.EntityMeta;

interface PlayTimeUserEntityMeta extends EntityMeta {

    String TABLE = "advanced_playtime_users";

    interface Col {

        String UUID = "uuid";

        String PLAYTIME_MILLIS = "playtimeMillis";

    }
}
