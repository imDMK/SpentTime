package com.github.imdmk.spenttime.command.editor;

import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import dev.rollczi.litecommands.factory.CommandEditor;

import java.util.List;

public class SpentTimeResetCommandEditor implements CommandEditor {

    private final PluginConfiguration pluginConfiguration;

    public SpentTimeResetCommandEditor(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    @Override
    public State edit(State state) {
        state.editChild("reset", s -> s.permission(List.of(this.pluginConfiguration.spentTimeResetTimeCommandPermission)));
        state.editChild("reset-all", s -> s.permission(List.of(this.pluginConfiguration.spentTimeResetTimeForAllCommandPermission)));

        return state;
    }
}
