package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.injector.annotations.placeholderapi.Placeholder;
import com.github.imdmk.playtime.core.platform.placeholder.PluginPlaceholder;
import com.github.imdmk.playtime.core.shared.time.Durations;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;

@Placeholder
final class PlayTimePlaceholder implements PluginPlaceholder {

    private static final String IDENTIFIER = "playtime";

    private final PlayTimeUserCache cache;

    @Inject
    PlayTimePlaceholder(PlayTimeUserCache cache) {
        this.cache = cache;
    }

    @Override
    public String identifier() {
        return IDENTIFIER;
    }

    @Override
    public String request(Player player, String params) {
        PlayTime cachedPlayTime = cache.get(player.getUniqueId())
                .map(PlayTimeUser::getPlayTime)
                .orElse(PlayTime.ZERO);

        return Durations.format(cachedPlayTime.toDuration());
    }
}
