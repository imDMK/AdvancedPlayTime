package com.github.imdmk.playtime;

import com.github.imdmk.playtime.user.UserService;

public interface PlayTimeApi {

    UserService getUserService();

    PlayTimeService getPlayTimeService();
}
