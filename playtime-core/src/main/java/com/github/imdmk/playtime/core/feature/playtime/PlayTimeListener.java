package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.injector.annotations.PluginListener;
import com.github.imdmk.playtime.core.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.core.injector.subscriber.event.PlayTimeShutdownEvent;
import com.github.imdmk.playtime.core.platform.logger.PluginLogger;
import com.github.imdmk.playtime.core.platform.playtime.PlayTimeAdapter;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@PluginListener
final class PlayTimeListener implements Listener {

    private final Server server;
    private final PluginLogger logger;
    private final PlayTimeAdapter adapter;
    private final PlayTimeUserService userService;

    @Inject
    PlayTimeListener(
            Server server,
            PluginLogger logger,
            PlayTimeAdapter adapter,
            PlayTimeUserService userService
    ) {
        this.server = server;
        this.logger = logger;
        this.adapter = adapter;
        this.userService = userService;
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        loadPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

    @EventHandler
    void onReload(ServerLoadEvent event) {
        server.getOnlinePlayers().forEach(p -> loadPlayer(p.getUniqueId(), p.getName()));
    }

    @EventHandler
    void onQuit(PlayerQuitEvent event) {
        saveUser(event.getPlayer().getUniqueId());
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    void onShutdown(PlayTimeShutdownEvent event) {
        server.getOnlinePlayers().forEach(p -> saveUser(p.getUniqueId()));
    }

    private void loadPlayer(UUID uuid, String name) {
        userService.getOrLoadUser(uuid)
                .thenCompose(user -> {
                    if (user == null) {
                        PlayTime time = adapter.read(uuid);
                        return userService.createUser(uuid, name, time);
                    }

                    adapter.write(uuid, user.getPlayTime());
                    return CompletableFuture.completedFuture(user);
                })
                .exceptionally(e -> {
                    logger.error(e, "Failed to load user with uuid %s", uuid);
                    return null;
                });
    }

    private void saveUser(UUID uuid) {
        userService.getOrLoadUser(uuid)
                .thenCompose(user -> {
                    if (user == null) {
                        return CompletableFuture.completedFuture(null);
                    }

                    PlayTime time = adapter.read(uuid);
                    user.setPlayTime(time);
                    return userService.saveUser(user);
                })
                .exceptionally(e -> {
                    logger.error(e, "Failed to save user with uuid %s", uuid);
                    return null;
                });
    }
}