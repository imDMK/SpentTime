package com.github.imdmk.spenttime.notification;

import com.github.imdmk.spenttime.util.ComponentUtil;
import net.kyori.adventure.text.Component;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public record Notification(NotificationType type, Component message) {

    public Notification(NotificationType notificationType, String message) {
        this(notificationType, ComponentUtil.deserialize(message));
    }

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static class NotificationBuilder {

        private NotificationType type;
        private Component message;

        private final Map<String, String> placeholders = new HashMap<>();

        @CheckReturnValue
        public NotificationBuilder fromNotification(@Nonnull Notification notification) {
            this.type = notification.type();
            this.message = notification.message();
            return this;
        }

        @CheckReturnValue
        public NotificationBuilder type(@Nonnull NotificationType type) {
            this.type = type;
            return this;
        }

        @CheckReturnValue
        public NotificationBuilder message(@Nonnull Component message) {
            this.message = message;
            return this;
        }

        @CheckReturnValue
        public NotificationBuilder placeholder(@Nonnull String from, String to) {
            this.placeholders.put(from, to);
            return this;
        }

        @CheckReturnValue
        public NotificationBuilder placeholder(@Nonnull String from, Iterable<? extends CharSequence> sequences) {
            this.placeholders.put(from, String.join(", ", sequences));
            return this;
        }

        @CheckReturnValue
        public <T> NotificationBuilder placeholder(@Nonnull String from, T to) {
            this.placeholders.put(from, to.toString());
            return this;
        }

        private void replacePlaceholders() {
            for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
                this.message = this.message.replaceText(b ->
                        b.matchLiteral(entry.getKey()).replacement(entry.getValue()));
            }
        }

        public Notification build() {
            this.replacePlaceholders();

            return new Notification(this.type, this.message);
        }
    }
}
