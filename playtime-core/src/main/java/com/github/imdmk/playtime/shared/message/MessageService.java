package com.github.imdmk.playtime.shared.message;

import com.eternalcode.multification.adventure.AudienceConverter;
import com.eternalcode.multification.bukkit.BukkitMultification;
import com.eternalcode.multification.notice.provider.NoticeProvider;
import com.eternalcode.multification.translation.TranslationProvider;
import com.github.imdmk.playtime.injector.ComponentPriority;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.injector.subscriber.event.PlayTimeShutdownEvent;
import com.github.imdmk.playtime.platform.adventure.AdventureComponents;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Service(priority = ComponentPriority.LOW)
public final class MessageService extends BukkitMultification<MessageConfig> {

    private final MessageConfig messageConfig;
    private final AudienceProvider audienceProvider;

    @Inject
    public MessageService(@NotNull Plugin plugin, @NotNull MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
        this.audienceProvider = BukkitAudiences.create(plugin);
    }

    @NotNull
    @Override
    protected TranslationProvider<MessageConfig> translationProvider() {
        return provider -> messageConfig;
    }

    @NotNull
    @Override
    protected ComponentSerializer<Component, Component, String> serializer() {
        return AdventureComponents.miniMessage();
    }

    @NotNull
    @Override
    protected AudienceConverter<CommandSender> audienceConverter() {
        return sender -> {
            if (sender instanceof Player player) {
                return audienceProvider.player(player.getUniqueId());
            }
            return audienceProvider.console();
        };
    }

    public void send(CommandSender sender, NoticeProvider<MessageConfig> notice) {
        create().viewer(sender).notice(notice).send();
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    public void shutdown() {
        audienceProvider.close();
    }
}
