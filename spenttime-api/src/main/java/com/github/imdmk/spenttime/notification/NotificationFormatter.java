package com.github.imdmk.spenttime.notification;

import java.util.LinkedHashMap;
import java.util.Map;

public class NotificationFormatter {

    private NotificationType type;
    private String message;

    private final Map<String, String> placeholders = new LinkedHashMap<>();

    public NotificationFormatter notification(Notification notification) {
        this.type = notification.type();
        this.message = notification.message();
        return this;
    }

    public NotificationFormatter type(NotificationType type) {
        this.type = type;
        return this;
    }

    public NotificationFormatter placeholder(String from, String to) {
        this.placeholders.put(from, to);
        return this;
    }

    public NotificationFormatter placeholder(String from, Iterable<? extends CharSequence> sequences) {
        return this.placeholder(from, String.join(", ", sequences));
    }

    public <T> NotificationFormatter placeholder(String from, T to) {
        return this.placeholder(from, to.toString());
    }

    public Notification build() {
        StringBuilder replacedMessage = new StringBuilder(this.message);

        for (Map.Entry<String, String> placeholder : this.placeholders.entrySet()) {
            String key = placeholder.getKey();
            String replacement = placeholder.getValue();

            this.replaceAllOccurrences(replacedMessage, key, replacement);
        }

        return new Notification(this.type, replacedMessage.toString());
    }

    private void replaceAllOccurrences(StringBuilder builder, String message, String replacement) {
        int index = builder.indexOf(message);

        while (index != -1) {
            builder.replace(index, index + message.length(), replacement);
            index = builder.indexOf(message, index + replacement.length());
        }
    }
}
