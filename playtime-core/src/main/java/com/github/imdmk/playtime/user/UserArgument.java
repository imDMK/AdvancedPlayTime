package com.github.imdmk.playtime.user;

import com.github.imdmk.playtime.injector.annotations.lite.LiteArgument;
import com.github.imdmk.playtime.message.MessageConfig;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@LiteArgument(type = User.class)
final class UserArgument extends ArgumentResolver<CommandSender, User> {

    private static final Duration LOOKUP_TIMEOUT = Duration.ofSeconds(2);

    private final PluginLogger logger;
    private final Server server;
    private final MessageConfig messageConfig;
    private final UserService userService;

    @Inject
    UserArgument(
            @NotNull PluginLogger logger,
            @NotNull Server server,
            @NotNull MessageConfig messageConfig,
            @NotNull UserService userService
    ) {
        this.logger = logger;
        this.server = server;
        this.messageConfig = messageConfig;
        this.userService = userService;
    }

    @Override
    protected ParseResult<User> parse(Invocation<CommandSender> invocation,
                                      Argument<User> context,
                                      String argument) {

        // Main thread -> cache-only (never block the tick)
        if (server.isPrimaryThread()) {
            logger.warn("UserArgument lookup for '%s' on main thread – using cache only.", argument);
            return userService.findCachedByName(argument)
                    .map(ParseResult::success)
                    .orElse(ParseResult.failure(messageConfig.playerNotFound));
        }

        // Off-thread -> full lookup (cache → DB)
        return userService.findByName(argument)
                .orTimeout(LOOKUP_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .join()
                .map(ParseResult::success)
                .orElse(ParseResult.failure(messageConfig.playerNotFound));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation,
                                    Argument<User> argument,
                                    SuggestionContext context) {
        return userService.getCachedUsers()
                .stream()
                .map(User::getName)
                .collect(SuggestionResult.collector());
    }
}

