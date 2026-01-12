package com.github.imdmk.playtime.platform.identity;

import com.github.imdmk.playtime.injector.annotations.Controller;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Controller
final class IdentityController implements Listener {

    private final IdentityCache cache;

    @Inject
    IdentityController(@NotNull IdentityCache cache) {
        this.cache = cache;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        cache.update(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cache.remove(event.getPlayer());
    }
}

