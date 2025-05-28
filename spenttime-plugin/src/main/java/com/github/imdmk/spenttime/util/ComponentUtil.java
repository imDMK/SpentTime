package com.github.imdmk.spenttime.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for working with Adventure {@link Component} objects.
 * Provides common operations like deserialization, serialization,
 * and applying style modifications such as disabling italics.
 * <p>
 * Uses {@link MiniMessage} as the deserialization/serialization engine.
 */
public final class ComponentUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always thrown when called
     */
    private ComponentUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Deserializes a MiniMessage string into a {@link Component} and disables italic decoration.
     *
     * @param text the MiniMessage-formatted string
     * @return the resulting {@link Component} with italic style disabled
     */
    public static Component notItalic(String text) {
        return MINI_MESSAGE.deserialize(text)
                .decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Deserializes an array of MiniMessage strings into {@link Component}s,
     * with italic style disabled on each.
     *
     * @param texts array of MiniMessage-formatted strings
     * @return list of {@link Component}s with italic style disabled
     */
    public static List<Component> notItalic(String... texts) {
        return Arrays.stream(texts)
                .map(ComponentUtil::notItalic)
                .toList();
    }

    /**
     * Deserializes a list of MiniMessage strings into {@link Component}s,
     * with italic style disabled on each.
     *
     * @param texts list of MiniMessage-formatted strings
     * @return list of {@link Component}s with italic style disabled
     */
    public static List<Component> notItalic(List<String> texts) {
        return texts.stream()
                .map(ComponentUtil::notItalic)
                .toList();
    }

    /**
     * Serializes a {@link Component} into a MiniMessage string.
     *
     * @param component the component to serialize
     * @return MiniMessage string representation of the component
     */
    public static String serialize(Component component) {
        return MINI_MESSAGE.serialize(component);
    }

    /**
     * Deserializes a MiniMessage string into a {@link Component}.
     *
     * @param text the MiniMessage-formatted string
     * @return the resulting {@link Component}
     */
    public static Component text(String text) {
        return MINI_MESSAGE.deserialize(text);
    }

    /**
     * Deserializes an array of MiniMessage strings into {@link Component}s.
     *
     * @param texts array of MiniMessage-formatted strings
     * @return list of {@link Component}s
     */
    public static List<Component> text(String... texts) {
        List<Component> components = new ArrayList<>(texts.length);
        for (String text : texts) {
            components.add(MINI_MESSAGE.deserialize(text));
        }
        return components;
    }

    /**
     * Deserializes a list of MiniMessage strings into {@link Component}s.
     *
     * @param strings list of MiniMessage-formatted strings
     * @return list of {@link Component}s
     */
    public static List<Component> text(List<String> strings) {
        return strings.stream()
                .map(ComponentUtil::text)
                .toList();
    }
}
