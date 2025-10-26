package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.user.UserService;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

record SpentTimeApiAdapter(@NotNull UserService userService,
                           @NotNull PlaytimeService playtimeService) implements SpentTimeApi {

    @Inject
    public SpentTimeApiAdapter(
            @NotNull UserService userService,
            @NotNull PlaytimeService playtimeService) {
        this.userService = Validator.notNull(userService, "userService cannot be null");
        this.playtimeService = Validator.notNull(playtimeService, "playtimeService cannot be null");
    }

    @Override
    public @NotNull UserService userService() {
        return this.userService;
    }

    @Override
    public @NotNull PlaytimeService playtimeService() {
        return this.playtimeService;
    }
}
