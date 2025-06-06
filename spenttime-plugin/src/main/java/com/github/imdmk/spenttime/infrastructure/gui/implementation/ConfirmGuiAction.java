package com.github.imdmk.spenttime.infrastructure.gui.implementation;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public final class ConfirmGuiAction {

    private final Consumer<Player> onConfirm;
    private final Consumer<Player> onCancel;

    private ConfirmGuiAction(@NotNull Builder builder) {
        Objects.requireNonNull(builder, "Builder cannot be null");

        this.onConfirm = builder.onConfirm;
        this.onCancel = builder.onCancel;
    }

    public @NotNull Consumer<Player> onConfirm() {
        return this.onConfirm;
    }

    public void onConfirmAccept(@NotNull Player player) {
        this.onConfirm.accept(player);
    }

    public @NotNull Consumer<Player> onCancel() {
        return this.onCancel;
    }

    public void onCancelAccept(@NotNull Player player) {
        this.onConfirm.accept(player);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Consumer<Player> onConfirm = player -> {};
        private Consumer<Player> onCancel = player -> {};

        public Builder onConfirm(@NotNull Consumer<Player> onConfirm) {
            this.onConfirm = Objects.requireNonNull(onConfirm);
            return this;
        }

        public Builder onCancel(@NotNull Consumer<Player> onCancel) {
            this.onCancel = Objects.requireNonNull(onCancel);
            return this;
        }

        public ConfirmGuiAction build() {
            return new ConfirmGuiAction(this);
        }
    }
}

