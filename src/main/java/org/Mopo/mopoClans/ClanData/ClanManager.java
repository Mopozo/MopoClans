package org.Mopo.mopoClans.ClanData;

import mopo.mopoLib.yml.MopoYaml;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.PlayerData.PlayerData;
import org.Mopo.mopoClans.PlayerData.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClanManager {

    private final HashMap<String, Clan> clanMap = new HashMap<>();
    private final MopoYaml configYML = MopoClans.getConfigYML();

    PlayerDataManager playerdatamanager = MopoClans.getPLAYERDATAMANAGER();
    ClanManager clanManager = MopoClans.getCLANMANAGER();

    public void createClan(String clanName,  String clanTag, UUID leaderUUID) {
        Clan clan = new Clan(clanName, clanTag, leaderUUID);
        try {
            int maxMember = configYML.getInt("max_clan_members");
            clan.setMaxMembers(maxMember);
        } catch (Exception _) {
            int maxMembers = 4;
            configYML.set("max_clan_members", maxMembers);
            clan.setMaxMembers(maxMembers);
        }
        File clanFile = new File(MopoClans.getClanFolder(), clanName + ".yml");
        clan.setClanFile(clanFile);
        PlayerData playerData = playerdatamanager.getPlayerData(leaderUUID);
        playerData.setClanRank("leader");
        MopoYaml yml = clan.getYml();
        yml.set("clan_name", clan);
        clanMap.put(clanName, clan);
        yml.saveAsync(clanFile);
    }

    public void loadClanMap() {
        File[] files = MopoClans.getClanFolder().listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String clanSubstring = fileName.substring(0, fileName.length() - 4);
                File clanFile = new File(MopoClans.getClanFolder(), fileName);
                MopoYaml mopoYaml = MopoYaml.loadFromFile(clanFile);
                ConfigurationSection clanConfigSection = mopoYaml.getConfigurationSection("clan_name");
                Clan clan = new Clan();
                for (String entry : clanConfigSection.getKeys(false)) {
                    switch (entry) {
                        case "max_clan_members" -> {}
                    }
                }
                clanMap.put(clanSubstring, (Clan) clan);
            }
        }
    }

    public Clan getClan(String clanName) {
        if (clanMap.containsKey(clanName)) {
            return clanMap.get(clanName);
        }else {
            return null;
        }
    }

    public void deleteClan(String clanName) {
        File clanFile = new File(MopoClans.getClanFolder(), clanName + ".yml");
        if (!clanFile.delete()) {
            System.out.println("File doesnt exist");
        }
    }

    public void renameClan(String oldName, String newName) {
        File clanFile = new File(MopoClans.getClanFolder(), oldName + ".yml");
        if (clanFile.exists()) {
            clanFile.renameTo(new File(MopoClans.getClanFolder(), newName + ".yml"));
        }
    }

    public void addClanMember(UUID playerUUID, String clanName) {

        PlayerData playerData = playerdatamanager.getPlayerData(playerUUID);
        String currentClan = playerData.getClanName();

        Clan clan = clanManager.getClan(clanName);
        if (clan != null) {
            if (currentClan == null) {
                playerData.setClanName(clanName);
                clan.addClanMembersUUID(playerUUID);
            }
        }
    }

    public void removeClanMember(UUID playerUUID) {
        PlayerData playerData = playerdatamanager.getPlayerData(playerUUID);
        if (playerData != null) {
            String clanName = playerData.getClanName();
            if (clanName != null) {
                Clan clan = clanManager.getClan(clanName);
                if (clanManager.getClan(clanName) != null) {
                    clan.removeClanMembersUUID(playerUUID);
                }
            }
        }
    }

    public void sendClanInvite(String playerName, String clanName) {
        Player player = Bukkit.getServer().getPlayer(playerName);
        if (clanManager.getClan(clanName) != null) {
            if (player != null) {
                TextComponent accept = Component.text("Accept")
                        .color(NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("/clan accept" + clanName))
                        .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize("<green>Accept")));
                TextComponent deny = Component.text("Deny")
                        .color(NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("/clan deny" + clanName))
                        .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize("<red>Deny")));
                MiniMessage.miniMessage().deserialize("<blue> You have been invited to clan " + clanName + ". Click to " + accept + " or " + deny);
                PlayerData playerData = playerdatamanager.getPlayerData(player.getUniqueId());
                playerData.addIncomingClanInvites(clanName);
            }
        }
    }

    public void removeClanInvite(String playerName, String clanName) {
        if (clanManager.getClan(clanName) != null) {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                PlayerData playerData = playerdatamanager.getPlayerData(player.getUniqueId());
                playerData.removeIncomingClanInvite(clanName);
            }
        }
    }

    public void promoteClanMember(String playerName, String clanName) {
        Clan clan = clanManager.getClan(clanName);
        if (clan != null) {
            List<UUID> clanAdminsUUID = clan.getClanAdminsUUID();
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                if (clanAdminsUUID.contains(player.getUniqueId())) {
                    clan.setClanAdminsUUID(player.getUniqueId());
                    PlayerData playerData = playerdatamanager.getPlayerData(player.getUniqueId());
                    playerData.setClanRank("admin");
                }
            }
        }
    }
}
