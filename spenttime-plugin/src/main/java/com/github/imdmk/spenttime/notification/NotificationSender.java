package com.github.imdmk.spenttime.notification;

import com.github.imdmk.spenttime.text.Formatter;
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
        this.send(sender, notification.type(), notification.message());
    }

    public void send(CommandSender sender, Notification notification, Formatter formatter) {
        NotificationType type = notification.type();
        String message = formatter.format(notification.message());

        this.send(sender, type, message);
    }

    public void send(CommandSender sender, NotificationType type, String message) {
        Audience audience = this.createAudience(sender);

        Component deserializedMessage = ComponentUtil.deserialize(message);

        switch (type) {
            case CHAT -> audience.sendMessage(deserializedMessage);
            case ACTIONBAR -> audience.sendActionBar(deserializedMessage);
            case TITLE -> {
                Title title = Title.title(deserializedMessage, Component.empty(), Title.DEFAULT_TIMES);

                audience.showTitle(title);
            }
            case SUBTITLE -> {
                Title subtitle = Title.title(Component.empty(), deserializedMessage, Title.DEFAULT_TIMES);

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
