package com.github.imdmk.playtime.shared.message;

import com.eternalcode.multification.adventure.AudienceConverter;
import com.eternalcode.multification.bukkit.BukkitMultification;
import com.eternalcode.multification.notice.provider.NoticeProvider;
import com.eternalcode.multification.translation.TranslationProvider;
import com.github.imdmk.playtime.shared.Validator;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Central message service for the PlayTime plugin, bridging plugin messages with
 * the Adventure and MiniMessage APIs through {@link BukkitMultification}.
 *
 * <p>This implementation provides a high-level abstraction for sending messages,
 * notices, and components to Bukkit {@link CommandSender}s, automatically converting
 * them into Adventure audiences via {@link AudienceProvider}.</p>
 *
 * <p><strong>Features:</strong></p>
 * <ul>
 *   <li>Uses {@link MessageConfig} as the single translation source (locale-agnostic).</li>
 *   <li>Serializes and deserializes Adventure {@link Component}s using {@link MiniMessage}.</li>
 *   <li>Converts Bukkit {@link CommandSender}s into Adventure audiences automatically.</li>
 *   <li>Supports both {@link Player} and console senders transparently.</li>
 * </ul>
 *
 * <p><strong>Thread-safety:</strong> Message sending is thread-safe and may be performed
 * off the main thread. Underlying Adventure components are immutable and safe for reuse.</p>
 *
 * @see MessageConfig
 * @see BukkitMultification
 * @see MiniMessage
 * @see AudienceProvider
 * @see NoticeProvider
 */
public final class MessageService extends BukkitMultification<MessageConfig> {

    private static final MiniMessage DEFAULT_MINI_MESSAGE = MiniMessage.miniMessage();

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

    public MessageService(@NotNull MessageConfig messageConfig, @NotNull BukkitAudiences bukkitAudiences) {
        this(messageConfig, bukkitAudiences, DEFAULT_MINI_MESSAGE);
    }

    public MessageService(@NotNull MessageConfig messageConfig, @NotNull Plugin plugin) {
        this(messageConfig, BukkitAudiences.create(plugin), DEFAULT_MINI_MESSAGE);
    }

    /**
     * Returns a translation provider that always returns the same {@link MessageConfig} instance,
     * ignoring locale differences.
     *
     * @return locale-agnostic translation provider
     */
    @Override
    protected @NotNull TranslationProvider<MessageConfig> translationProvider() {
        return provider -> messageConfig;
    }

    /**
     * Returns the {@link MiniMessage}-based component serializer.
     *
     * @return component serializer for text serialization/deserialization
     */
    @Override
    protected @NotNull ComponentSerializer<Component, Component, String> serializer() {
        return miniMessage;
    }

    /**
     * Converts Bukkit {@link CommandSender}s into Adventure audiences
     * using the configured {@link AudienceProvider}.
     *
     * <p>Players are mapped to player audiences, while other senders
     * (e.g., console or command blocks) are mapped to {@link AudienceProvider#console()}.</p>
     *
     * @return non-null audience converter
     */
    @Override
    protected @NotNull AudienceConverter<CommandSender> audienceConverter() {
        return sender -> {
            if (sender instanceof Player player) {
                return audienceProvider.player(player.getUniqueId());
            }
            return audienceProvider.console();
        };
    }

    /**
     * Sends a localized or static notice message to the specified Bukkit {@link CommandSender}.
     *
     * <p>The notice is resolved through the active {@link MessageConfig}
     * and rendered using {@link MiniMessage} formatting.</p>
     *
     * @param sender non-null Bukkit command sender (player, console, etc.)
     * @param notice non-null notice provider bound to {@link MessageConfig}
     * @throws NullPointerException if {@code sender} or {@code notice} is null
     */
    public void send(@NotNull CommandSender sender, @NotNull NoticeProvider<MessageConfig> notice) {
        Validator.notNull(sender, "sender cannot be null");
        Validator.notNull(notice, "notice cannot be null");
        create().viewer(sender).notice(notice).send();
    }

    /**
     * Shuts down the underlying {@link AudienceProvider} to release Adventure resources.
     *
     * <p>This should be called during plugin disable to avoid memory leaks or
     * lingering references to the plugin classloader.</p>
     */
    public void shutdown() {
        Validator.notNull(audienceProvider, "audienceProvider cannot be null");
        audienceProvider.close();
    }
}
