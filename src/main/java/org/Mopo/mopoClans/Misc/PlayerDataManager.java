package org.Mopo.mopoClans.Misc;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDataManager {
    private HashMap<UUID, PlayerData> playerDataMap = new HashMap<UUID, PlayerData>();
    private HashMap<String, UUID> playerUUIDMap = new HashMap<String, UUID>();

    public void createPlayerData(String userName, UUID uuid) {
        playerUUIDMap.put(userName, uuid);
        PlayerData playerData = new PlayerData(uuid);
        playerDataMap.put(uuid, playerData);
    }
}
