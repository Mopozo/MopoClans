package org.Mopo.mopoClans.Listeners;

import org.Mopo.mopoClans.ClanData.ClanManager;
import org.Mopo.mopoClans.Inbox.InboxManager;
import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.PlayerData.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InboxListener implements Listener {

    ClanManager clanManager = MopoClans.getCLANMANAGER();
    PlayerDataManager playerdatamanager = MopoClans.getPLAYERDATAMANAGER();
    InboxManager inboxManager = MopoClans.getINBOXMANAGER();

    boolean testing = true;

    @EventHandler
    public void playerClickEvent(InventoryClickEvent event) {
        if ((event.getWhoClicked() instanceof Player player)) {
            Inventory inbox = inboxManager.getInbox(player.getUniqueId());
            Inventory topInventory = player.getOpenInventory().getTopInventory();
            if (topInventory.getHolder().equals(inbox.getHolder())) {
                if (testing) {
                    player.sendMessage("Holders match");
                }

                event.setCancelled(true);
                if ()
            }
        }
    }

}
