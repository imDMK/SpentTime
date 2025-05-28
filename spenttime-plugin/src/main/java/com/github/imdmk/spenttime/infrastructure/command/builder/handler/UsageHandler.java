package com.github.imdmk.spenttime.infrastructure.command.builder.handler;

import com.github.imdmk.spenttime.infrastructure.message.MessageService;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UsageHandler implements InvalidUsageHandler<CommandSender> {

    private final MessageService messageService;

    public UsageHandler(@NotNull MessageService messageService) {
        this.messageService = Objects.requireNonNull(messageService, "message service cannot be null");
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {
        CommandSender sender = invocation.sender();
        Schematic schematic = result.getSchematic();

        if (schematic.isOnlyFirst()) {
            this.messageService.create()
                    .viewer(sender)
                    .notice(notice -> notice.invalidCommandUsage)
                    .placeholder("{USAGE}", schematic.first())
                    .send();
            return;
        }

        this.messageService.create()
                .viewer(sender)
                .notice(notice -> notice.usageHeader)
                .send();

        for (String scheme : schematic.all()) {
            this.messageService.create()
                    .viewer(sender)
                    .notice(notice -> notice.usageEntry)
                    .placeholder("{USAGE}", scheme)
                    .send();
        }
    }
}
