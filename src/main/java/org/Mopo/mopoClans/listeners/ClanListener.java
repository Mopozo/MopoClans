package org.Mopo.mopoClans.listeners;

import mopo.mopoLib.nbt.NBT;
import org.Mopo.mopoClans.inventories.ClanGUIHolder;
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
                        //todo has tag
                        return nbt.getString("action");
                    });

                    //todo null check action after

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
