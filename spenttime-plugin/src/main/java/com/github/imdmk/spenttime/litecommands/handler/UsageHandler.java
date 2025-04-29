package com.github.imdmk.spenttime.litecommands.handler;

import com.github.imdmk.spenttime.message.MessageService;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;

public class UsageHandler implements InvalidUsageHandler<CommandSender> {

    private final MessageService messageService;

    public UsageHandler(MessageService messageService) {
        this.messageService = messageService;
    }


    @Override
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {
        CommandSender sender = invocation.sender();
        Schematic schematic = result.getSchematic();

        if (schematic.isOnlyFirst()) {
            this.messageService.create()
                    .viewer(sender)
                    .notice(notice -> notice.invalidUsage)
                    .placeholder("{USAGE}", schematic.first())
                    .send();
            return;
        }

        this.messageService.create()
                .viewer(sender)
                .notice(notice -> notice.invalidUsageFirst)
                .send();

        for (String scheme : schematic.all()) {
            this.messageService.create()
                    .viewer(sender)
                    .notice(notice -> notice.invalidUsageList)
                    .placeholder("{USAGE}", scheme)
                    .send();
        }
    }
}
