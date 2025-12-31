package com.github.imdmk.playtime;

import com.github.imdmk.playtime.user.UserService;
import org.panda_lang.utilities.inject.annotations.Inject;

@Inject
record PlayTimeApiAdapter(
        UserService getUserService,
        PlayTimeService getPlayTimeService
) implements PlayTimeApi {}
