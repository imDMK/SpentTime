package com.github.imdmk.spenttime.command.settings;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class CommandSettings extends OkaeriConfig {

    @Comment("# \"spenttime reset\" command permissions")
    public String spentTimeResetPermission = "spenttime.resettime";

    @Comment("# \"spenttime reset-all\" command permissions")
    public String spentTimeResetAllPermission = "spenttime.resettimeforall";
}
