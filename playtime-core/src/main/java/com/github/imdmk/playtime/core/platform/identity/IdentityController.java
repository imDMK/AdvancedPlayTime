package com.github.imdmk.playtime.core.platform.identity;

import com.github.imdmk.playtime.core.injector.annotations.Controller;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.panda_lang.utilities.inject.annotations.Inject;

@Controller
final class IdentityController implements Listener {

    private final IdentityCache cache;

    @Inject
    IdentityController(IdentityCache cache) {
        this.cache = cache;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        cache.update(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cache.remove(event.getPlayer());
    }
}

