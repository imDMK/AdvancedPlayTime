package com.github.imdmk.playtime.core.feature.playtime.placeholder;

import com.github.imdmk.playtime.core.feature.playtime.PlayTimeUser;
import com.github.imdmk.playtime.core.feature.playtime.PlayTimeUserService;
import com.github.imdmk.playtime.core.injector.annotations.placeholderapi.Placeholder;
import com.github.imdmk.playtime.core.platform.placeholder.PluginPlaceholder;
import com.github.imdmk.playtime.core.time.DurationService;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;

@Placeholder
final class PlayTimePlaceholder implements PluginPlaceholder {

    private static final String IDENTIFIER = "playtime";

    private final DurationService durationService;
    private final PlayTimeUserService userService;

    @Inject
    PlayTimePlaceholder(
            DurationService durationService,
            PlayTimeUserService userService
    ) {
        this.durationService = durationService;
        this.userService = userService;
    }

    @Override
    public String identifier() {
        return IDENTIFIER;
    }

    @Override
    public String request(Player player, String params) {
        UUID playerId = player.getUniqueId();

        return userService.getUser(playerId)
                .map(PlayTimeUser::getPlayTime)
                .map(time -> durationService.format(time.toDuration()))
                .orElse(null);
    }
}
