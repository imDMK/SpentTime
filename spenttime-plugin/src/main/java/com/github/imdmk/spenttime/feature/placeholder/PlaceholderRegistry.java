package com.github.imdmk.spenttime.feature.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Registry responsible for managing the lifecycle of {@link PlaceholderExpansion} instances.
 * <p>
 * Provides a mechanism for safe registration and unregistration of placeholders in bulk or individually.
 */
public final class PlaceholderRegistry {

    private final Set<PlaceholderExpansion> registeredExpansions = new HashSet<>();

    /**
     * Registers and tracks a {@link PlaceholderExpansion} instance.
     *
     * @param placeholder the placeholder to register; must not be null
     * @throws NullPointerException if the placeholder is null
     */
    public void register(@NotNull PlaceholderExpansion placeholder) {
        Objects.requireNonNull(placeholder, "placeholder cannot be null");

        if (placeholder.register()) {
            this.registeredExpansions.add(placeholder);
        }
    }

    /**
     * Unregisters a previously registered {@link PlaceholderExpansion} instance.
     * <p>
     * Does not throw an error if the placeholder was not previously registered.
     *
     * @param placeholder the placeholder to unregister; must not be null
     * @throws NullPointerException if the placeholder is null
     */
    public void unregister(@NotNull PlaceholderExpansion placeholder) {
        Objects.requireNonNull(placeholder, "placeholder cannot be null");

        if (placeholder.unregister()) {
            this.registeredExpansions.remove(placeholder);
        }
    }

    /**
     * Unregisters all currently registered {@link PlaceholderExpansion} instances and clears the registry.
     */
    public void unregisterAll() {
        this.registeredExpansions.forEach(this::unregister);
    }

    /**
     * Returns an unmodifiable view of currently registered placeholders.
     *
     * @return the registered placeholder set
     */
    @NotNull
    public Set<PlaceholderExpansion> getRegisteredExpansions() {
        return Set.copyOf(this.registeredExpansions);
    }
}

