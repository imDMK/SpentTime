package com.github.imdmk.spenttime.notification;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public record Notification(NotificationType type, String message) {

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static class NotificationBuilder {

        private NotificationType type;
        private String message;

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
        public NotificationBuilder message(@Nonnull String message) {
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
                this.message = this.message.replace(entry.getKey(), entry.getValue());
            }
        }

        public Notification build() {
            this.replacePlaceholders();

            return new Notification(this.type, this.message);
        }
    }
}
