package com.github.imdmk.spenttime.user.feature.migration;

import com.github.imdmk.spenttime.infrastructure.message.MessageService;
import com.github.imdmk.spenttime.task.PluginTask;
import com.github.imdmk.spenttime.user.UserService;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A task that performs batched migration of user data for offline players.
 * It communicates progress via command sender and logs detailed output.
 * <p>
 * This task is intended to be scheduled periodically until the player queue is empty.
 */
public class MigrationTask implements PluginTask {

    private final CommandSender sender;
    private final Logger logger;
    private final MessageService messageService;
    private final MigrationManager migrationManager;
    private final UserService userService;
    private final int batchSize;

    private final Queue<OfflinePlayer> playerQueue;
    private int migratedCount;

    // Volatile to ensure visibility across threads
    private volatile boolean stopped = false;

    public MigrationTask(
            @NotNull CommandSender sender,
            @NotNull Logger logger,
            @NotNull MessageService messageService,
            @NotNull MigrationManager migrationManager,
            @NotNull UserService userService,
            @NotNull List<OfflinePlayer> playerQueue,
            int batchSize
    ) {
        this.sender = Objects.requireNonNull(sender, "sender cannot be null");
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.migrationManager = Objects.requireNonNull(migrationManager, "migrationManager cannot be null");
        this.userService = Objects.requireNonNull(userService, "userService cannot be null");
        this.batchSize = batchSize;

        this.playerQueue = new ConcurrentLinkedQueue<>(Objects.requireNonNull(playerQueue, "playerQueue cannot be null"));
        this.migratedCount = 0;
    }

    @Override
    public void run() {
        if (this.isStopped()) {
            this.finishMigration(this.sender);
            return;
        }

        this.logger.info("Migration task started.");
        this.messageService.create()
                .viewer(this.sender)
                .notice(notice -> notice.migrationStarted)
                .placeholder("{SIZE}", String.valueOf(this.playerQueue.size()))
                .send();

        int processedThisBatch = 0;
        while (processedThisBatch < this.batchSize) {
            OfflinePlayer player = this.playerQueue.poll();

            if (player == null) {
                this.finishMigration(this.sender);
                return;
            }

            this.migratePlayer(player);
            this.migratedCount++;

            processedThisBatch++;
        }

        this.reportProgress(this.sender);
    }

    /**
     * Migrates spent time data for a single offline player asynchronously.
     * Errors are logged.
     *
     * @param player the offline player to migrate; must not be null
     */
    private void migratePlayer(@NotNull OfflinePlayer player) {
        String playerName = player.getName() == null ? player.getUniqueId().toString() : player.getName();

        this.userService.findOrCreateUser(player.getUniqueId(), playerName)
                .thenAccept(user -> this.userService.updateUser(player, user))
                .exceptionally(throwable -> {
                    this.logger.log(Level.SEVERE, "Failed to migrate player: " + player.getName(), throwable);
                    return null;
                });
    }

    /**
     * Sends progress information to the {@link CommandSender} and logs it.
     *
     * @param sender the command sender to notify; must not be null
     */
    private void reportProgress(@NotNull CommandSender sender) {
        int remaining = this.playerQueue.size();

        this.messageService.create()
                .viewer(sender)
                .notice(notice -> notice.migrationInProgress)
                .placeholder("{SIZE}", String.valueOf(this.migratedCount))
                .placeholder("{REMAINING}", String.valueOf(remaining))
                .send();

        this.logger.info(String.format("Migrated %d players, %d remaining", this.migratedCount, remaining));
    }

    /**
     * Sends a migration completion message to the {@link CommandSender} and logs it.
     *
     * @param sender the command sender to notify; must not be null
     */
    private void finishMigration(@NotNull CommandSender sender) {
        this.messageService.create()
                .viewer(sender)
                .notice(notice -> notice.migrationCompleted)
                .placeholder("{SIZE}", String.valueOf(this.migratedCount))
                .send();

        this.logger.info("Migration complete.");
        this.stop();
    }

    @Override
    public boolean isStopped() {
        return this.stopped;
    }

    @Override
    public void stop() {
        if (this.isStopped()) {
            return;
        }

        this.stopped = true;
        this.migrationManager.cancel();
    }
}
