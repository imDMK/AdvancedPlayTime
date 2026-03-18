package com.github.imdmk.playtime.core.feature.playtime.placeholder;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.injector.annotations.placeholder.Placeholder;
import com.github.imdmk.playtime.core.platform.placeholder.PluginPlaceholder;
import com.github.imdmk.playtime.core.platform.playtime.PlayTimeAdapter;
import com.github.imdmk.playtime.core.time.DurationService;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;

@Placeholder
final class PlayTimePlaceholder implements PluginPlaceholder {

    private static final String IDENTIFIER = "playtime";

    private final DurationService durationService;
    private final PlayTimeAdapter playTimeAdapter;

    @Inject
    PlayTimePlaceholder(
            DurationService durationService,
            PlayTimeAdapter playTimeAdapter
    ) {
        this.durationService = durationService;
        this.playTimeAdapter = playTimeAdapter;
    }

    @Override
    public String identifier() {
        return IDENTIFIER;
    }

    @Override
    public String request(Player player, String params) {
        PlayTime playTime = playTimeAdapter.read(player);
        return durationService.format(playTime.toDuration());
    }
}
