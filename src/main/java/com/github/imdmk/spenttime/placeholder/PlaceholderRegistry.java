package com.github.imdmk.spenttime.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.HashSet;
import java.util.Set;

public class PlaceholderRegistry {

    private final Set<PlaceholderExpansion> placeholderExpansions = new HashSet<>();

    public void register(PlaceholderExpansion placeholder) {
        this.placeholderExpansions.add(placeholder);

        placeholder.register();
    }

    public void unregister(PlaceholderExpansion placeholderExpansion) {
        this.placeholderExpansions.remove(placeholderExpansion);

        placeholderExpansion.unregister();
    }

    public void unregisterAll() {
        this.placeholderExpansions.forEach(this::unregister);
    }
}
