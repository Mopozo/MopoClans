package org.Mopo.mopoClans.Listeners;

import mopo.mopoLib.nbt.NBT;
import org.Mopo.mopoClans.Inventories.ClanGUIHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ClanListener implements Listener {

    @EventHandler
    public void playerClickEvent(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof  Player player) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof ClanGUIHolder) {
                e.setCancelled(true);
                ItemStack currentItem = e.getCurrentItem();
                if (currentItem != null) {
                    String action = NBT.get(currentItem, nbt -> {
                        return nbt.getString("action");
                    });
                    switch (action) {
                        case "close" -> {
                            player.closeInventory();
                        }
                        case "profile" -> {

                        }
                        case "members" -> {

                        }
                        case "inbox" -> {

                        }
                        default -> {}
                    }

                }
            }
        }
    }

}
