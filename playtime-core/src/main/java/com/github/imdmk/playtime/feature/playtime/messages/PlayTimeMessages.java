package com.github.imdmk.playtime.feature.playtime.messages;

import com.eternalcode.multification.notice.Notice;

public interface PlayTimeMessages {

    Notice playerPlayTimeSelf();
    Notice playerPlayTimeTarget();
    Notice playerPlayTimeUpdated();

}
