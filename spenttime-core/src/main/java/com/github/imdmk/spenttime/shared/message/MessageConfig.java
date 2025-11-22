package com.github.imdmk.spenttime.shared.message;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.spenttime.feature.playtime.messages.ENPlaytimeMessages;
import com.github.imdmk.spenttime.feature.reload.messages.ENReloadMessages;
import com.github.imdmk.spenttime.shared.config.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

public final class MessageConfig extends ConfigSection {

    @Comment({
            "#",
            "# Sent when a player attempts to execute a command without having all required permissions.",
            "#",
            "# Placeholders:",
            "#  {PERMISSIONS} - comma-separated list of missing permission nodes required to execute the command.",
            "#"
    })
    public Notice commandPermissionMissing = Notice.chat(
            "<dark_gray>• <red>You are missing required permissions <gray>{PERMISSIONS} <red>to execute this command."
    );

    @Comment({
            "#",
            "# Sent when a player uses a command with an invalid or incomplete syntax.",
            "# ",
            "# Placeholders:",
            "#   {USAGE} - correct usage format of the command (e.g. /spenttime <player>).",
            "#"
    })
    public Notice commandUsageInvalid = Notice.chat(
            "<dark_gray>• <red>Invalid command usage! <gray>Correct syntax: <red>{USAGE}<dark_gray>."
    );

    @Comment({
            "#",
            "# Header shown before listing available correct usages for a command.",
            "# Typically used together with 'commandUsageEntry' when there are multiple valid variants.",
            "#"
    })
    public Notice commandUsageHeader = Notice.chat(
            "<dark_gray>• <red>Correct usage variants:"
    );

    @Comment({
            "#",
            "# Single entry in the list of valid command usages.",
            "# Displayed under 'commandUsageHeader' for each available usage.",
            "# ",
            "# Placeholders:",
            "#  {USAGE} - a single valid usage variant of the command.",
            "#"
    })
    public Notice commandUsageEntry = Notice.chat(
            "<dark_gray>  ▸ <gray>{USAGE}"
    );

    @Comment({
            "#",
            "# Sent when a command expects a player by name, but no matching player is found.",
            "# ",
            "# Placeholders:",
            "#   {PLAYER_NAME} - nickname provided by the command sender.",
            "#"
    })
    public Notice playerNotFound = Notice.chat(
            "<dark_gray>• <red>Player <gray>{PLAYER_NAME} <red>was not found.<dark_gray>"
    );

    @Comment({
            "#",
            "# Fallback message for unexpected errors while executing commands or actions.",
            "# Used when the plugin cannot provide a more detailed error description.",
            "#"
    })
    public Notice actionExecutionError = Notice.chat(
            "<dark_gray>• <red>An unexpected error occurred while performing this action. "
                    + "<gray>If the problem persists, contact an administrator.<dark_gray>"
    );

    @Comment({" ", "# Playtime messages", " "})
    public ENPlaytimeMessages playtimeMessages = new ENPlaytimeMessages();

    @Comment({" ", "# Reload messages", " "})
    public ENReloadMessages reloadMessages = new ENReloadMessages();

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> registry.register(
                new MultificationSerdesPack(NoticeResolverDefaults.createRegistry())
        );
    }

    @Override
    public @NotNull String getFileName() {
        return "messageConfig.yml";
    }
}
