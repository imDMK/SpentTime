package com.github.imdmk.spenttime.update;

import com.eternalcode.gitcheck.GitCheck;
import com.eternalcode.gitcheck.GitCheckResult;
import com.eternalcode.gitcheck.git.GitException;
import com.eternalcode.gitcheck.git.GitRelease;
import com.eternalcode.gitcheck.git.GitRepository;
import com.eternalcode.gitcheck.git.GitTag;
import com.github.imdmk.spenttime.util.AnsiColor;

import java.util.logging.Logger;

public class UpdateService {

    private static final GitRepository GIT_REPOSITORY = GitRepository.of("imDMK", "SpentTime");

    private final String version;
    private final Logger logger;

    public UpdateService(String version, Logger logger) {
        this.version = version;
        this.logger = logger;
    }

    public void check() throws GitException {
        GitCheck gitCheck = new GitCheck();

        GitTag gitTag = GitTag.of("v" + this.version);
        GitCheckResult checkResult = gitCheck.checkRelease(GIT_REPOSITORY, gitTag);

        if (checkResult.isUpToDate()) {
            this.logger.info(AnsiColor.GREEN + "You are using latest version. Thank you." + AnsiColor.RESET);
        }
        else {
            GitRelease latestRelease = checkResult.getLatestRelease();

            this.logger.info(AnsiColor.YELLOW + "A new version is available: " + latestRelease.getTag() + AnsiColor.RESET);
            this.logger.info(AnsiColor.YELLOW + "Download it here: " + latestRelease.getPageUrl() + AnsiColor.RESET);
        }
    }
}
