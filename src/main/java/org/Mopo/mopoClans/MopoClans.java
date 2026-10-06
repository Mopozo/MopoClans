package org.Mopo.mopoClans;

import mopo.mopoLib.inventories.BufferManager;
import mopo.mopoLib.yml.MopoYaml;
import org.Mopo.mopoClans.clandata.ClanManager;
import org.Mopo.mopoClans.commands.ClanCMD;
import org.Mopo.mopoClans.inbox.InboxManager;
import org.Mopo.mopoClans.listeners.ClanListener;
import org.Mopo.mopoClans.listeners.InboxListener;
import org.Mopo.mopoClans.listeners.PlayerDataListener;
import org.Mopo.mopoClans.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MopoClans extends JavaPlugin {
    private static MopoClans INSTANCE; //todo no caps. only caps for final
    private static BufferManager BUFFERMANAGER; //todo none of these should be static. use getters, also no caps
    private static PlayerDataManager PLAYERDATAMANAGER;
    private static InboxManager INBOXMANAGER;
    private static ClanManager CLANMANAGER;
    private static File FOLDER;
    private static File CONFIG_FILE;
    private static MopoYaml configYML;
    private static MopoYaml MOPO_YAML;
    private static File CLAN_FOLDER;
    private static File PLAYER_FOLDER; // should be handled in the player class, same with others ect
    private static MopoYaml PLAYER_YAML;
    private static MopoYaml CLAN_YAML;
    private static File INBOX_FOLDER;
    private static MopoYaml INBOX_YML;




    public static MopoYaml getPlayerYaml() {
        return PLAYER_YAML;
    }

    public static MopoYaml getClanYaml() {
        return CLAN_YAML;
    }

    public static File getClanFolder() {
        return ensureFolder(CLAN_FOLDER);
    }
    public static File getInboxFolder() {
        return ensureFolder(INBOX_FOLDER);
    }
    public static File getPlayerFolder() {
        return ensureFolder(PLAYER_FOLDER);
    }

    private static File ensureFolder(File folder) {
        if (folder == null) {
            throw new IllegalStateException("Plugin data folders have not been initialized");
        }
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException("Could not create plugin data folder: " + folder);
        }
        return folder;
    }

    public static PlayerDataManager getPLAYERDATAMANAGER() { // shouldnt all be upper case
        return PLAYERDATAMANAGER;
    }

    public static ClanManager getCLANMANAGER() {
        return CLANMANAGER;
    }

    public static InboxManager getINBOXMANAGER() {
        return INBOXMANAGER;
    }



    @Override
    public void onEnable() {
        INSTANCE = this;
        FOLDER = getDataFolder();
        ensureFolder(FOLDER);
        CONFIG_FILE = new File(FOLDER, "config.yml");
        CLAN_FOLDER = new File(FOLDER, "clans");
        PLAYER_FOLDER = new File(FOLDER, "players");
        INBOX_FOLDER = new File(FOLDER, "inboxes");
        ensureFolder(CLAN_FOLDER);
        ensureFolder(PLAYER_FOLDER);
        ensureFolder(INBOX_FOLDER);

        INBOXMANAGER = new InboxManager();
        BUFFERMANAGER = new BufferManager();
        saveResource("quest.yml", false);
        MOPO_YAML = new MopoYaml();
        configYML = MopoYaml.loadFromFile(CONFIG_FILE, true);
        PLAYER_YAML = new MopoYaml();
        CLAN_YAML = new MopoYaml();
        INBOX_YML = new MopoYaml();
        PLAYERDATAMANAGER = new PlayerDataManager();
        CLANMANAGER = new ClanManager();
        CLANMANAGER.loadClanMap();

        PluginManager pm = getServer().getPluginManager();
        Bukkit.getPluginCommand("clan").setExecutor(new ClanCMD());

        pm.registerEvents(new InboxListener(), this);
        pm.registerEvents(new ClanListener(), this);
        pm.registerEvents(new PlayerDataListener(), this);
    }

    @Override
    public void onDisable() {
        if (PLAYERDATAMANAGER != null) {
            PLAYERDATAMANAGER.saveAllPlayerData();
        }
        if (CLANMANAGER != null) {
            CLANMANAGER.saveAll();
        }
        if (configYML != null) {
            configYML.saveSync(CONFIG_FILE);
        }

    }

    public static BufferManager getBUFFERMANAGER() {
        return BUFFERMANAGER;
    }

    public static MopoClans getINSTANCE() {
        return INSTANCE;
    }

    public static File getFolder() {
        return FOLDER;
    }

    public static MopoYaml getMopoYaml() {
        return MOPO_YAML;
    }

    public static MopoYaml getConfigYML() {
        return configYML;
    }
}
