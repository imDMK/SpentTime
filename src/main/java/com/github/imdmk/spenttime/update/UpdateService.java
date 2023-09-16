package com.github.imdmk.spenttime.update;

import com.eternalcode.gitcheck.GitCheck;
import com.eternalcode.gitcheck.GitCheckResult;
import com.eternalcode.gitcheck.git.GitRepository;
import com.eternalcode.gitcheck.git.GitTag;
import org.bukkit.plugin.PluginDescriptionFile;

public class UpdateService {

    private static final GitRepository GIT_REPOSITORY = GitRepository.of("imDMK", "SpentTime");

    private final GitCheck gitCheck;
    private final PluginDescriptionFile pluginDescriptionFile;

    public UpdateService(PluginDescriptionFile pluginDescriptionFile) {
        this.gitCheck = new GitCheck();
        this.pluginDescriptionFile = pluginDescriptionFile;
    }

    public GitCheckResult check() {
        GitTag gitTag = GitTag.of("v" + this.pluginDescriptionFile.getVersion());

        return this.gitCheck.checkRelease(GIT_REPOSITORY, gitTag);
    }
}
