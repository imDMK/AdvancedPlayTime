package com.github.imdmk.playtime.core.feature.playtime.message;

import com.eternalcode.multification.notice.Notice;

public interface PlayTimeMessages {

    Notice playerPlayTimeSelf();
    Notice playerPlayTimeTarget();
    Notice playerPlayTimeUpdated();

    Notice playTimeTopCacheInvalidated();

}
