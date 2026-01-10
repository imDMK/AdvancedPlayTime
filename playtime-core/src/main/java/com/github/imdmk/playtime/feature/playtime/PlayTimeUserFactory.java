package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.PlayTimeService;
import com.github.imdmk.playtime.injector.ComponentPriority;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserFactory;
import com.github.imdmk.playtime.PlayTime;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.Optional;
import java.util.UUID;

@Service(priority = ComponentPriority.LOW)
public final class PlayTimeUserFactory implements UserFactory {

    private static final String UNKNOWN_PLAYER_NAME_FORMAT = "Unknown:%s";

    private final PlayTimeService playtimeService;

    @Inject
    public PlayTimeUserFactory(@NotNull PlayTimeService playtimeService) {
        this.playtimeService = playtimeService;
    }

    @Override
    public @NotNull User createFrom(@NotNull Player player) {
        final UUID uuid = player.getUniqueId();
        final String name = player.getName();
        final PlayTime time = playtimeService.getTime(uuid);

        return new User(uuid, name, time);
    }

    @Override
    public @NotNull User createFrom(@NotNull OfflinePlayer player) {
        final UUID uuid = player.getUniqueId();
        final String name = Optional.ofNullable(player.getName()).orElse(UNKNOWN_PLAYER_NAME_FORMAT.formatted(uuid));
        final PlayTime time = playtimeService.getTime(uuid);

        return new User(uuid, name, time);
    }
}
