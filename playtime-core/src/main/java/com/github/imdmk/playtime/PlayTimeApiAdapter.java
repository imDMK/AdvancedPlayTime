package com.github.imdmk.playtime;

import com.github.imdmk.playtime.shared.Validator;
import com.github.imdmk.playtime.user.UserService;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

record PlayTimeApiAdapter(@NotNull UserService userService,
                          @NotNull PlaytimeService playtimeService) implements PlayTimeApi {

    @Inject
    PlayTimeApiAdapter(
            @NotNull UserService userService,
            @NotNull PlaytimeService playtimeService) {
        this.userService = Validator.notNull(userService, "userService cannot be null");
        this.playtimeService = Validator.notNull(playtimeService, "playtimeService cannot be null");
    }

    @Override
    public @NotNull UserService userService() {
        return userService;
    }

    @Override
    public @NotNull PlaytimeService playtimeService() {
        return playtimeService;
    }
}
