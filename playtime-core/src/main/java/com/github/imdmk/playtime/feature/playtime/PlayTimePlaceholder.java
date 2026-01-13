package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.PlayTime;
import com.github.imdmk.playtime.injector.annotations.placeholderapi.Placeholder;
import com.github.imdmk.playtime.platform.placeholder.PluginPlaceholder;
import com.github.imdmk.playtime.shared.time.Durations;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Placeholder
final class PlayTimePlaceholder implements PluginPlaceholder {

    private static final String IDENTIFIER = "playtime";

    private final PlayTimeUserCache cache;

    @Inject
    PlayTimePlaceholder(@NotNull PlayTimeUserCache cache) {
        this.cache = cache;
    }

    @Override
    public String identifier() {
        return IDENTIFIER;
    }

    @Override
    public String request(@NotNull Player player, @NotNull String params) {
        final PlayTime cachedPlayTime = cache.get(player.getUniqueId())
                .map(PlayTimeUser::getPlayTime)
                .orElse(PlayTime.ZERO);

        return Durations.format(cachedPlayTime.toDuration());
    }
}
