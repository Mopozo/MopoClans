package org.Mopo.mopoClans.playerdata;

import mopo.mopoLib.yml.MopoYaml;
import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.inbox.Inbox;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private final Map<String, UUID> playerUUIDMap = new HashMap<>();

    public PlayerData loadPlayerData(String userName, UUID uuid) {
        File playerFile = new File(MopoClans.getPlayerFolder(), uuid + ".yml");
        File legacyFile = new File(MopoClans.getPlayerFolder(), userName + ".yml");
        File dataSource = playerFile.exists() ? playerFile : legacyFile.exists() ? legacyFile : playerFile;
        MopoYaml playerYaml = MopoYaml.loadFromFile(dataSource, true);
        PlayerData playerData = new PlayerData(uuid);
        playerData.setPlayerFile(playerFile);
        playerData.setAdmin(playerYaml.getBoolean("admin", false));
        playerData.setClanName(playerYaml.getString("clan_name"));
        playerData.setClanRank(playerYaml.getString("clan_rank"));
        playerData.setIncomingClanInvites(playerYaml.getStringList("incoming_clan_invites"));
        playerData.setInbox(new Inbox(uuid));
        playerData.getInbox().load();
        playerDataMap.put(uuid, playerData);
        playerUUIDMap.put(userName, uuid);
        return playerData;
    }

    public void createPlayerData(String userName, UUID uuid) {
        loadPlayerData(userName, uuid);
        savePlayerData(uuid);
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public UUID getPlayerUUID(String userName) {
        return playerUUIDMap.get(userName);
    }

    public void savePlayerData(UUID uuid) {
        PlayerData playerData = playerDataMap.get(uuid);
        if (playerData == null) {
            return;
        }
        MopoYaml playerYaml = MopoYaml.loadFromFile(playerData.getPlayerFile(), true);
        playerYaml.set("uuid", uuid.toString());
        playerYaml.set("admin", playerData.isAdmin());
        playerYaml.set("clan_name", playerData.getClanName());
        playerYaml.set("clan_rank", playerData.getClanRank());
        playerYaml.set("incoming_clan_invites", playerData.getIncomingClanInvites());
        playerYaml.saveSync(playerData.getPlayerFile());
        if (playerData.getInbox() != null) {
            playerData.getInbox().save();
        }
    }

    public void saveAllPlayerData() {
        for (UUID uuid : playerDataMap.keySet()) {
            savePlayerData(uuid);
        }
    }

    public void updateClanData(UUID uuid, String clanName, String clanRank) {
        PlayerData playerData = playerDataMap.get(uuid);
        if (playerData != null) {
            playerData.setClanName(clanName);
            playerData.setClanRank(clanRank);
            savePlayerData(uuid);
            return;
        }

        File playerFile = new File(MopoClans.getPlayerFolder(), uuid + ".yml");
        if (!playerFile.exists()) {
            playerFile = findLegacyPlayerFile(uuid);
        }
        if (playerFile == null || !playerFile.exists()) {
            return;
        }
        MopoYaml playerYaml = MopoYaml.loadFromFile(playerFile, true);
        playerYaml.set("uuid", uuid.toString());
        playerYaml.set("clan_name", clanName);
        playerYaml.set("clan_rank", clanRank);
        playerYaml.saveSync(playerFile);
    }

    private File findLegacyPlayerFile(UUID uuid) {
        File[] files = MopoClans.getPlayerFolder().listFiles((folder, name) -> name.endsWith(".yml"));
        if (files == null) {
            return null;
        }
        for (File file : files) {
            MopoYaml playerYaml = MopoYaml.loadFromFile(file);
            if (uuid.toString().equals(playerYaml.getString("uuid"))) {
                return file;
            }
        }
        return null;
    }
}
