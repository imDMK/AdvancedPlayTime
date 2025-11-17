package com.github.imdmk.spenttime.feature.reload.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class ENReloadMessages extends OkaeriConfig implements ReloadMessages {

    @Comment({
            "# Sent when configuration files for the SpentTime plugin have been reloaded successfully.",
            "# Used after a reload command is executed."
    })
    public Notice configReloadedSuccess = Notice.chat(
            "<dark_gray>• <green>SpentTime configuration has been reloaded successfully.<dark_gray>"
    );

    @Comment({
            "# Sent when an error occurs while reloading the SpentTime configuration files.",
            "# Advises the executor to shut down the plugin and verify the configuration.",
            "# ",
            "# Note: No placeholders."
    })
    public Notice configReloadFailed = Notice.chat(
            "<dark_gray>• <red>Failed to reload SpentTime configuration files. "
                    + "<red>Disable the plugin and verify your config files."
    );

    @Override
    public Notice configReloadedSuccess() {
        return configReloadedSuccess;
    }

    @Override
    public Notice configReloadFailed() {
        return configReloadFailed;
    }
}
