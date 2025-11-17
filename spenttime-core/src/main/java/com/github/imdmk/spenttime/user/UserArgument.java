package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.platform.logger.PluginLogger;
import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.shared.message.MessageConfig;
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

import java.util.concurrent.TimeUnit;

/**
 * Argument resolver for {@link User} objects.
 * <p>
 * Performs a cache-only lookup on the primary server thread to avoid blocking,
 * and a full asynchronous lookup (cache → database) off the main thread.
 */
final class UserArgument extends ArgumentResolver<CommandSender, User> {

    private static final long LOOKUP_TIMEOUT_MS = 2000;

    private final PluginLogger logger;
    private final Server server;
    private final MessageConfig messageConfig;
    private final UserService userService;

    @Inject
    UserArgument(
            @NotNull PluginLogger logger,
            @NotNull Server server,
            @NotNull MessageConfig messageConfig,
            @NotNull UserService userService) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.server = Validator.notNull(server, "server cannot be null");
        this.messageConfig = Validator.notNull(messageConfig, "config cannot be null");
        this.userService = Validator.notNull(userService, "userService cannot be null");
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
                .orTimeout(LOOKUP_TIMEOUT_MS, TimeUnit.MILLISECONDS)
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

