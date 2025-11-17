package com.github.imdmk.spenttime.feature.playtime;

import com.github.imdmk.spenttime.PlaytimeService;
import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserFactory;
import com.github.imdmk.spenttime.user.UserTime;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.Optional;
import java.util.UUID;

/**
 * Concrete implementation of {@link UserFactory} that constructs {@link User} instances
 * using data retrieved from the {@link PlaytimeService}.
 *
 * <p>This factory supports both online and offline players, resolving their unique identifiers,
 * last known names, and total recorded playtime from the underlying service.</p>
 *
 * <p><strong>Dependency:</strong> {@link PlaytimeService} is injected at runtime and must be available
 * before this factory is used.</p>
 *
 * @see User
 * @see PlaytimeService
 * @see UserFactory
 */
public class PlaytimeUserFactory implements UserFactory {

    private static final String UNKNOWN_PLAYER_NAME_FORMAT = "Unknown:%s";

    private final PlaytimeService playtimeService;

    @Inject
    public PlaytimeUserFactory(@NotNull PlaytimeService playtimeService) {
        this.playtimeService = Validator.notNull(playtimeService, "playtimeService cannot be null");
    }

    /**
     * Creates a {@link User} instance from an online {@link Player}.
     *
     * <p>The user's UUID and current name are taken directly from the live {@link Player} object,
     * and their total playtime is resolved via the {@link PlaytimeService}.</p>
     *
     * @param player non-null online player instance
     * @return new {@link User} representing the given player and their current playtime
     * @throws NullPointerException if {@code player} is null
     */
    @Override
    public @NotNull User createFrom(@NotNull Player player) {
        Validator.notNull(player, "player cannot be null");

        final UUID uuid = player.getUniqueId();
        final String name = player.getName();
        final UserTime time = playtimeService.getTime(uuid);

        return new User(uuid, name, time);
    }

    /**
     * Creates a {@link User} instance from an {@link OfflinePlayer}.
     *
     * <p>If the player's name cannot be resolved (e.g. first join or data missing),
     * a default placeholder name {@code "Unknown"} is used instead.
     * The total playtime is fetched from {@link PlaytimeService} based on the player's UUID.</p>
     *
     * @param player non-null offline player instance
     * @return new {@link User} representing the offline player and their playtime data
     * @throws NullPointerException if {@code player} is null
     */
    @Override
    public @NotNull User createFrom(@NotNull OfflinePlayer player) {
        Validator.notNull(player, "player cannot be null");

        final UUID uuid = player.getUniqueId();
        final String name = Optional.ofNullable(player.getName()).orElse(UNKNOWN_PLAYER_NAME_FORMAT.formatted(uuid));
        final UserTime time = playtimeService.getTime(uuid);

        return new User(uuid, name, time);
    }
}
