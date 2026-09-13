package org.Mopo.mopoClans;

import mopo.mopoLib.inventories.BufferManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MopoClans extends JavaPlugin {
    private static MopoClans INSTANCE;
    private static BufferManager BUFFERMANAGER;



    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        BUFFERMANAGER = new BufferManager();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BufferManager getBUFFERMANAGER() {
        return BUFFERMANAGER;
    }

    public static MopoClans getINSTANCE() {
        return INSTANCE;
    }
}
