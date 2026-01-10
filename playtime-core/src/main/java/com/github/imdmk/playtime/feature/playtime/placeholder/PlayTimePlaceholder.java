package com.github.imdmk.playtime.feature.playtime.placeholder;

import com.github.imdmk.playtime.PlayTimeService;
import com.github.imdmk.playtime.injector.annotations.placeholderapi.Placeholder;
import com.github.imdmk.playtime.platform.placeholder.PluginPlaceholder;
import com.github.imdmk.playtime.time.Durations;
import com.github.imdmk.playtime.PlayTime;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Placeholder
public final class PlayTimePlaceholder implements PluginPlaceholder {

    private final PlayTimeService playtimeService;

    @Inject
    public PlayTimePlaceholder(@NotNull PlayTimeService playtimeService) {
        this.playtimeService = playtimeService;
    }

    @Override
    public @NotNull String identifier() {
        return "advancedplaytime";
    }

    @Override
    public @NotNull String request(@NotNull Player player, @NotNull String params) {
        final PlayTime time = playtimeService.getTime(player.getUniqueId());
        return Durations.format(time.toDuration());
    }
}
