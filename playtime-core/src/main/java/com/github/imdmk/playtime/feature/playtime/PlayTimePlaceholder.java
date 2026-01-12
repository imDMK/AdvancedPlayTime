package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.injector.annotations.placeholderapi.Placeholder;
import com.github.imdmk.playtime.platform.placeholder.PluginPlaceholder;
import com.github.imdmk.playtime.shared.time.Durations;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Placeholder
final class PlayTimePlaceholder implements PluginPlaceholder {

    private static final String IDENTIFIER = "playtime";

    private final PlayTimeUserService userService;

    @Inject
    PlayTimePlaceholder(@NotNull PlayTimeUserService userService) {
        this.userService = userService;
    }

    @Override
    public String identifier() {
        return IDENTIFIER;
    }

    @Override
    public String request(@NotNull Player player, @NotNull String params) {
        return Durations.format(
                userService.getPlayTime(player.getUniqueId()).toDuration()
        );
    }
}
