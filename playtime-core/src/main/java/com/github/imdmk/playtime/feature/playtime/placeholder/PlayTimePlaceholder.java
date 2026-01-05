package com.github.imdmk.playtime.feature.playtime.placeholder;

import com.github.imdmk.playtime.PlayTimeService;
import com.github.imdmk.playtime.injector.annotations.Placeholder;
import com.github.imdmk.playtime.platform.placeholder.PluginPlaceholder;
import com.github.imdmk.playtime.time.Durations;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;

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
    public @NotNull String onRequest(@NotNull Player player, @NotNull String params) {
        return formatUserTime(player.getUniqueId());
    }

    @Override
    public @NotNull String onRequest(@NotNull OfflinePlayer player, @NotNull String params) {
        return formatUserTime(player.getUniqueId());
    }

    private @NotNull String formatUserTime(@NotNull UUID uuid) {
        return Durations.format(playtimeService.getTime(uuid).toDuration());
    }
}
