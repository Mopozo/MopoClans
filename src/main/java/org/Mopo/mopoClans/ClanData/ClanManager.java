package org.Mopo.mopoClans.ClanData;

import mopo.mopoLib.yml.MopoYaml;
import org.Mopo.mopoClans.MopoClans;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClanManager {

    private final HashMap<String, Clan> clanMap = new HashMap<>();
    private final MopoYaml configYML = MopoClans.getConfigYML();



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

}
