package com.github.imdmk.playtime.feature.playtime.placeholder;

import com.github.imdmk.playtime.PlaytimeService;
import com.github.imdmk.playtime.platform.placeholder.PluginPlaceholder;
import com.github.imdmk.playtime.shared.Validator;
import com.github.imdmk.playtime.shared.time.Durations;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;

public final class PlayTimePlaceholder implements PluginPlaceholder {

    private final PlaytimeService playtimeService;

    @Inject
    public PlayTimePlaceholder(@NotNull PlaytimeService playtimeService) {
        this.playtimeService = Validator.notNull(playtimeService, "playtimeService cannot be null");
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
