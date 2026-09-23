package org.Mopo.mopoClans.PlayerData;

import mopo.mopoLib.inventories.BufferManager;
import mopo.mopoLib.yml.MopoYaml;
import org.Mopo.mopoClans.Inbox.Inbox;
import org.Mopo.mopoClans.MopoClans;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;



public class PlayerDataManager {
    private HashMap<UUID, PlayerData> playerDataMap = new HashMap<UUID, PlayerData>();
    private HashMap<String, UUID> playerUUIDMap = new HashMap<String, UUID>();

    public void createPlayerData(String userName, UUID uuid) {
        playerUUIDMap.put(userName, uuid);
        PlayerData playerData = new PlayerData(uuid);
        playerData.setAdmin(false);
        playerData.setInbox(new Inbox(uuid));
        playerData.setPlayerFile(new File(MopoClans.getPlayerFolder(), userName + ".yml"));
        playerDataMap.put(uuid, playerData);
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public UUID getPlayerUUID(String userName) {
        return playerUUIDMap.get(userName);
    }

    public void savePlayerData(UUID uuid) {
        if (playerDataMap.containsKey(uuid)) {
            PlayerData playerData = playerDataMap.get(uuid);
            MopoYaml playerYaml = MopoClans.getPlayerYaml();
            File playerFile = playerData.getPlayerFile();
            playerYaml.saveAsync(playerFile);
        }
    }

    public void saveAllPlayerData() {
        for (PlayerData playerData : playerDataMap.values()) {
            MopoYaml playerYaml = MopoClans.getPlayerYaml();
            File playerFile = playerData.getPlayerFile();
            playerYaml.saveAsync(playerFile);
        }
    }

}
