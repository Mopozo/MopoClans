package org.Mopo.mopoClans.clandata;

import mopo.mopoLib.yml.MopoYaml;
import net.kyori.adventure.text.Component;
import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.inbox.ClanInviteItem;
import org.Mopo.mopoClans.playerdata.PlayerData;
import org.Mopo.mopoClans.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ClanManager {

    private final HashMap<String, Clan> clanMap = new HashMap<>();
    private final MopoYaml configYML = MopoClans.getConfigYML();
    private final PlayerDataManager playerdatamanager = MopoClans.getPLAYERDATAMANAGER();

    public void createClan(String clanName, String clanTag, UUID leaderUUID) {
        validateClanName(clanName);
        PlayerData playerData = playerdatamanager.getPlayerData(leaderUUID);
        if (playerData == null) {
            throw new IllegalStateException("Player data must be loaded before creating a clan");
        }
        if (playerData.getClanName() != null) {
            throw new IllegalStateException("Player is already in a clan");
        }
        if (clanMap.containsKey(clanName) || new File(MopoClans.getClanFolder(), clanName + ".yml").exists()) {
            throw new IllegalArgumentException("A clan with that name already exists");
        }
        Clan clan = new Clan(clanName, clanTag, leaderUUID);
        int maxMembers = configYML.getInt("max_clan_members",
                configYML.getInt("Clan.MaxClanSize", configYML.getInt("MaxClanSize", 4)));
        if (maxMembers < 1) {
            maxMembers = 4;
        }
        if (!configYML.isSet("max_clan_members") || configYML.getInt("max_clan_members") < 1) {
            configYML.set("max_clan_members", maxMembers);
            configYML.saveSync(new File(MopoClans.getFolder(), "config.yml"));
        }
        clan.setMaxMembers(maxMembers);
        clan.addClanMembersUUID(leaderUUID);
        File clanFile = new File(MopoClans.getClanFolder(), clanName + ".yml");
        clan.setClanFile(clanFile);
        clan.setYml(MopoYaml.loadFromFile(clanFile, true));
        playerData.setClanName(clanName);
        playerData.setClanRank("leader");
        clanMap.put(clanName, clan);
        clan.save();
        playerdatamanager.savePlayerData(leaderUUID);
    }


    /*
        player joins -> check if clan is loaded by another member
        if not:
            load clan

        player leaves:
            if no other member is online:
                unload clan


        then for getters:

        you can get clan, if in map, return. else. load from file but dont add to map.

    */



    public void loadClanMap() {
        clanMap.clear();
        File[] files = MopoClans.getClanFolder().listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isFile() || !file.getName().endsWith(".yml")) {
                    continue;
                }
                MopoYaml mopoYaml = MopoYaml.loadFromFile(file);
                Clan clan = new Clan();
                clan.setClanFile(file);
                clan.setYml(mopoYaml);
                clan.setClanName(mopoYaml.getString("name"));
                clan.setClanTag(mopoYaml.getString("tag"));
                clan.setMaxMembers(mopoYaml.getInt("max_members", 4));
                clan.setClanLeaderUUID(parseUuid(mopoYaml.getString("leader_uuid"), file, "leader_uuid"));
                clan.setClanPoints(mopoYaml.getInt("points"));
                clan.setClanBalance(mopoYaml.getDouble("balance"));
                clan.setClanLevel(mopoYaml.getInt("level"));
                clan.setClanEXP(mopoYaml.getDouble("experience"));
                readUuids(mopoYaml.getStringList("admin_uuids"), clan, true, file);
                readUuids(mopoYaml.getStringList("member_uuids"), clan, false, file);
                if (clan.getClanName() == null) {
                    clan.setClanName(file.getName().substring(0, file.getName().length() - 4));
                }
                clanMap.put(clan.getClanName(), clan);
            }
        }
    }

    public Clan getClan(String clanName) {
        return clanName == null ? null : clanMap.get(clanName);
    }

    public List<Clan> getClans() {
        return List.copyOf(clanMap.values());
    }

    public void deleteClan(String clanName) {
        if (clanName == null) {
            return;
        }
        validateClanName(clanName);
        Clan clan = getClan(clanName);
        File clanFile;
        if (clan == null) clanFile = new File(MopoClans.getClanFolder(), clanName + ".yml");
        else clanFile = clan.getClanFile();
        if (clan != null) {
            for (UUID memberUUID : clan.getClanMembersUUID()) {
                playerdatamanager.updateClanData(memberUUID, null, null);
            }
            clanMap.remove(clanName);
        }
        if (clanFile.exists() && !clanFile.delete()) {
            throw new IllegalStateException("Could not delete clan file: " + clanFile);
        }
    }

    public void renameClan(String oldName, String newName) {
        validateClanName(newName);
        Clan clan = getClan(oldName);
        if (clan == null) {
            return;
        }
        File newFile = new File(MopoClans.getClanFolder(), newName + ".yml");
        if (newFile.exists()) {
            throw new IllegalArgumentException("A clan with that name already exists");
        }
        File oldFile = clan.getClanFile();
        if (!oldFile.renameTo(newFile)) {
            throw new IllegalStateException("Could not rename clan file: " + oldFile);
        }
        clan.setClanName(newName);
        clan.setClanFile(newFile);
        clan.setYml(MopoYaml.loadFromFile(newFile));
        clanMap.remove(oldName);
        clanMap.put(newName, clan);
        for (UUID memberUUID : clan.getClanMembersUUID()) {
            String rank = memberUUID.equals(clan.getClanLeaderUUID()) ? "leader"
                    : clan.getClanAdminsUUID().contains(memberUUID) ? "admin" : "default";
            playerdatamanager.updateClanData(memberUUID, newName, rank);
        }
        clan.save();
    }

    public void saveAll() {
        clanMap.values().forEach(Clan::save);
    }

    public boolean addClanMember(UUID playerUUID, String clanName) {
        PlayerData playerData = playerdatamanager.getPlayerData(playerUUID);
        Clan clan = getClan(clanName);
        if (playerData == null || clan == null || playerData.getClanName() != null
                || clan.getClanMembersUUID().size() >= clan.getMaxMembers()) {
            return false;
        }
        playerData.setClanName(clanName);
        playerData.setClanRank("default");
        playerData.removeIncomingClanInvite(clanName);
        if (playerData.getInbox() != null) {
            playerData.getInbox().removeClanInvite(clanName);
        }
        clan.addClanMembersUUID(playerUUID);
        clan.save();
        playerdatamanager.savePlayerData(playerUUID);
        return true;
    }

    public void removeClanMember(UUID playerUUID) {
        PlayerData playerData = playerdatamanager.getPlayerData(playerUUID);
        if (playerData != null) {
            String clanName = playerData.getClanName();
            if (clanName != null) {
                Clan clan = getClan(clanName);
                if (clan != null) {
                    clan.removeClanMembersUUID(playerUUID);
                    playerData.setClanName(null);
                    playerData.setClanRank(null);
                    clan.save();
                    playerdatamanager.savePlayerData(playerUUID);
                }
            }
        }
    }

    public void sendClanInvite(String playerName, String clanName) {
        Player player = Bukkit.getServer().getPlayer(playerName);
        Clan clan = getClan(clanName);
        if (clan != null) {
            if (player != null) {
                PlayerData playerData = playerdatamanager.getPlayerData(player.getUniqueId());
                if (playerData != null) {
                    boolean newInvite = !playerData.getIncomingClanInvites().contains(clanName);
                    if (newInvite) {
                        playerData.addIncomingClanInvites(clanName);
                        playerdatamanager.savePlayerData(player.getUniqueId());
                        if (playerData.getInbox() != null) {
                            playerData.getInbox().addContent(ClanInviteItem.create(clanName));
                        }
                    }
                    player.sendMessage(Component.text("You have been invited to clan " + clanName + "."));
                }
            }
        }
    }

    public void removeClanInvite(String playerName, String clanName) {
        if (getClan(clanName) != null) {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                removeClanInvite(player.getUniqueId(), clanName);
            }
        }
    }

    public void removeClanInvite(UUID playerUUID, String clanName) {
        PlayerData playerData = playerdatamanager.getPlayerData(playerUUID);
        if (playerData != null) {
            playerData.removeIncomingClanInvite(clanName);
            if (playerData.getInbox() != null) {
                playerData.getInbox().removeClanInvite(clanName);
            }
            playerdatamanager.savePlayerData(playerUUID);
        }
    }

    public void promoteClanMember(String playerName, String clanName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            promoteClanMember(player.getUniqueId(), clanName);
        }
    }

    public void promoteClanMember(UUID playerUUID, String clanName) {
        Clan clan = getClan(clanName);
        if (clan != null && clan.getClanMembersUUID().contains(playerUUID)
                && !clan.getClanAdminsUUID().contains(playerUUID)
                && !playerUUID.equals(clan.getClanLeaderUUID())) {
            clan.setClanAdminsUUID(playerUUID);
            playerdatamanager.updateClanData(playerUUID, clanName, "admin");
            clan.save();
        }
    }

    public boolean setClanMemberRank(UUID playerUUID, String clanName, String rank) {
        if (!"admin".equals(rank) && !"default".equals(rank)) {
            throw new IllegalArgumentException("Clan member rank must be admin or default");
        }
        Clan clan = getClan(clanName);
        if (clan == null || !clan.getClanMembersUUID().contains(playerUUID)
                || playerUUID.equals(clan.getClanLeaderUUID())) {
            return false;
        }
        if ("admin".equals(rank)) {
            clan.setClanAdminsUUID(playerUUID);
        } else {
            clan.removeClanAdminUUID(playerUUID);
        }
        playerdatamanager.updateClanData(playerUUID, clanName, rank);
        clan.save();
        return true;
    }

    public void setClanOwner(String playerName, String clanName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            setClanOwner(player.getUniqueId(), clanName);
        }
    }

    public void setClanOwner(UUID playerUUID, String clanName) {
        Clan clan = getClan(clanName);
        if (clan == null || !clan.getClanMembersUUID().contains(playerUUID)) {
            return;
        }
        UUID previousLeader = clan.getClanLeaderUUID();
        if (playerUUID.equals(previousLeader)) {
            return;
        }
        clan.setClanAdminsUUID(previousLeader);
        clan.removeClanAdminUUID(playerUUID);
        clan.setClanLeaderUUID(playerUUID);
        playerdatamanager.updateClanData(previousLeader, clanName, "admin");
        playerdatamanager.updateClanData(playerUUID, clanName, "leader");
        clan.save();
    }

    private void readUuids(List<String> uuids, Clan clan, boolean admins, File file) {
        for (String value : uuids) {
            UUID uuid = parseUuid(value, file, admins ? "admin_uuids" : "member_uuids");
            if (uuid != null) {
                if (admins) {
                    clan.setClanAdminsUUID(uuid);
                } else {
                    clan.addClanMembersUUID(uuid);
                }
            }
        }
    }

    private UUID parseUuid(String value, File file, String key) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            MopoClans.getINSTANCE().getLogger().warning("Invalid UUID in " + file + " at " + key + ": " + value);
            return null;
        }
    }

    private void validateClanName(String clanName) {
        if (clanName == null || !clanName.matches("[A-Za-z0-9_-]{1,32}")) {
            throw new IllegalArgumentException("Clan names must be 1-32 letters, numbers, underscores, or hyphens");
        }
    }
}
