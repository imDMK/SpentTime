package com.github.imdmk.spenttime.gui.implementation;

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

        if (this.actionAfterCancel != null) {
            cancelItem.setAction(this.actionAfterCancel);
        }

        GuiItem confirmItem = ItemBuilder.from(Material.GREEN_CONCRETE)
                .name(ComponentUtil.createItalic("<green>Confirm"))
                .asGuiItem();

        if (this.actionAfterConfirm != null) {
            confirmItem.setAction(this.actionAfterConfirm);
        }
        else if (this.closeAfterCancel) {
            confirmItem.setAction(event -> this.gui.close(player));
        }

        this.gui.getFiller().fillBorder(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).asGuiItem());

        this.gui.setItem(21, confirmItem);
        this.gui.setItem(30, confirmItem);

        this.gui.setItem(23, cancelItem);
        this.gui.setItem(32, cancelItem);

        this.taskScheduler.runSync(() -> this.gui.open(player));
    }
}
