package com.github.imdmk.spenttime.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

public class ComponentUtil {

    public static final CharSequence LEGACY_CHAR = "§";
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private ComponentUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    public static Component createItalic(String text) {
        return MINI_MESSAGE.deserialize("<!italic>" + text);
    }

    public static Component deserialize(String text) {
        return text.contains(LEGACY_CHAR)
                ? LegacyComponentSerializer.legacySection().deserialize(text)
                : MINI_MESSAGE.deserialize(text);
    }

    public static List<Component> deserialize(List<String> strings) {
        return strings.stream()
                .map(ComponentUtil::deserialize)
                .toList();
    }
}
