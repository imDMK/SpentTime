package com.github.imdmk.spenttime.command.implementation.editor;

import com.github.imdmk.spenttime.command.settings.CommandSettings;
import dev.rollczi.litecommands.factory.CommandEditor;

import java.util.List;

public class SpentTimeResetCommandEditor implements CommandEditor {

    private final CommandSettings commandSettings;

    public SpentTimeResetCommandEditor(CommandSettings commandSettings) {
        this.commandSettings = commandSettings;
    }

    @Override
    public State edit(State state) {
        state.editChild("reset", s -> s.permission(List.of(this.commandSettings.spentTimeResetPermission)));
        state.editChild("reset-all", s -> s.permission(List.of(this.commandSettings.spentTimeResetAllPermission)));

        return state;
    }
}
