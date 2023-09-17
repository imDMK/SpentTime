package com.github.imdmk.spenttime.command.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

import java.util.List;

public class CommandConfiguration extends OkaeriConfig {

    @Comment("# \"spenttime\" command aliases")
    public List<String> spentTimeAliases = List.of("dj");

    @Comment("# \"spenttime reset\" command permissions")
    public String spentTimeResetPermission = "spenttime.resettime";

    @Comment("# \"spenttime reset-all\" command permissions")
    public String spentTimeResetAllPermission = "spenttime.resettimeforall";
}
