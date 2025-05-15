package com.github.imdmk.spenttime.shared;

import com.eternalcode.multification.adventure.PlainComponentSerializer;
import com.github.imdmk.spenttime.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Formatter {

    private static final PlainComponentSerializer PLAIN_SERIALIZER = new PlainComponentSerializer();

    private final Map<String, String> placeholders = new LinkedHashMap<>();

    @Contract("_,_->this")
    public Formatter placeholder(@NotNull String from, @NotNull String to) {
        this.placeholders.put(from, to);
        return this;
    }

    @Contract("_,_->this")
    public Formatter placeholder(@NotNull String from, @NotNull Iterable<? extends CharSequence> sequences) {
        return this.placeholder(from, String.join(", ", sequences));
    }

    @Contract("_,_->this")
    public <T> Formatter placeholder(@NotNull String from, @NotNull T to) {
        return this.placeholder(from, to.toString());
    }

    @Contract("_,_->this")
    public Formatter placeholder(@NotNull String from, @NotNull Component to) {
        return this.placeholder(from, PLAIN_SERIALIZER.serialize(to));
    }

    public @NotNull Component format(@NotNull Component component) {
        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            String placeholder = entry.getKey();
            Component replacement = ComponentUtil.text(entry.getValue());

            component = component.replaceText(builder -> builder
                    .matchLiteral(placeholder)
                    .replacement(replacement)
            );
        }

        return component;
    }

    public @NotNull List<Component> format(@NotNull List<Component> component) {
        List<Component> replaced = new ArrayList<>();

        for (Component replacement : component) {
            replaced.add(this.format(replacement));
        }

        return replaced;
    }
}
