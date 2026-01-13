package com.github.imdmk.playtime.feature.playtime.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public final class ENPlayTimeMessages
        extends OkaeriConfig
        implements PlayTimeMessages {

    @Comment({
            "#",
            "# Sent to a player when they check their own playtime.",
            "#",
            "# Placeholders:",
            "#   {PLAYER_PLAYTIME} - formatted playtime of the player (e.g. 5h 32m).",
            "#"
    })
    Notice playerPlayTimeSelf = Notice.chat(
            "<dark_gray>• <gray>You have spent <red>{PLAYER_PLAYTIME} <gray>on this server.<dark_gray>"
    );

    @Comment({
            "#",
            "# Sent to a command executor when they check another player's playtime.",
            "#",
            "# Placeholders:",
            "#   {PLAYER_PLAYTIME}  - formatted playtime of the target player.",
            "#"
    })
    Notice playerPlayTimeTarget = Notice.chat(
            "<dark_gray>• <gray>Player <red>{PLAYER_NAME} <gray>has spent <red>{PLAYER_PLAYTIME} <gray>on this server.<dark_gray>"
    );

    @Comment({
            "#",
            "# Sent to a command executor after manually setting a player's playtime.",
            "#",
            "# Placeholders:",
            "#   {PLAYER_PLAYTIME}  - new formatted playtime value.",
            "#"
    })
    Notice playerPlayTimeUpdated = Notice.chat(
            "<dark_gray>• <gray>Updated playtime for player to <red>{PLAYER_PLAYTIME}<dark_gray>."
    );

    @Override
    public Notice playerPlayTimeSelf() {
        return playerPlayTimeSelf;
    }

    @Override
    public Notice playerPlayTimeTarget() {
        return playerPlayTimeTarget;
    }

    @Override
    public Notice playerPlayTimeUpdated() {
        return playerPlayTimeUpdated;
    }
}