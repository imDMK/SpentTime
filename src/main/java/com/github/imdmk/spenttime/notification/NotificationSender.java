package com.github.imdmk.spenttime.notification;

import com.github.imdmk.spenttime.util.ComponentUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NotificationSender {

    private final AudienceProvider audienceProvider;

    public NotificationSender(AudienceProvider audienceProvider) {
        this.audienceProvider = audienceProvider;
    }

    public void send(CommandSender sender, Notification notification) {
        Audience audience = this.createAudience(sender);

        NotificationType type = notification.type();
        Component message = ComponentUtil.deserialize(notification.message());

        switch (type) {
            case CHAT -> audience.sendMessage(message);
            case ACTIONBAR -> audience.sendActionBar(message);
            case TITLE -> {
                Title title = Title.title(message, Component.empty(), Title.DEFAULT_TIMES);

                audience.showTitle(title);
            }
            case SUBTITLE -> {
                Title subtitle = Title.title(Component.empty(), message, Title.DEFAULT_TIMES);

                audience.showTitle(subtitle);
            }
            case DISABLED -> {
            }
            default -> throw new IllegalStateException("Unexpected notification type: " + type);
        }
    }

    public Audience createAudience(CommandSender sender) {
        if (sender instanceof Player player) {
            return this.audienceProvider.player(player.getUniqueId());
        }

        return this.audienceProvider.console();
    }
}