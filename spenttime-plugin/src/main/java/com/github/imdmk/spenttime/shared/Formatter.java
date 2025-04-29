package com.github.imdmk.spenttime.shared;

import com.eternalcode.multification.adventure.PlainComponentSerializer;
import com.github.imdmk.spenttime.util.ComponentUtil;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Formatter {

    private static final PlainComponentSerializer PLAIN_SERIALIZER = new PlainComponentSerializer();

    private final Map<String, String> placeholders = new LinkedHashMap<>();

    public Formatter placeholder(String from, String to) {
        this.placeholders.put(from, to);
        return this;
    }

    public Formatter placeholder(String from, Iterable<? extends CharSequence> sequences) {
        return this.placeholder(from, String.join(", ", sequences));
    }

    public <T> Formatter placeholder(String from, T to) {
        return this.placeholder(from, to.toString());
    }

    public Formatter placeholder(String from, Component to) {
        return this.placeholder(from, PLAIN_SERIALIZER.serialize(to));
    }

    public Component format(Component component) {
        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            String placeholder = entry.getKey();
            Component replacement = ComponentUtil.deserialize(entry.getValue());

            component = component.replaceText(builder -> builder
                    .matchLiteral(placeholder)
                    .replacement(replacement)
            );
        }

        return component;
    }

    public List<Component> format(List<Component> component) {
        List<Component> replaced = new ArrayList<>();

        for (Component replacement : component) {
            replaced.add(this.format(replacement));
        }

        return replaced;
    }
}
