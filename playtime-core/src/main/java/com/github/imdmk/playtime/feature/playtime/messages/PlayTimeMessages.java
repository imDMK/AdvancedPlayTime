package com.github.imdmk.playtime.feature.playtime.messages;

import com.eternalcode.multification.notice.Notice;

public interface PlayTimeMessages {

    Notice playerPlaytimeSelf();
    Notice playerPlaytimeTarget();

    Notice playerPlaytimeUpdated();
    Notice playerPlaytimeReset();

    Notice playerPlaytimeResetAllStarted();
    Notice playerPlaytimeResetAllFailed();
    Notice playerPlaytimeResetAllFinished();

    Notice topUsersCacheInvalidated();
}
