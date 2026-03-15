package com.github.imdmk.playtime.core.feature.playtime.command.handler;

import com.github.imdmk.playtime.core.feature.playtime.PlayTimeUser;
import com.github.imdmk.playtime.core.feature.playtime.PlayTimeUserService;
import com.github.imdmk.playtime.core.injector.annotations.lite.LiteContext;
import com.github.imdmk.playtime.core.message.MessageConfig;
import dev.rollczi.litecommands.context.ContextProvider;
import dev.rollczi.litecommands.context.ContextResult;
import dev.rollczi.litecommands.invocation.Invocation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;

@LiteContext(type = PlayTimeUser.class)
final class PlayTimeUserContext
        implements ContextProvider<CommandSender, PlayTimeUser> {

    private static final String PLAYER_ONLY_ERROR = "Only player can use this command!";

    private final MessageConfig messageConfig;
    private final PlayTimeUserService userService;

    @Inject
    PlayTimeUserContext(
            MessageConfig messageConfig,
            PlayTimeUserService userService
    ) {
        this.messageConfig = messageConfig;
        this.userService = userService;
    }

    @Override
    public ContextResult<PlayTimeUser> provide(Invocation<CommandSender> invocation) {
        if (!(invocation.sender() instanceof Player player)) {
            return ContextResult.error(PLAYER_ONLY_ERROR);
        }

        return ContextResult.completableFuture(
                userService.getOrLoadUser(player.getUniqueId()),
                user -> {
                    if (user == null) {
                        return ContextResult.error(messageConfig.actionExecutionError);
                    }

                    return ContextResult.ok(() -> user);
                });
    }
}
