package com.github.imdmk.spenttime.feature.gui.implementation;

import com.github.imdmk.spenttime.feature.gui.AbstractGui;
import com.github.imdmk.spenttime.feature.gui.ParameterizedGui;
import com.github.imdmk.spenttime.feature.gui.configuration.GuiConfiguration;
import com.github.imdmk.spenttime.feature.gui.configuration.item.ItemGui;
import com.github.imdmk.spenttime.feature.gui.configuration.item.ItemGuiConfiguration;
import com.github.imdmk.spenttime.task.TaskScheduler;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ConfirmationGui extends AbstractGui implements ParameterizedGui<ConfirmationGuiAction> {

    public static final String GUI_IDENTIFIER = "confirmation";

    private final GuiConfiguration guiConfiguration;
    private final ItemGuiConfiguration itemConfiguration;

    public ConfirmationGui(
            @NotNull GuiConfiguration guiConfiguration,
            @NotNull ItemGuiConfiguration itemConfiguration,
            @NotNull TaskScheduler taskScheduler
    ) {
        super(itemConfiguration, taskScheduler);
        this.guiConfiguration = guiConfiguration;
        this.itemConfiguration = itemConfiguration;
    }

    @Override
    public @NotNull BaseGui createGui(@NotNull Player viewer, @NotNull ConfirmationGuiAction action) {
        return Gui.gui()
                .title(this.getConfig().title)
                .rows(6)
                .disableAllInteractions()
                .create();
    }

    @Override
    public void prepareBorderItems(@NotNull BaseGui gui) {
        if (this.itemConfiguration.fillBorder) {
            gui.getFiller().fillBorder(this.itemConfiguration.borderItem.asGuiItem());
        }
    }

    @Override
    public void prepareNavigationItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull ConfirmationGuiAction action) {
        this.createExitItem(this.itemConfiguration.exitItem.slot(), exit -> gui.close(viewer)).forEach(gui::setItem);
    }

    @Override
    public void prepareItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull ConfirmationGuiAction action) {
        ItemGui confirmItem = this.getConfig().confirmItem;
        ItemGui cancelItem = this.getConfig().cancelItem;

        GuiItem confirmGuiItem = confirmItem.asGuiItem(confirm -> action.onConfirmAccept(viewer));
        GuiItem cancelGuiItem = cancelItem.asGuiItem(cancel -> action.onCancelAccept(viewer));

        gui.setItem(confirmItem.slot(), confirmGuiItem);
        gui.setItem(cancelItem.slot(), cancelGuiItem);
    }

    @Override
    public @NotNull String getIdentifier() {
        return GUI_IDENTIFIER;
    }

    private @NotNull GuiConfiguration.ConfirmationGuiConfiguration getConfig() {
        return this.guiConfiguration.confirmationGui;
    }
}
