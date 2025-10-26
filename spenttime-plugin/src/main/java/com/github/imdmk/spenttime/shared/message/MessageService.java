package com.github.imdmk.spenttime.shared.message;

import com.eternalcode.multification.adventure.AudienceConverter;
import com.eternalcode.multification.bukkit.BukkitMultification;
import com.eternalcode.multification.notice.provider.NoticeProvider;
import com.eternalcode.multification.translation.TranslationProvider;
import com.github.imdmk.spenttime.Validator;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Message service bridging plugin messaging with Adventure/MiniMessage, backed by
 * {@link BukkitMultification} and {@link AudienceProvider}.
 * <p>
 * This implementation:
 * <ul>
 *   <li>Uses a single {@link MessageConfig} instance for translations (locale argument is ignored).</li>
 *   <li>Serializes components via {@link MiniMessage}.</li>
 *   <li>Converts Bukkit {@link CommandSender} to Adventure audiences with the provided {@link AudienceProvider}.</li>
 * </ul>
 * <p>
 * <strong>Threading:</strong> Sending messages do not require the primary thread
 */
public final class MessageService
        extends BukkitMultification<MessageConfig> {

    private final MessageConfig messageConfig;
    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;

    public MessageService(
            @NotNull MessageConfig messageConfig,
            @NotNull AudienceProvider audienceProvider,
            @NotNull MiniMessage miniMessage) {
        this.messageConfig = Validator.notNull(messageConfig, "messageConfig cannot be null");
        this.audienceProvider = Validator.notNull(audienceProvider, "audienceProvider cannot be null");
        this.miniMessage = Validator.notNull(miniMessage, "miniMessage cannot be null");
    }

    /**
     * Returns a translation provider that always supplies the same {@link MessageConfig}
     * instance, regardless of the requested locale.
     */
    @Override
    protected @NotNull TranslationProvider<MessageConfig> translationProvider() {
        return locale -> this.messageConfig;
    }

    /**
     * Returns the {@link MiniMessage} serializer used to serialize/deserialize Adventure {@link Component}s.
     */
    @Override
    protected @NotNull ComponentSerializer<Component, Component, String> serializer() {
        return this.miniMessage;
    }

    /**
     * Converts Bukkit command senders to Adventure audiences.
     */
    @Override
    protected @NotNull AudienceConverter<CommandSender> audienceConverter() {
        return sender -> {
            if (sender instanceof Player player) {
                return this.audienceProvider.player(player.getUniqueId());
            }

            return this.audienceProvider.console();
        };
    }

    /**
     * Sends a notice to a specific {@link CommandSender}.
     *
     * @param sender non-null Bukkit command sender (player, console, etc.)
     * @param notice non-null notice provider bound to {@link MessageConfig}
     * @throws NullPointerException if {@code sender} or {@code notice} is {@code null}
     */
    public void send(@NotNull CommandSender sender, @NotNull NoticeProvider<MessageConfig> notice) {
        Validator.notNull(sender, "sender cannot be null");
        Validator.notNull(notice, "notice cannot be null");
        this.create().viewer(sender).notice(notice).send();
    }
}
