package com.github.imdmk.spenttime.command.editor;

import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import dev.rollczi.litecommands.factory.CommandEditor;

import java.util.List;

public class SpentTimeCommandEditor implements CommandEditor {

    private final PluginConfiguration pluginConfiguration;

    public SpentTimeCommandEditor(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    @Override
    public State edit(State state) {
        state.editChild("reset-time", s -> s.permission(List.of(this.pluginConfiguration.spentTimeResetTimeCommandPermission)));
        state.editChild("reset-time-for-all", s -> s.permission(List.of(this.pluginConfiguration.spentTimeResetTimeForAllCommandPermission)));

        return state;
    }
}
