package org.Mopo.mopoClans;

import mopo.mopoLib.MopoLib;
import mopo.mopoLib.inventories.BufferManager;
import mopo.mopoLib.yml.MopoYaml;
import org.Mopo.mopoClans.ClanData.Clan;
import org.Mopo.mopoClans.ClanData.ClanManager;
import org.Mopo.mopoClans.Commands.ClanCMD;
import org.Mopo.mopoClans.Inbox.InboxManager;
import org.Mopo.mopoClans.Listeners.ClanListener;
import org.Mopo.mopoClans.Listeners.InboxListener;
import org.Mopo.mopoClans.PlayerData.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MopoClans extends JavaPlugin {
    private static MopoClans INSTANCE;
    private static BufferManager BUFFERMANAGER;
    private static PlayerDataManager PLAYERDATAMANAGER;
    private static InboxManager INBOXMANAGER;
    private static ClanManager CLANMANAGER;
    private static File FOLDER;
    private static File CONFIG_FILE;
    private static MopoYaml configYML;
    private static MopoYaml MOPO_YAML;
    private static File CLAN_FOLDER;
    private static File PLAYER_FOLDER;
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
        if (!CLAN_FOLDER.exists()) {
            CLAN_FOLDER.mkdir();
            return CLAN_FOLDER;
        }
        return CLAN_FOLDER;
    }
    public static File getInboxFolder() {
        if (!INBOX_FOLDER.exists()) {
            INBOX_FOLDER.mkdir();
            return INBOX_FOLDER;
        }
        return INBOX_FOLDER;
    }
    public static File getPlayerFolder() {
        if (!PLAYER_FOLDER.exists()) {
            PLAYER_FOLDER.mkdir();
            return PLAYER_FOLDER;
        }
        return PLAYER_FOLDER;
    }

    public static PlayerDataManager getPLAYERDATAMANAGER() {
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
        // Plugin startup logic
        INSTANCE = this;
        INBOXMANAGER = new InboxManager();
        BUFFERMANAGER = new BufferManager();
        if (!FOLDER.exists()) {
            FOLDER.mkdir();
        }


        if (!CONFIG_FILE.exists()) {
            CONFIG_FILE = new File(FOLDER, "config.yml");
        }

        MOPO_YAML = new MopoYaml();
        configYML = MopoYaml.loadFromFile(CONFIG_FILE, true);
        PLAYERDATAMANAGER = new PlayerDataManager();
        CLANMANAGER = new ClanManager();

        PluginManager pm = getServer().getPluginManager();
        Bukkit.getPluginCommand("clan").setExecutor(new ClanCMD());

        pm.registerEvents(new InboxListener(), MopoClans.getINSTANCE());
        pm.registerEvents(new ClanListener(), MopoClans.getINSTANCE());
    }

    @Override
    public void onDisable() {

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
