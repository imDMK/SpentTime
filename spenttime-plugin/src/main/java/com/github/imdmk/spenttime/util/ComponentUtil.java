package com.github.imdmk.spenttime.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Arrays;
import java.util.List;

public class ComponentUtil {

    private static final CharSequence LEGACY_CHAR = "§";

    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private ComponentUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    public static Component notItalic(String text) {
        return MINI_MESSAGE.deserialize("<!italic>" + text);
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

    public static Component deserialize(String text) {
        return text.contains(LEGACY_CHAR)
                ? LEGACY_COMPONENT_SERIALIZER.deserialize(text)
                : MINI_MESSAGE.deserialize(text);
    }

    public static List<Component> deserialize(List<String> strings) {
        return strings.stream()
                .map(ComponentUtil::deserialize)
                .toList();
    }
}
