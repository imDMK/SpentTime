package com.github.imdmk.spenttime.infrastructure.command.configurator;

import com.github.imdmk.spenttime.configuration.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Header({
        "#",
        "# This file allows you to configure commands.",
        "#"
})
public class CommandConfiguration extends ConfigSection {

    @Comment("# Enable the command configurator?")
    public boolean enabled = false;

    @Comment({
            "# This allows you to globally edit commands.",
            "# For example, if you want to change the command name from /my-furnaces to /furnaces,",
            "# you can configure it like this:",
            "#",
            "# commands:",
            "#   <command_name>:",
            "#     name: \"<new_command_name>\"",
            "#     enabled: true/false",
            "#     aliases:",
            "#       - \"<new_command_aliases>\"",
            "#     permissions:",
            "#       - \"<new_command_permission>\"",
            "#     subCommands:",
            "#       <default_sub_command_name>:",
            "#         name: <new_sub_command_name>",
            "#         enabled: true/false",
            "#         aliases:",
            "#           - \"<new_sub_command_aliases>\"",
            "#         permissions:",
            "#           - \"<new_sub_command_permission>\"",
    })
    public Map<String, Command> commands = Map.of(
            "spenttime", new Command(
                    "spenttime",
                    true,
                    List.of("st"),
                    List.of(),
                    Map.of()
            )
    );

    public Optional<Command> getCommand(String name) {
        return Optional.ofNullable(this.commands.get(name));
    }

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {};
    }

    @Override
    public @NotNull String getFileName() {
        return "commandConfiguration.yml";
    }
}
