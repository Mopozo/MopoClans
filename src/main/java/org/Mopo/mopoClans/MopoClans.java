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
import java.util.Map;

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
        MOPO_YAML = new MopoYaml();
        configYML = MopoYaml.loadFromFile(CONFIG_FILE, true);
        initializeConfig();
        PLAYER_YAML = new MopoYaml();
        CLAN_YAML = new MopoYaml();
        INBOX_YML = new MopoYaml();
        PLAYERDATAMANAGER = new PlayerDataManager();
        CLANMANAGER = new ClanManager();
        CLANMANAGER.loadClanMap();

        PluginManager pm = getServer().getPluginManager();
        ClanCMD clanCommand = new ClanCMD();
        Bukkit.getPluginCommand("clan").setExecutor(clanCommand);
        Bukkit.getPluginCommand("clan").setTabCompleter(clanCommand);

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

    private static void initializeConfig() {
        Map<String, Object> defaults = Map.ofEntries(
                Map.entry("max_clan_members", configYML.getInt("Clan.MaxClanSize",
                        configYML.getInt("MaxClanSize", 4))),
                Map.entry("gui.main.title", "Clan Menu"),
                Map.entry("gui.main.close.material", "BARRIER"),
                Map.entry("gui.main.close.name", "<gray>Close"),
                Map.entry("gui.main.create.material", "EMERALD"),
                Map.entry("gui.main.create.name", "<green>Create a Clan"),
                Map.entry("gui.main.invitations.material", "BOOK"),
                Map.entry("gui.main.invitations.name", "<aqua>Clan Invitations"),
                Map.entry("gui.main.clan_info.material", "WHITE_BANNER"),
                Map.entry("gui.main.clan_info.name", "<gold>{clan} <gray>[{tag}]"),
                Map.entry("gui.main.members.material", "PLAYER_HEAD"),
                Map.entry("gui.main.members.name", "<aqua>Members"),
                Map.entry("gui.main.invite.material", "NAME_TAG"),
                Map.entry("gui.main.invite.name", "<yellow>Invite a Player"),
                Map.entry("gui.main.rename.material", "ANVIL"),
                Map.entry("gui.main.rename.name", "<yellow>Rename Clan"),
                Map.entry("gui.main.disband.material", "TNT"),
                Map.entry("gui.main.disband.name", "<red>Disband Clan"),
                Map.entry("gui.main.leave.material", "BARRIER"),
                Map.entry("gui.main.leave.name", "<red>Leave Clan"),
                Map.entry("gui.main.admin_clans.material", "COMPASS"),
                Map.entry("gui.main.admin_clans.name", "<light_purple>Manage All Clans"),
                Map.entry("gui.members.title", "Clan Members"),
                Map.entry("gui.members.item.material", "PLAYER_HEAD"),
                Map.entry("gui.members.item.name", "<aqua>{player}"),
                Map.entry("gui.members.item.lore", "<gray>Rank: {rank}"),
                Map.entry("gui.members.rank.owner", "<gold>Owner"),
                Map.entry("gui.members.rank.admin", "<yellow>Admin"),
                Map.entry("gui.members.rank.member", "<green>Member"),
                Map.entry("gui.invitations.title", "Clan Invitations"),
                Map.entry("gui.invitations.empty.material", "PAPER"),
                Map.entry("gui.invitations.empty.name", "<gray>You have no clan invitations"),
                Map.entry("gui.invitations.item.material", "WRITABLE_BOOK"),
                Map.entry("gui.invitations.item.name", "<blue>Invitation from <gold>{clan}"),
                Map.entry("gui.invitations.item.lore", "<gray>Left-click to accept, right-click to decline."),
                Map.entry("gui.invitations.back.name", "<gray>Back"),
                Map.entry("gui.member_actions.title", "Member Actions"),
                Map.entry("gui.member_actions.rank.material", "NAME_TAG"),
                Map.entry("gui.member_actions.rank.name", "<yellow>Set Member Rank"),
                Map.entry("gui.ranks.title", "Set Member Rank"),
                Map.entry("gui.ranks.admin.material", "GOLD_INGOT"),
                Map.entry("gui.ranks.admin.name", "<yellow>Admin"),
                Map.entry("gui.ranks.member.material", "IRON_INGOT"),
                Map.entry("gui.ranks.member.name", "<green>Member"),
                Map.entry("gui.confirmation.title", "Confirm Clan Action")
        );
        boolean changed = false;
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            if (!configYML.isSet(entry.getKey())) {
                configYML.set(entry.getKey(), entry.getValue());
                changed = true;
            }
        }
        if (changed) {
            configYML.saveSync(CONFIG_FILE);
        }
    }
}
