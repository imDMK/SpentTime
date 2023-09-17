package com.github.imdmk.spenttime.command.editor;

import com.github.imdmk.spenttime.command.configuration.CommandConfiguration;
import dev.rollczi.litecommands.factory.CommandEditor;

import java.util.List;

public class SpentTimeResetCommandEditor implements CommandEditor {

    private final CommandConfiguration commandConfiguration;

    public SpentTimeResetCommandEditor(CommandConfiguration commandConfiguration) {
        this.commandConfiguration = commandConfiguration;
    }

    @Override
    public State edit(State state) {
        state.editChild("reset", s -> s.permission(List.of(this.commandConfiguration.spentTimeResetPermission)));
        state.editChild("reset-all", s -> s.permission(List.of(this.commandConfiguration.spentTimeResetAllPermission)));

        return state;
    }
}
