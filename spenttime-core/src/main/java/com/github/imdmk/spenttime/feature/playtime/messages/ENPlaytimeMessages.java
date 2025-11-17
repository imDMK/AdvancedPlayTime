package com.github.imdmk.spenttime.feature.playtime.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class ENPlaytimeMessages extends OkaeriConfig implements PlaytimeMessages {

    @Comment({
            "#",
            "# Sent to a player when they check their own playtime.",
            "# ",
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
            "# ",
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
            "# ",
            "# Placeholders:",
            "#   {PLAYER_NAME}      - target player's nickname.",
            "#   {PLAYER_PLAYTIME}  - new formatted playtime value set for the player.",
            "#"
    })
    public Notice playerPlaytimeUpdated = Notice.chat(
            "<dark_gray>• <gray>Updated playtime for player <red>{PLAYER_NAME} <gray>to <red>{PLAYER_PLAYTIME}<dark_gray>."
    );

    @Comment({
            "#",
            "# Sent to a command executor after resetting a single player's playtime to zero.",
            "# ",
            "# Placeholders:",
            "#   {PLAYER_NAME} - target player's nickname.",
            "#"
    })
    public Notice playerPlaytimeReset = Notice.chat(
            "<dark_gray>• <gray>Playtime for player <red>{PLAYER_NAME} <gray>has been reset to <red>ZERO<gray>.<dark_gray>"
    );

    @Comment({
            "#",
            "# Sent when the global reset of all players' playtime has started.",
            "# Usually triggered by an administrative command that affects every stored player.",
            "#"
    })
    public Notice playerPlaytimeResetAllStarted = Notice.chat(
            "<dark_gray>• <green>Global playtime reset started... <gray>Please wait while all players are processed.<dark_gray>"
    );

    @Comment({
            "#",
            "# Sent when the global reset of all players' playtime fails due to an internal error.",
            "# Displayed to the command executor.",
            "#"
    })
    public Notice playerPlaytimeResetAllFailed = Notice.chat(
            "<dark_gray>• <red>An error occurred while resetting playtime for all players. "
                    + "<gray>Check the console for details.<dark_gray>"
    );

    @Comment({
            "#",
            "Sent when the global reset of all players' playtime completes successfully.",
            "Displayed to the command executor.",
            "#"
    })
    public Notice playerPlaytimeResetAllFinished = Notice.chat(
            "<dark_gray>• <green>Successfully reset playtime for all stored players.<dark_gray>"
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
}
