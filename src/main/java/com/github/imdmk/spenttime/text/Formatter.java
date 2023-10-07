package com.github.imdmk.spenttime.text;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

public class Formatter {

    private final Map<String, String> placeholders = new LinkedHashMap<>();

    @CheckReturnValue
    public Formatter placeholder(@Nonnull String from, String to) {
        this.placeholders.put(from, to);
        return this;
    }

    @CheckReturnValue
    public Formatter placeholder(@Nonnull String from, Iterable<? extends CharSequence> sequences) {
        return this.placeholder(from, String.join(", ", sequences));
    }

    @CheckReturnValue
    public <T> Formatter placeholder(@Nonnull String from, T to) {
        return this.placeholder(from, to.toString());
    }

    public String format(String message) {
        StringBuilder replacedMessage = new StringBuilder(message);

        for (Map.Entry<String, String> placeholder : this.placeholders.entrySet()) {
            String key = placeholder.getKey();
            String replacement = placeholder.getValue();

            this.replaceAllOccurrences(replacedMessage, key, replacement);
        }

        return replacedMessage.toString();
    }

    private void replaceAllOccurrences(StringBuilder builder, String message, String replacement) {
        int index = builder.indexOf(message);

        while (index != -1) {
            builder.replace(index, index + message.length(), replacement);
            index = builder.indexOf(message, index + replacement.length());
        }
    }
}
