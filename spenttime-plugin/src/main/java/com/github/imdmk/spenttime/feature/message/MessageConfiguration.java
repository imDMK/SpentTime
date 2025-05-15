package com.github.imdmk.spenttime.feature.message;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.spenttime.configuration.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

public class MessageConfiguration extends ConfigSection {

    @Comment("# Sent when successfully reloaded all plugin configuration files")
    public Notice reload = Notice.chat("<green>The plugin configuration files has been reloaded. May note that not all functions are reloaded.");

    @Comment("# Sent when there an error occurred while trying to load plugin configuration file")
    public Notice reloadError = Notice.chat("<red>Failed to reload plugin configuration files. Please see the console.");

    @Comment({
            "# Sent to the player with their own spent time",
            "# {TIME} - The spent player time"
    })
    public Notice ownSpentTime = Notice.chat("<gray>You have spent <red>{TIME} <gray>on the server<dark_gray>.");

    @Comment({
            "# Sent to the player with another player's spent time",
            "# {PLAYER} - Target player name",
            "# {TIME} - Target spent time"
    })
    public Notice otherPlayerSpentTime = Notice.chat("<gray>Player <red>{PLAYER} <gray>has spent <green>{TIME} <gray>on the server<dark_gray>.");

    @Comment("# Sent when there are no top players to display")
    public Notice topListEmpty = Notice.chat("<red>The top spent time is empty<dark_gray>.");

    @Comment("# Sent when querying top list fails")
    public Notice topListQueryError = Notice.chat("<red>An error occurred while querying top spent time<dark_gray>.");

    @Comment({
            "# Sent when a specific player's time has been reset",
            "# {PLAYER} - The player name"
    })
    public Notice playerTimeReset = Notice.chat("<green>The player {PLAYER} spent time on the server has been reset<dark_gray>.");

    @Comment("# Sent when resetting a specific player's time fails")
    public Notice playerTimeResetError = Notice.chat("<red>An error occurred while trying to reset player spent time on the server<dark_gray>.");

    @Comment("# Sent when all users' spent time has been reset")
    public Notice globalTimeReset = Notice.chat("<green>Time spent on the server has been reset for all users<dark_gray>.");

    @Comment("# Sent when global reset fails")
    public Notice globalTimeResetError = Notice.chat("<red>An error occurred while trying to reset spent time on the server<dark_gray>.");

    @Comment({
            "# Sent when a player's time has been set to a new value",
            "# {PLAYER} - The player name",
            "# {TIME} - New spent player time"
    })
    public Notice playerTimeSet = Notice.chat("<green>The player {PLAYER} spent time has been set to {TIME}<dark_gray>.");

    @Comment("# Sent when setting a player's time fails")
    public Notice playerTimeSetError = Notice.chat("<red>An error occurred while trying to set player spent time on the server<dark_gray>.");

    @Comment({
            "# Sent when a command is used without required permissions",
            "# {PERMISSIONS} - Required permission nodes"
    })
    public Notice noPermission = Notice.chat("<red>Missing permissions: <dark_red>{PERMISSIONS}<dark_gray>.");

    @Comment("# Sent when player was not found by name or UUID")
    public Notice playerNotFound = Notice.chat("<red>Player not found<dark_gray>.");

    @Comment({
            "# Sent when player uses command incorrectly",
            "# {USAGE} - Correct command usage"
    })
    public Notice invalidCommandUsage = Notice.chat("<red>Invalid usage: <dark_red>{USAGE}<dark_gray>.");

    @Comment("# Header for multiple command usages")
    public Notice usageHeader = Notice.chat("<red>Invalid usage:");

    @Comment({
            "# Each entry for valid usages when there are multiple possibilities",
            "# {USAGE} - Correct command usage"
    })
    public Notice usageEntry = Notice.chat("<dark_gray>- <red>{USAGE}");

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new MultificationSerdesPack(NoticeResolverDefaults.createRegistry()));
        };
    }

    @Override
    public @NotNull String getFileName() {
        return "messageConfiguration.yml";
    }
}
