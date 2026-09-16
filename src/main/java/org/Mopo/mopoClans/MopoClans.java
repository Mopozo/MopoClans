package org.Mopo.mopoClans;

import mopo.mopoLib.MopoLib;
import mopo.mopoLib.inventories.BufferManager;
import mopo.mopoLib.yml.MopoYaml;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MopoClans extends JavaPlugin {
    private static MopoClans INSTANCE;
    private static BufferManager BUFFERMANAGER;
    private static File FOLDER;
    private static File CONFIG_FILE;
    private static MopoYaml configYML;
    private static MopoYaml MOPO_YAML;



    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        BUFFERMANAGER = new BufferManager();
        if (!FOLDER.exists()) {
            FOLDER.mkdir();
        }
        if (!CONFIG_FILE.exists()) {
            CONFIG_FILE = new File(FOLDER, "config.yml");
        }
        MOPO_YAML = new MopoYaml();
        configYML = MopoYaml.loadFromFile(CONFIG_FILE, true);

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

    public static File getFolder() {
        if (FOLDER != null) {
            return FOLDER;
        } else {
            FOLDER.mkdir();
        }
        return null;
    }

    public static MopoYaml getMopoYaml() {
        return MOPO_YAML;
    }

    public static MopoYaml getConfigYML() {
        return configYML;
    }

}
