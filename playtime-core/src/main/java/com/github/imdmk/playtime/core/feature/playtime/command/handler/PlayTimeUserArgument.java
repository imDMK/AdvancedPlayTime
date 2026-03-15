package com.github.imdmk.playtime.core.feature.playtime.command.handler;

import com.github.imdmk.playtime.core.feature.playtime.PlayTimeUser;
import com.github.imdmk.playtime.core.feature.playtime.PlayTimeUserService;
import com.github.imdmk.playtime.core.injector.annotations.lite.LiteArgument;
import com.github.imdmk.playtime.core.message.MessageConfig;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.regex.Pattern;

@LiteArgument(type = PlayTimeUser.class)
final class PlayTimeUserArgument
        extends ArgumentResolver<CommandSender, PlayTimeUser> {

    private static final Pattern VALID_USER_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

    private final MessageConfig messageConfig;
    private final PlayTimeUserService userService;

    @Inject
    PlayTimeUserArgument(
            MessageConfig messageConfig,
            PlayTimeUserService userService
    ) {
        this.messageConfig = messageConfig;
        this.userService = userService;
    }

    @Override
    protected ParseResult<PlayTimeUser> parse(
            Invocation<CommandSender> invocation,
            Argument<PlayTimeUser> context,
            String argument
    ) {
        return ParseResult.completableFuture(
                userService.getOrLoadUser(argument),
                user -> {
                    if (user == null) {
                        return ParseResult.failure(messageConfig.playerNotFound);
                    }

                    return ParseResult.success(user);
                }
        );
    }

    @Override
    protected boolean match(
            Invocation<CommandSender> invocation,
            Argument<PlayTimeUser> context,
            String argument
    ) {
        return VALID_USER_PATTERN.matcher(argument).matches();
    }

    @Override
    public SuggestionResult suggest(
            Invocation<CommandSender> invocation,
            Argument<PlayTimeUser> argument,
            SuggestionContext context
    ) {
        return SuggestionResult.of(userService.cachedNames());
    }
}
