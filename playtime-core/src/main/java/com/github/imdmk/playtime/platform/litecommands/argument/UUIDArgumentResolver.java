package com.github.imdmk.playtime.platform.litecommands.argument;

import com.github.imdmk.playtime.injector.annotations.lite.LiteArgument;
import com.github.imdmk.playtime.platform.identity.IdentityCache;
import com.github.imdmk.playtime.shared.message.MessageConfig;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;

@LiteArgument(type = UUID.class)
class UUIDArgumentResolver extends ArgumentResolver<CommandSender, UUID> {

    private final IdentityCache cache;
    private final MessageConfig messageConfig;

    @Inject
    UUIDArgumentResolver(@NotNull IdentityCache cache, @NotNull MessageConfig messageConfig) {
        this.cache = cache;
        this.messageConfig = messageConfig;
    }

    @Override
    protected ParseResult<UUID> parse(Invocation<CommandSender> invocation, Argument<UUID> context, String argument) {
        return cache.getUuidByName(argument)
                .map(ParseResult::success)
                .orElse(ParseResult.failure(messageConfig.playerNotFound));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<UUID> argument, SuggestionContext context) {
        return SuggestionResult.of(cache.getPlayerNames());
    }
}