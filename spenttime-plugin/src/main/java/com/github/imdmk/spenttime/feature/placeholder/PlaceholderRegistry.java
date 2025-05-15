package com.github.imdmk.spenttime.feature.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PlaceholderRegistry {

    private final Set<PlaceholderExpansion> placeholderExpansions = new HashSet<>();

    public void register(@NotNull PlaceholderExpansion placeholder) {
        this.placeholderExpansions.add(placeholder);

        placeholder.register();
    }

    public void unregister(@NotNull PlaceholderExpansion placeholderExpansion) {
        placeholderExpansion.unregister();
    }

    public void unregisterAll() {
        this.placeholderExpansions.forEach(this::unregister);
    }
}
