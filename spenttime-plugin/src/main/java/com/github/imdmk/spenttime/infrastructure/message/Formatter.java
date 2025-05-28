package com.github.imdmk.spenttime.infrastructure.message;

import com.eternalcode.multification.adventure.PlainComponentSerializer;
import com.github.imdmk.spenttime.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for formatting {@link Component} objects by applying placeholder replacements.
 * Supports a fluent API for defining placeholders with various input types.
 */
public class Formatter {

    private static final PlainComponentSerializer PLAIN_SERIALIZER = new PlainComponentSerializer();

    private final Map<String, String> placeholders = new LinkedHashMap<>();

    /**
     * Adds a placeholder replacement using raw strings.
     *
     * @param from the placeholder key to replace
     * @param to   the replacement value
     * @return this formatter instance for method chaining
     */
    @Contract("_,_ -> this")
    public Formatter placeholder(@NotNull String from, @NotNull String to) {
        this.placeholders.put(from, to);
        return this;
    }

    /**
     * Adds a placeholder replacement by joining a sequence of strings with a comma and space.
     *
     * @param from      the placeholder key to replace
     * @param sequences the values to join and use as the replacement
     * @return this formatter instance for method chaining
     */
    @Contract("_,_ -> this")
    public Formatter placeholder(@NotNull String from, @NotNull Iterable<? extends CharSequence> sequences) {
        return this.placeholder(from, String.join(", ", sequences));
    }

    /**
     * Adds a placeholder replacement for any object by using its {@code toString()} representation.
     *
     * @param from the placeholder key to replace
     * @param to   the object whose string value will be used as the replacement
     * @return this formatter instance for method chaining
     */
    @Contract("_,_ -> this")
    public <T> Formatter placeholder(@NotNull String from, @NotNull T to) {
        return this.placeholder(from, to.toString());
    }

    /**
     * Adds a placeholder replacement using a {@link Component}. The component is serialized
     * into a plain string representation.
     *
     * @param from the placeholder key to replace
     * @param to   the component to serialize and use as the replacement
     * @return this formatter instance for method chaining
     */
    @Contract("_,_ -> this")
    public Formatter placeholder(@NotNull String from, @NotNull Component to) {
        return this.placeholder(from, PLAIN_SERIALIZER.serialize(to));
    }

    /**
     * Applies all defined placeholder replacements to a given {@link Component}.
     *
     * @param component the input component to process
     * @return a new component with all placeholders replaced
     */
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

    /**
     * Applies all defined placeholder replacements to a list of {@link Component}s.
     *
     * @param components the list of components to process
     * @return a new list with all placeholders replaced in each component
     */
    public @NotNull List<Component> format(@NotNull List<Component> components) {
        List<Component> replaced = new ArrayList<>(components.size());

        for (Component component : components) {
            replaced.add(this.format(component));
        }

        return replaced;
    }
}
