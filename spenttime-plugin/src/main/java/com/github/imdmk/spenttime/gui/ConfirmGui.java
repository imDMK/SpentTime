package com.github.imdmk.spenttime.gui;

import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.util.ComponentUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ConfirmGui {

    private final TaskScheduler taskScheduler;

    private Gui gui;

    private GuiAction<InventoryClickEvent> actionAfterConfirm;
    private GuiAction<InventoryClickEvent> actionAfterCancel;

    private boolean closeAfterCancel;

    public ConfirmGui(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public ConfirmGui create(Component title) {
        this.gui = Gui.gui()
                .title(title)
                .rows(6)
                .disableAllInteractions()
                .create();
        return this;
    }

    public ConfirmGui afterConfirm(GuiAction<InventoryClickEvent> actionAfterConfirm) {
        this.actionAfterConfirm = actionAfterConfirm;
        return this;
    }

    public ConfirmGui afterCancel(GuiAction<InventoryClickEvent> actionAfterCancel) {
        this.actionAfterCancel = actionAfterCancel;
        return this;
    }

    public ConfirmGui closeAfterCancel() {
        this.closeAfterCancel = true;
        return this;
    }

    public void open(Player player) {
        GuiItem cancelItem = ItemBuilder.from(Material.RED_CONCRETE)
                .name(ComponentUtil.createItalic("<red>Cancel"))
                .asGuiItem();

        this.setCloseAfterCancelAction(cancelItem, player);

        GuiItem confirmItem = ItemBuilder.from(Material.GREEN_CONCRETE)
                .name(ComponentUtil.createItalic("<green>Confirm"))
                .asGuiItem();

        this.setCloseAfterConfirmAction(confirmItem);

        this.gui.getFiller().fillBorder(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).asGuiItem());

        this.gui.setItem(21, confirmItem);
        this.gui.setItem(30, confirmItem);

        this.gui.setItem(23, cancelItem);
        this.gui.setItem(32, cancelItem);

        this.taskScheduler.runSync(() -> this.gui.open(player));
    }

    private void setCloseAfterConfirmAction(GuiItem guiItem) {
        if (this.actionAfterConfirm != null) {
            guiItem.setAction(this.actionAfterConfirm);
        }
    }

    private void setCloseAfterCancelAction(GuiItem guiItem, Player player) {
        if (this.actionAfterCancel != null) {
            guiItem.setAction(this.actionAfterCancel);
        }
        else if (this.closeAfterCancel) {
            guiItem.setAction(event -> this.gui.close(player));
        }
    }
}