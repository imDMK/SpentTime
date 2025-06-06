package com.github.imdmk.spenttime.infrastructure.message;

import com.eternalcode.multification.adventure.AudienceConverter;
import com.eternalcode.multification.bukkit.BukkitMultification;
import com.eternalcode.multification.notice.provider.NoticeProvider;
import com.eternalcode.multification.translation.TranslationProvider;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MessageService extends BukkitMultification<MessageConfiguration> {

    private final MessageConfiguration messageConfiguration;
    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;

    public MessageService(
            @NotNull MessageConfiguration messageConfiguration,
            @NotNull AudienceProvider audienceProvider,
            @NotNull MiniMessage miniMessage
    ) {
        this.messageConfiguration = Objects.requireNonNull(messageConfiguration, "messageConfiguration cannot be null");
        this.audienceProvider = Objects.requireNonNull(audienceProvider, "audienceProvider cannot be null");
        this.miniMessage = Objects.requireNonNull(miniMessage, "miniMessage cannot be null");
    }

    @Override
    protected @NotNull TranslationProvider<MessageConfiguration> translationProvider() {
        return locale -> this.messageConfiguration;
    }

    @Override
    protected @NotNull ComponentSerializer<Component, Component, String> serializer() {
        return this.miniMessage;
    }

    @Override
    protected @NotNull AudienceConverter<CommandSender> audienceConverter() {
        return commandSender -> {
            if (commandSender instanceof Player player) {
                return this.audienceProvider.player(player.getUniqueId());
            }

            return this.audienceProvider.console();
        };
    }

    public void send(CommandSender sender, NoticeProvider<MessageConfiguration> notice) {
        this.create().viewer(sender).notice(notice).send();
    }

    public void close() {
        this.audienceProvider.close();
    }
}
