package com.github.imdmk.spenttime.feature.playtime.messages;

import com.eternalcode.multification.notice.Notice;

public interface PlaytimeMessages {

    Notice playerPlaytimeSelf();
    Notice playerPlaytimeTarget();

    Notice playerPlaytimeUpdated();
    Notice playerPlaytimeReset();

    Notice playerPlaytimeResetAllStarted();
    Notice playerPlaytimeResetAllFailed();
    Notice playerPlaytimeResetAllFinished();
}
