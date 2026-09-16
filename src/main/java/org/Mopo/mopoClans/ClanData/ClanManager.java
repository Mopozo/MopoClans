package org.Mopo.mopoClans.ClanData;

import mopo.mopoLib.yml.MopoYaml;
import org.Mopo.mopoClans.MopoClans;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClanManager {

    private final String[] clanNames = MopoClans.getFolder().list();
    private final MopoYaml configYML = MopoClans.getConfigYML();
    public static File CLANS_FOLDER = new File(MopoClans.getFolder(), "Clans");


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
        File clanFile = new File(CLANS_FOLDER, clanName + ".yml");
        clan.setClanFile(clanFile);
        MopoYaml yml = clan.getYml();
        yml.set("clan_name", clan);
        yml.saveAsync(clanFile);
    }

    public void deleteClan(String clanName) {
        File clanFile = new File(CLANS_FOLDER, clanName + ".yml");
        if (!clanFile.delete()) {
            System.out.println("File doesnt exist");
        }
    }

    public void renameClan(String oldName, String newName) {
        File clanFile = new File(CLANS_FOLDER, oldName + ".yml");
        if (clanFile.exists()) {
            clanFile.renameTo(new File(CLANS_FOLDER, newName + ".yml"));
        }
    }

}
