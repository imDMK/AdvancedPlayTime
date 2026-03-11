package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.api.event.PlayTimeChangedEvent;
import com.github.imdmk.playtime.core.injector.ComponentPriority;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.platform.event.EventCaller;
import com.github.imdmk.playtime.core.platform.playtime.PlayTimeAdapter;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;

@Service(priority = ComponentPriority.HIGH)
public final class PlayTimeService {

    private final PlayTimeAdapter adapter;
    private final EventCaller eventCaller;

    @Inject
    public PlayTimeService(PlayTimeAdapter adapter, EventCaller eventCaller) {
        this.adapter = adapter;
        this.eventCaller = eventCaller;
    }

    public PlayTime getCurrentPlayTime(PlayTimeUser user) {
        PlayTime playTime = adapter.read(user.getUuid());
        return playTime != null ? playTime : user.getPlayTime();
    }

    public void setPlayTime(PlayTimeUser user, PlayTime newPlayTime) {
        UUID uuid = user.getUuid();
        PlayTime oldPlayTime = user.getPlayTime();

        user.setPlayTime(newPlayTime);
        adapter.write(uuid, newPlayTime);

        eventCaller.callEvent(new PlayTimeChangedEvent(uuid, newPlayTime, oldPlayTime));
    }
}
