package com.github.imdmk.spenttime.command.editor;

import com.github.imdmk.spenttime.command.configuration.CommandConfiguration;
import dev.rollczi.litecommands.factory.CommandEditor;

public class SpentTimeCommandEditor implements CommandEditor {

    private final CommandConfiguration commandConfiguration;

    public SpentTimeCommandEditor(CommandConfiguration commandConfiguration) {
        this.commandConfiguration = commandConfiguration;
    }

    @Override
    public State edit(State state) {
        state.aliases(this.commandConfiguration.spentTimeAliases);

        return state;
    }
}
