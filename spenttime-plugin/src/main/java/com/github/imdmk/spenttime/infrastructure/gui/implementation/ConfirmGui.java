package com.github.imdmk.spenttime.infrastructure.gui.implementation;

import com.github.imdmk.spenttime.infrastructure.gui.AbstractGui;
import com.github.imdmk.spenttime.infrastructure.gui.ParameterizedGui;
import com.github.imdmk.spenttime.infrastructure.gui.configuration.ConfigGuiItem;
import com.github.imdmk.spenttime.infrastructure.gui.configuration.GuiConfiguration;
import com.github.imdmk.spenttime.task.TaskScheduler;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ConfirmGui extends AbstractGui implements ParameterizedGui<ConfirmGuiAction> {

    public static final String GUI_IDENTIFIER = "confirmation";
    private static final int ROWS = 6;

    private final GuiConfiguration guiConfiguration;

    public ConfirmGui(
            @NotNull GuiConfiguration guiConfiguration,
            @NotNull TaskScheduler taskScheduler
    ) {
        super(guiConfiguration, taskScheduler);
        this.guiConfiguration = Objects.requireNonNull(guiConfiguration, "guiConfiguration cannot be null");
    }

    @Override
    public @NotNull BaseGui createGui(@NotNull Player viewer, @NotNull ConfirmGuiAction action) {
        return Gui.gui()
                .title(this.getConfig().title)
                .rows(ROWS)
                .disableAllInteractions()
                .create();
    }

    @Override
    public void prepareBorderItems(@NotNull BaseGui gui) {
        if (this.guiConfiguration.fillBorder) {
            gui.getFiller().fillBorder(this.guiConfiguration.borderItem.asGuiItem());
        }
    }

    @Override
    public void prepareNavigationItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull ConfirmGuiAction action) {
        this.setExitPageItem(gui, e -> gui.close(viewer));
    }

    @Override
    public void prepareItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull ConfirmGuiAction action) {
        ConfigGuiItem confirmItem = this.getConfig().confirmItem;
        ConfigGuiItem cancelItem = this.getConfig().cancelItem;

        GuiItem confirmGuiItem = confirmItem.asGuiItem(confirm -> action.onConfirmAccept(viewer));
        GuiItem cancelGuiItem = cancelItem.asGuiItem(cancel -> action.onCancelAccept(viewer));

        gui.setItem(confirmItem.slot(), confirmGuiItem);
        gui.setItem(cancelItem.slot(), cancelGuiItem);
    }

    @Override
    public void defaultClickAction(@NotNull BaseGui gui, @NotNull Player viewer) {
        this.guiConfiguration.clickSound.play(viewer);
    }

    @Override
    public @NotNull String getIdentifier() {
        return GUI_IDENTIFIER;
    }

    private @NotNull GuiConfiguration.ConfirmationGuiConfiguration getConfig() {
        return this.guiConfiguration.confirmationGui;
    }
}
