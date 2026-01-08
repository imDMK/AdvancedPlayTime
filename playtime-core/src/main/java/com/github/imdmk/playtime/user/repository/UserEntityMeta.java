package com.github.imdmk.playtime.user.repository;

import com.github.imdmk.playtime.database.repository.ormlite.EntityMeta;

interface UserEntityMeta extends EntityMeta {

    String TABLE = "advanced_playtime_users";

    interface Col {

        String UUID = "uuid";

        String NAME = "name";

        String PLAYTIME_MILLIS = "playtimeMillis";

    }
}
