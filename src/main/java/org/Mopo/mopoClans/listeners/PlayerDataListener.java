package org.Mopo.mopoClans.listeners;

import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.playerdata.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDataListener implements Listener {
    private final PlayerDataManager playerDataManager = MopoClans.getPLAYERDATAMANAGER();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerDataManager.loadPlayerData(event.getPlayer().getName(), event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerDataManager.savePlayerData(event.getPlayer().getUniqueId());
    }
}
