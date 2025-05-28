package com.github.imdmk.spenttime;

import org.bukkit.plugin.java.JavaPlugin;

public class SpentTimePlugin extends JavaPlugin {

    /** bStats Metrics service ID for reporting plugin statistics */
    public static final int METRICS_SERVICE_ID = 19362;

    private SpentTime spentTime;

    @Override
    public void onEnable() {
        this.spentTime = new SpentTime(this);
    }

    @Override
    public void onDisable() {
        this.spentTime.disable();
    }
}
