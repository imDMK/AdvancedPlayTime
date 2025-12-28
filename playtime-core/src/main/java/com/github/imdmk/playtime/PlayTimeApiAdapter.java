package com.github.imdmk.playtime;

import com.github.imdmk.playtime.user.UserService;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Inject
record PlayTimeApiAdapter(
        @NotNull UserService userService,
        @NotNull PlaytimeService playtimeService
) implements PlayTimeApi {

}
