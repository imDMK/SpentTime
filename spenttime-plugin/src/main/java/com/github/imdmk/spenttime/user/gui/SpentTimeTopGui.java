package com.github.imdmk.spenttime.user.gui;

import com.github.imdmk.spenttime.feature.gui.AbstractGui;
import com.github.imdmk.spenttime.feature.gui.GuiManager;
import com.github.imdmk.spenttime.feature.gui.ParameterizedGui;
import com.github.imdmk.spenttime.feature.gui.configuration.GuiConfiguration;
import com.github.imdmk.spenttime.feature.gui.configuration.item.ItemGui;
import com.github.imdmk.spenttime.feature.gui.configuration.item.ItemGuiConfiguration;
import com.github.imdmk.spenttime.feature.gui.implementation.ConfirmationGui;
import com.github.imdmk.spenttime.feature.gui.implementation.ConfirmationGuiAction;
import com.github.imdmk.spenttime.feature.message.MessageService;
import com.github.imdmk.spenttime.shared.Formatter;
import com.github.imdmk.spenttime.task.TaskScheduler;
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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpentTimeTopGui extends AbstractGui implements ParameterizedGui<List<User>> {

    public static final String GUI_IDENTIFIER = "spenttimetop";

    private final Logger logger;
    private final Server server;
    private final GuiConfiguration guiConfiguration;
    private final ItemGuiConfiguration itemConfiguration;
    private final UserService userService;
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final GuiManager guiManager;

    public SpentTimeTopGui(
            @NotNull Logger logger,
            @NotNull Server server,
            @NotNull GuiConfiguration guiConfiguration,
            @NotNull ItemGuiConfiguration itemConfiguration,
            @NotNull UserService userService,
            @NotNull UserRepository userRepository,
            @NotNull MessageService messageService,
            @NotNull GuiManager guiManager,
            @NotNull TaskScheduler taskScheduler
    ) {
        super(itemConfiguration, taskScheduler);
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.server = Objects.requireNonNull(server, "server cannot be null");
        this.guiConfiguration = Objects.requireNonNull(guiConfiguration, "guiConfiguration cannot be null");
        this.itemConfiguration = Objects.requireNonNull(itemConfiguration, "guiItemConfiguration cannot be null");
        this.userService = Objects.requireNonNull(userService, "userService cannot be null");
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository cannot be null");
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.guiManager = Objects.requireNonNull(guiManager, "guiManager cannot be null");
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

    @Override
    public void defaultClickAction(@NotNull BaseGui gui, @NotNull Player viewer) {
        this.playSoundIfEnabled(gui, viewer, this.guiConfiguration.sound);
    }

    private Formatter createFormatter(User user, int position) {
        return new Formatter()
                .placeholder("{PLAYER}", user.getName())
                .placeholder("{POSITION}", position)
                .placeholder("{TIME}", DurationUtil.format(user.getSpentTimeAsDuration()))
                .placeholder("{CLICK_REFRESH}", this.getConfig().headItemClickRefresh.name())
                .placeholder("{CLICK_RESET}", this.getConfig().headItemClickReset.name());
    }

    private @NotNull GuiItem createHeadItem(@NotNull Player viewer, @NotNull User user, @NotNull Formatter formatter, int querySize) {
        ItemGui headItem = this.getConfig().headItem.toBuilder()
                .loreComponent(this.hasResetPermission(viewer) ? this.getConfig().headItemAdminLore : this.getConfig().headItem.lore())
                .build();

        return ItemBuilder.skull()
                .owner(this.server.getOfflinePlayer(user.getUuid()))
                .name(formatter.format(headItem.name()))
                .lore(formatter.format(headItem.lore()))
                .enchant(headItem.enchantments())
                .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                .asGuiItem(event -> {
                    ClickType type = event.getClick();

                    if (!this.hasResetPermission(viewer)) {
                        return;
                    }

                    if (type == this.getConfig().headItemClickRefresh) {
                        this.forceRefreshSpentTime(viewer, user, querySize);
                    }
                    else if (type == this.getConfig().headItemClickReset) {
                        this.openResetConfirmGui(viewer, user, querySize);
                    }
                });
    }

    private void forceRefreshSpentTime(@NotNull Player admin, @NotNull User target, int querySize) {
        boolean updated = this.userService.updateUser(admin, target);

        if (updated) {
            this.userRepository.findTopUsersBySpentTime(querySize)
                    .thenAccept(newTopUsers -> this.guiManager.openGui(GUI_IDENTIFIER, admin, newTopUsers));
        }
    }

    private void openResetConfirmGui(@NotNull Player admin, @NotNull User target, int querySize) {
        this.guiManager.openGui(
                ConfirmationGui.GUI_IDENTIFIER,
                admin,
                ConfirmationGuiAction.builder()
                        .onConfirm(player -> {
                            this.userService.setSpentTime(target, Duration.ZERO);

                            this.userService.saveUser(target)
                                    .thenCompose(user -> {
                                        this.messageService.create()
                                                .notice(notice -> notice.playerTimeReset)
                                                .placeholder("{PLAYER}", user.getName())
                                                .send();

                                        return this.userRepository.findTopUsersBySpentTime(querySize);
                                    })
                                    .thenAccept(newTopUsers -> this.guiManager.openGui(GUI_IDENTIFIER, admin, newTopUsers))
                                    .exceptionally(throwable -> {
                                        this.messageService.send(admin, notice -> notice.playerTimeResetError);
                                        this.logger.log(Level.SEVERE, "An error occurred while trying to reset spent time", throwable);
                                        return null;
                                    });
                        })
                        .onCancel(player ->
                                this.userRepository.findTopUsersBySpentTime(querySize)
                                        .thenAccept(newTopUsers -> this.guiManager.openGui(GUI_IDENTIFIER, admin, newTopUsers))
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
