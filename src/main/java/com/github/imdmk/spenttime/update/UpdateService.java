package com.github.imdmk.spenttime.update;

import com.eternalcode.gitcheck.GitCheck;
import com.eternalcode.gitcheck.GitCheckResult;
import com.eternalcode.gitcheck.git.GitException;
import com.eternalcode.gitcheck.git.GitRelease;
import com.eternalcode.gitcheck.git.GitRepository;
import com.eternalcode.gitcheck.git.GitTag;
import com.github.imdmk.spenttime.util.AnsiColor;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateService {

    private static final GitRepository GIT_REPOSITORY = GitRepository.of("imDMK", "SpentTime");

    private final GitCheck gitCheck;
    private final PluginDescriptionFile pluginDescriptionFile;
    private final Logger logger;

    public UpdateService(PluginDescriptionFile pluginDescriptionFile, Logger logger) {
        this.gitCheck = new GitCheck();
        this.pluginDescriptionFile = pluginDescriptionFile;
        this.logger = logger;
    }

    public void check() {
        this.logger.info("Checking for update...");

        try {
            GitTag gitTag = GitTag.of("v" + this.pluginDescriptionFile.getVersion());
            GitCheckResult checkResult = this.gitCheck.checkRelease(GIT_REPOSITORY, gitTag);

            if (checkResult.isUpToDate()) {
                this.logger.info(AnsiColor.GREEN + "You are using latest version. Thank you." + AnsiColor.RESET);
            }
            else {
                GitRelease latestRelease = checkResult.getLatestRelease();

                this.logger.info(AnsiColor.YELLOW + "A new version is available: " + latestRelease.getTag() + AnsiColor.RESET);
                this.logger.info(AnsiColor.YELLOW + "Download it here: " + latestRelease.getPageUrl() + AnsiColor.RESET);
            }
        }
        catch (GitException gitException) {
            this.logger.log(Level.SEVERE, AnsiColor.RED + "An error occurred while checking for update: " + gitException.getMessage() + AnsiColor.RESET);
        }
    }
}
