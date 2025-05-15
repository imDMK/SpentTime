package com.github.imdmk.spenttime.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ComponentUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private ComponentUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    public static Component notItalic(String text) {
        return MINI_MESSAGE.deserialize(text)
                .decoration(TextDecoration.ITALIC, false);
    }

    public static List<Component> notItalic(String... text) {
        return Arrays.stream(text).map(ComponentUtil::notItalic).toList();
    }

    public static List<Component> notItalic(List<String> texts) {
        return texts.stream().map(ComponentUtil::notItalic).toList();
    }

    public static String serialize(Component component) {
        return MINI_MESSAGE.serialize(component);
    }

    public static Component text(String text) {
        return MINI_MESSAGE.deserialize(text);
    }

    public static List<Component> text(String... texts) {
        List<Component> components = new ArrayList<>();
        for (String text : texts) {
            components.add(MINI_MESSAGE.deserialize(text));
        }

        return components;
    }

    public static List<Component> text(List<String> strings) {
        return strings.stream()
                .map(ComponentUtil::text)
                .toList();
    }
}
