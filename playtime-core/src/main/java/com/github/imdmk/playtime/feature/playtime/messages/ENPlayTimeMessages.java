package com.github.imdmk.playtime.feature.playtime.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public final class ENPlayTimeMessages extends OkaeriConfig implements PlayTimeMessages {

    @Comment({
            "#",
            "# Sent to a player when they check their own playtime.",
            "#",
            "# Placeholders:",
            "#   {PLAYER_PLAYTIME} - formatted playtime of the player (e.g. 5h 32m).",
            "#"
    })
    public Notice playerPlaytimeSelf = Notice.chat(
            "<dark_gray>• <gray>You have spent <red>{PLAYER_PLAYTIME} <gray>on this server.<dark_gray>"
    );

    @Comment({
            "#",
            "# Sent to a command executor when they check another player's playtime.",
            "#",
            "# Placeholders:",
            "#   {PLAYER_NAME}      - target player's nickname.",
            "#   {PLAYER_PLAYTIME}  - formatted playtime of the target player.",
            "#"
    })
    public Notice playerPlaytimeTarget = Notice.chat(
            "<dark_gray>• <gray>Player <red>{PLAYER_NAME} <gray>has spent <red>{PLAYER_PLAYTIME} <gray>on this server.<dark_gray>"
    );

    @Comment({
            "#",
            "# Sent to a command executor after manually setting a player's playtime.",
            "#",
            "# Placeholders:",
            "#   {PLAYER_NAME}      - target player's nickname.",
            "#   {PLAYER_PLAYTIME}  - new formatted playtime value.",
            "#"
    })
    public Notice playerPlaytimeUpdated = Notice.chat(
            "<dark_gray>• <gray>Updated playtime for player <red>{PLAYER_NAME} <gray>to <red>{PLAYER_PLAYTIME}<dark_gray>."
    );

    @Comment({
            "#",
            "# Sent to a command executor after resetting a single player's playtime to zero.",
            "#",
            "# Placeholders:",
            "#   {PLAYER_NAME} - target player's nickname.",
            "#"
    })
    public Notice playerPlaytimeReset = Notice.chat(
            "<dark_gray>• <gray>Playtime for player <red>{PLAYER_NAME} <gray>has been reset to <red>ZERO<gray>.<dark_gray>"
    );

    @Comment({
            "#",
            "# Sent when a global reset of all players' playtime is triggered.",
            "#"
    })
    public Notice playerPlaytimeResetAllStarted = Notice.chat(
            "<dark_gray>• <green>Global playtime reset started... <gray>Please wait.<dark_gray>"
    );

    @Comment({
            "#",
            "# Sent to the executor if the global playtime reset fails.",
            "#"
    })
    public Notice playerPlaytimeResetAllFailed = Notice.chat(
            "<dark_gray>• <red>An error occurred while resetting playtime for all players. "
                    + "<gray>Check console for details.<dark_gray>"
    );

    @Comment({
            "#",
            "# Sent when the global playtime reset finishes successfully.",
            "#"
    })
    public Notice playerPlaytimeResetAllFinished = Notice.chat(
            "<dark_gray>• <green>Successfully reset playtime for all stored players.<dark_gray>"
    );

    @Comment({
            "#",
            "# Sent after invalidating the Top users playtime cache.",
            "#"
    })
    public Notice topUsersCacheInvalidated = Notice.chat(
            "<dark_gray>• <green>Successfully invalidated the PlayTime Top cache.<dark_gray>"
    );

    @Override
    public Notice playerPlaytimeSelf() {
        return playerPlaytimeSelf;
    }

    @Override
    public Notice playerPlaytimeTarget() {
        return playerPlaytimeTarget;
    }

    @Override
    public Notice playerPlaytimeUpdated() {
        return playerPlaytimeUpdated;
    }

    @Override
    public Notice playerPlaytimeReset() {
        return playerPlaytimeReset;
    }

    @Override
    public Notice playerPlaytimeResetAllStarted() {
        return playerPlaytimeResetAllStarted;
    }

    @Override
    public Notice playerPlaytimeResetAllFailed() {
        return playerPlaytimeResetAllFailed;
    }

    @Override
    public Notice playerPlaytimeResetAllFinished() {
        return playerPlaytimeResetAllFinished;
    }

    @Override
    public Notice topUsersCacheInvalidated() {
        return topUsersCacheInvalidated;
    }
}
