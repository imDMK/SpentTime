package com.github.imdmk.spenttime.feature.update;

import com.eternalcode.gitcheck.GitCheck;
import com.eternalcode.gitcheck.GitCheckResult;
import com.eternalcode.gitcheck.git.GitRepository;
import com.eternalcode.gitcheck.git.GitTag;
import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Objects;

public class UpdateService {

    private static final GitRepository GIT_REPOSITORY = GitRepository.of("imDMK", "SpentTime");
    private static final GitCheck GIT_CHECK = new GitCheck();

    private final PluginConfiguration pluginConfiguration;
    private final PluginDescriptionFile pluginDescriptionFile;

    private Instant latestCheck;

    public UpdateService(@NotNull PluginConfiguration pluginConfiguration, @NotNull PluginDescriptionFile descriptionFile) {
        this.pluginConfiguration = Objects.requireNonNull(pluginConfiguration, "pluginConfiguration cannot be null");
        this.pluginDescriptionFile = Objects.requireNonNull(descriptionFile, "pluginDescriptionFile cannot be null");
    }

    public @NotNull GitCheckResult check() {
        this.latestCheck = Instant.now();

        GitTag tag = GitTag.of("v" + this.pluginDescriptionFile.getVersion());
        return GIT_CHECK.checkRelease(GIT_REPOSITORY, tag);
    }

    public boolean shouldCheck() {
        if (this.latestCheck == null) {
            return true;
        }

        Instant nextCheckTime = this.latestCheck.plus(this.pluginConfiguration.updateInterval);
        return Instant.now().isAfter(nextCheckTime);
    }
}
