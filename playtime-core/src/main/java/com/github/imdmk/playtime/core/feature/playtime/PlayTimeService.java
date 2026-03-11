package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.injector.ComponentPriority;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.platform.playtime.PlayTimeAdapter;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;

@Service(priority = ComponentPriority.HIGH)
public final class PlayTimeService {

    private final PlayTimeAdapter adapter;

    @Inject
    PlayTimeService(PlayTimeAdapter adapter) {
        this.adapter = adapter;
    }

    public PlayTime getCurrentPlayTime(PlayTimeUser user) {
        PlayTime playTime = adapter.read(user.getUuid());
        return playTime != null ? playTime : user.getPlayTime();
    }

    public void setPlayTime(PlayTimeUser user, PlayTime playTime) {
        user.setPlayTime(playTime);
        adapter.write(user.getUuid(), playTime);
    }
}
