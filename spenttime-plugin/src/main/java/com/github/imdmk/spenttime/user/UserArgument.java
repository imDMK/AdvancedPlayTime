package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.shared.message.MessageConfig;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.inject.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Argument resolver for {@link User} objects.
 * <p>
 * Performs a cache-only lookup on the primary server thread to avoid blocking,
 * and a full asynchronous lookup (cache → database) off the main thread.
 */
final class UserArgument extends ArgumentResolver<CommandSender, User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserArgument.class);
    private static final long LOOKUP_TIMEOUT_MS = 2000;

    @Inject private Server server;
    @Inject private UserService userService;
    @Inject private MessageConfig messageConfig;

    @Override
    protected ParseResult<User> parse(Invocation<CommandSender> invocation,
                                      Argument<User> context,
                                      String argument) {

        // Main thread -> cache-only (never block the tick)
        if (this.server.isPrimaryThread()) {
            LOGGER.debug("UserArgument lookup for '{}' on main thread – using cache only.", argument);
            return this.userService.findCachedByName(argument)
                    .map(ParseResult::success)
                    .orElse(ParseResult.failure(this.messageConfig.playerNotFound));
        }

        // Off-thread -> full lookup (cache → DB)
        return this.userService.findByName(argument)
                .orTimeout(LOOKUP_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .join()
                .map(ParseResult::success)
                .orElse(ParseResult.failure(this.messageConfig.playerNotFound));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation,
                                    Argument<User> argument,
                                    SuggestionContext context) {
        return this.userService.getCachedUsers()
                .stream()
                .map(User::getName)
                .collect(SuggestionResult.collector());
    }
}

