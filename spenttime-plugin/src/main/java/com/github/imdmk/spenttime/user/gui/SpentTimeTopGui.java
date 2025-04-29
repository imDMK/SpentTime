package com.github.imdmk.spenttime.user.gui;

import com.github.imdmk.spenttime.gui.AbstractGui;
import com.github.imdmk.spenttime.gui.ConfirmGui;
import com.github.imdmk.spenttime.gui.configuration.item.GuiItemConfiguration;
import com.github.imdmk.spenttime.message.MessageService;
import com.github.imdmk.spenttime.shared.Formatter;
import com.github.imdmk.spenttime.task.TaskScheduler;
import com.github.imdmk.spenttime.user.BukkitSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.ComponentUtil;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;

public class SpentTimeTopGui extends AbstractGui {

    private final Server server;
    private final GuiItemConfiguration itemConfig;
    private final SpentTimeTopGuiConfiguration guiConfig;
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final TaskScheduler taskScheduler;
    private final BukkitSpentTimeService bukkitSpentTimeService;

    public SpentTimeTopGui(Server server, GuiItemConfiguration itemConfig, SpentTimeTopGuiConfiguration guiConfig, UserRepository userRepository, MessageService messageService, TaskScheduler taskScheduler, BukkitSpentTimeService bukkitSpentTimeService) {
        super(itemConfig);
        this.server = server;
        this.itemConfig = itemConfig;
        this.guiConfig = guiConfig;
        this.userRepository = userRepository;
        this.messageService = messageService;
        this.taskScheduler = taskScheduler;
        this.bukkitSpentTimeService = bukkitSpentTimeService;
    }

    public void open(Player viewer, List<User> topUsers) {
        BaseGui gui = this.createGuiBuilder(this.guiConfig.type)
                .title(ComponentUtil.deserialize(this.guiConfig.title))
                .rows(6)
                .disableAllInteractions()
                .create();

        if (this.itemConfig.fillBorder) {
            gui.getFiller().fillBorder(this.itemConfig.borderItem.asGuiItem());
        }

        this.prepareNavigationItems(gui, viewer);

        for (int i = 0; i < topUsers.size(); i++) {
            User topUser = topUsers.get(i);
            int topUserPosition = i + 1;

            gui.addItem(this.createUserItem(gui, viewer, topUser, topUserPosition, topUsers));
        }

        this.taskScheduler.runSync(() -> gui.open(viewer));
    }

    private GuiItem createUserItem(BaseGui gui, Player viewer, User user, int userPosition, List<User> topUsers) {
        OfflinePlayer userPlayer = this.server.getOfflinePlayer(user.getUuid());

        Formatter formatter = new Formatter()
                .placeholder("{PLAYER}", user.getName())
                .placeholder("{POSITION}", userPosition)
                .placeholder("{TIME}", DurationUtil.toHumanReadable(user.getSpentTimeAsDuration()))
                .placeholder("{CLICK}", this.guiConfig.headAdminClick.name());

        ItemBuilder headItem = ItemBuilder.from(this.guiConfig.headItem.asItemStack())
                .setSkullOwner(userPlayer);

        if (viewer.hasPermission(this.guiConfig.permissionToResetSpentTime)) {
            headItem.lore(ComponentUtil.deserialize(this.guiConfig.headLoreAdmin));
        }

        return headItem.asGuiItem(event -> {
            if (event.getClick() != this.guiConfig.headAdminClick) {
                return;
            }

            if (!viewer.hasPermission(this.guiConfig.permissionToResetSpentTime)) {
                return;
            }

            new ConfirmGui(this.taskScheduler)
                    .create("<red>Reset " + user.getName() + " player spent time?")
                    .afterConfirm(e -> {
                        gui.close(viewer);
                        this.resetUserTime(viewer, userPlayer, user);
                    })
                    .afterCancel(e -> this.open(viewer, topUsers))
                    .open(viewer);
        });
    }

    private void resetUserTime(Player viewer, OfflinePlayer userPlayer, User user) {
        user.setSpentTime(0L);
        this.userRepository.save(user).thenAcceptAsync(updated -> {
                    this.bukkitSpentTimeService.resetSpentTime(userPlayer);

                    this.messageService.create()
                            .notice(notice -> notice.targetSpentTimeHasBeenReset)
                            .placeholder("{PLAYER}", user.getName())
                            .viewer(viewer)
                            .send();
                })
                .exceptionally(throwable -> {
                    this.messageService.create()
                            .notice(notice -> notice.targetSpentTimeResetError)
                            .viewer(viewer)
                            .send();
                    throw new RuntimeException(throwable);
                });
    }

    private void prepareNavigationItems(BaseGui gui, Player viewer) {
        if (gui instanceof PaginatedGui paginatedGui) {
            GuiItem nextPageItem = this.createNextPageItem(paginatedGui);
            GuiItem previousPageItem = this.createPreviousPageItem(paginatedGui);

            gui.setItem(this.itemConfig.nextPageItem.slot(), nextPageItem);
            gui.setItem(this.itemConfig.previousPageItem.slot(), previousPageItem);
        }

        GuiItem exitItem = this.createExitItem(close -> gui.close(viewer));
        gui.setItem(this.itemConfig.exitItem.slot(), exitItem);
    }

    public SpentTimeTopGuiConfiguration getGuiConfig() {
        return this.guiConfig;
    }
}
