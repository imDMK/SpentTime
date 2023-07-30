package com.github.imdmk.spenttime;

import org.bukkit.plugin.java.JavaPlugin;

public class SpentTimePlugin extends JavaPlugin {

    private SpentTime spentTime;

    @Override
    public void onEnable() {
        this.spentTime = new SpentTime(this);
    }

    @Override
    public void onDisable() {
        this.spentTime.onDisable();
    }
}
