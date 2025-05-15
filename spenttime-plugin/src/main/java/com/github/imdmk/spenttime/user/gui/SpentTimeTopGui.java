package com.github.imdmk.spenttime.user.gui;

import com.github.imdmk.spenttime.feature.gui.AbstractGui;
import com.github.imdmk.spenttime.feature.gui.GuiProvider;
import com.github.imdmk.spenttime.feature.gui.ParameterizedGui;
import com.github.imdmk.spenttime.feature.gui.configuration.GuiConfiguration;
import com.github.imdmk.spenttime.feature.gui.configuration.item.ItemGui;
import com.github.imdmk.spenttime.feature.gui.configuration.item.ItemGuiConfiguration;
import com.github.imdmk.spenttime.feature.gui.implementation.ConfirmationGui;
import com.github.imdmk.spenttime.feature.gui.implementation.ConfirmationGuiAction;
import com.github.imdmk.spenttime.feature.message.MessageService;
import com.github.imdmk.spenttime.shared.Formatter;
import com.github.imdmk.spenttime.task.TaskScheduler;
import com.github.imdmk.spenttime.user.BukkitSpentTime;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserService;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpentTimeTopGui extends AbstractGui implements ParameterizedGui<List<User>> {

    public static final String GUI_IDENTIFIER = "spenttimetop";

    private final Logger logger;
    private final Server server;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BukkitSpentTime bukkitSpentTime;
    private final MessageService messageService;
    private final GuiConfiguration guiConfiguration;
    private final ItemGuiConfiguration itemConfiguration;

    public SpentTimeTopGui(
            @NotNull Logger logger,
            @NotNull Server server,
            @NotNull UserService userService,
            @NotNull UserRepository userRepository,
            @NotNull BukkitSpentTime bukkitSpentTime,
            @NotNull MessageService messageService,
            @NotNull TaskScheduler taskScheduler,
            @NotNull GuiConfiguration guiConfiguration,
            @NotNull ItemGuiConfiguration itemConfiguration
    ) {
        super(itemConfiguration, taskScheduler);
        this.logger = logger;
        this.server = server;
        this.userService = userService;
        this.userRepository = userRepository;
        this.bukkitSpentTime = bukkitSpentTime;
        this.messageService = messageService;
        this.guiConfiguration = guiConfiguration;
        this.itemConfiguration = itemConfiguration;
    }

    @Override
    public @NotNull BaseGui createGui(@NotNull Player viewer, @NotNull List<User> topUsers) {
        return this.createGuiBuilder(this.getConfig().type)
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
    public void prepareNavigationItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull List<User> topUsers) {
        this.createExitItem(this.itemConfiguration.exitItem.slot(), exit -> gui.close(viewer)).forEach(gui::setItem);

        if (gui instanceof PaginatedGui paginated) {
            this.createNextPageItem(paginated, this.itemConfiguration.paginatedGui.nextPageItem.slot()).forEach(gui::setItem);
            this.createPreviousPageItem(paginated, this.itemConfiguration.paginatedGui.previousPageItem.slot()).forEach(gui::setItem);
        }
    }

    @Override
    public void prepareItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull List<User> topUsers) {
        for (int i = 0; i < topUsers.size(); i++) {
            User user = topUsers.get(i);
            int position = i + 1;

            gui.addItem(this.createHeadItem(viewer, user, this.createFormatter(user, position), topUsers.size()));
        }
    }

    private Formatter createFormatter(User user, int position) {
        return new Formatter()
                .placeholder("{PLAYER}", user.getName())
                .placeholder("{POSITION}", position)
                .placeholder("{TIME}", DurationUtil.format(user.getSpentTimeAsDuration()))
                .placeholder("{CLICK}", this.getConfig().headItemClick.name());
    }

    private @NotNull GuiItem createHeadItem(@NotNull Player viewer, @NotNull User user, @NotNull Formatter formatter, int querySize) {
        ItemGui headItem = this.getConfig().headItem.toBuilder()
                .loreComponent(this.hasResetPermission(viewer) ? this.getConfig().headItem.lore() : this.getConfig().headItemAdminLore)
                .build();

        return ItemBuilder.skull()
                .owner(this.server.getOfflinePlayer(user.getUuid()))
                .name(formatter.format(headItem.name()))
                .lore(formatter.format(headItem.lore()))
                .enchant(headItem.enchantments())
                .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                .asGuiItem(event -> {
                    if (event.getClick() != this.getConfig().headItemClick) {
                        return;
                    }

                    if (!this.hasResetPermission(viewer)) {
                        return;
                    }

                    this.openResetConfirmGui(viewer, user, querySize);
                });
    }

    private void openResetConfirmGui(@NotNull Player admin, @NotNull User target, int querySize) {
        GuiProvider.openGui(
                ConfirmationGui.GUI_IDENTIFIER,
                admin,
                ConfirmationGuiAction.builder()
                        .onConfirm(player -> {
                            target.setSpentTime(BukkitSpentTime.ZERO_SPENT_TIME);
                            this.bukkitSpentTime.resetSpentTime(target.getUuid());

                            this.userService.saveUser(target)
                                    .thenCompose(user -> {
                                        this.messageService.create()
                                                .notice(notice -> notice.playerTimeReset)
                                                .placeholder("{PLAYER}", user.getName())
                                                .send();

                                        return this.userRepository.findTopUsersBySpentTime(querySize);
                                    })
                                    .thenAcceptAsync(newTopUsers -> GuiProvider.openGui(GUI_IDENTIFIER, admin, newTopUsers))
                                    .exceptionally(throwable -> {
                                        this.messageService.send(admin, notice -> notice.playerTimeResetError);
                                        this.logger.log(Level.SEVERE, "An error occurred while trying to reset spent time", throwable);
                                        return null;
                                    });
                        })
                        .onCancel(player ->
                                this.userRepository.findTopUsersBySpentTime(querySize)
                                        .thenAcceptAsync(newTopUsers -> GuiProvider.openGui(GUI_IDENTIFIER, admin, newTopUsers))
                        )
                        .build()
        );
    }

    private boolean hasResetPermission(@NotNull Player player) {
        return player.hasPermission(this.getConfig().headItemPermissionReset);
    }


    private GuiConfiguration.SpentTimeTopGuiConfiguration getConfig() {
        return this.guiConfiguration.spentTimeTopGui;
    }

    @Override
    public @NotNull String getIdentifier() {
        return GUI_IDENTIFIER;
    }
}
