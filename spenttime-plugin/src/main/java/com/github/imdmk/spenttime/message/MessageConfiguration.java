package com.github.imdmk.spenttime.message;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.spenttime.configuration.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;

public class MessageConfiguration extends ConfigSection {

    @Comment("# {TIME} - The spent player time")
    public Notice playerSpentTime = Notice.chat("<gray>You have spent <red>{TIME} <gray>on the server<dark_gray>.");

    @Comment({
            "# {PLAYER} - Target name",
            "# {TIME} - Spent target time"
    })
    public Notice targetSpentTime = Notice.chat("<gray>Player <green>{PLAYER} has spent <red>{TIME} on the server<dark_gray>.");

    public Notice topSpentTimeIsEmpty = Notice.chat("<red>The top spent time is empty<dark_gray>.");
    public Notice topSpentTimeQueryError = Notice.chat("<red>An error occurred while querying top spent time<dark_gray>.");

    @Comment("# Used when top spent time GUI is disabled")
    public Notice topSpentTimeListFirst = Notice.chat("<green>Top 10 players who spent the most time on the server<dark_gray>:");

    @Comment({
            "# Used when top spent time GUI is disabled",
            "# {POSITION} - The player position",
            "# PLAYER} - The player name",
            "# {TIME} - The spent player time"
    })
    public Notice topSpentTimeList = Notice.chat("<dark_gray>- <green>{POSITION} Player {PLAYER}<dark_gray>: <red>{TIME}");

    @Comment("# {PLAYER} - The player name")
    public Notice targetSpentTimeHasBeenReset = Notice.chat("<green>The player {PLAYER} spent time on the server has been reset<dark_gray>.");
    public Notice targetSpentTimeResetError = Notice.chat("<red>An error occurred while trying to reset player spent time on the server<dark_gray>.");

    public Notice globalSpentTimeHasBeenReset = Notice.chat("<green>Time spent on the server has been reset for all users<dark_gray>.");
    public Notice globalSpentTimeResetError = Notice.chat("<red>An error occurred while trying to reset spent time on the server<dark_gray>.");

    @Comment({
            "# {PLAYER} - The player name",
            "# {TIME} - New spent player time"
    })
    public Notice targetSpentTimeHasBeenSet = Notice.chat("<green>The player {PLAYER} spent time has been set to {TIME}<dark_gray>.");
    public Notice targetSpentTimeSetError = Notice.chat("<red>An error occurred while trying to set player spent time on the server<dark_gray>.");

    @Comment("# {PERMISSIONS} - Required permissions")
    public Notice missingPermissions = Notice.chat("<red>Missing permissions: <dark_red>{PERMISSIONS}<dark_gray>.");

    public Notice playerNotFound = Notice.chat("<red>Player not found<dark_gray>.");

    @Comment("# {USAGE} - Correct usage of command")
    public Notice invalidUsage = Notice.chat("<red>Invalid usage: <dark_red>{USAGE}<dark_gray>.");

    @Comment("# Used when there is more than one option to use a command")
    public Notice invalidUsageFirst = Notice.chat("<red>Invalid usage:");

    @Comment({
            "# {USAGE} - Correct usage of command",
            "# Used when there is more than one option to use a command"
    })
    public Notice invalidUsageList = Notice.chat("<dark_gray>- <red>{USAGE}");

    @Override
    public OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new MultificationSerdesPack(NoticeResolverDefaults.createRegistry()));
        };
    }

    @Override
    public String getFileName() {
        return "messageConfiguration.yml";
    }
}
