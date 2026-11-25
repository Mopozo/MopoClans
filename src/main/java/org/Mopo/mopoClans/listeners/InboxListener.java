package org.Mopo.mopoClans.listeners;

import mopo.mopoLib.nbt.NBT;
import mopo.mopoLib.nbt.iface.ReadableItemNBT;
import org.Mopo.mopoClans.clandata.ClanManager;
import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.inventories.InboxHolder;
import org.Mopo.mopoClans.playerdata.PlayerData;
import org.Mopo.mopoClans.playerdata.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class InboxListener implements Listener {

    ClanManager clanManager = MopoClans.getCLANMANAGER();
    PlayerDataManager playerdatamanager = MopoClans.getPLAYERDATAMANAGER(); //todo camel case playerDataManager

    @EventHandler
    public void playerClickEvent(InventoryClickEvent event) {
        if ((event.getWhoClicked() instanceof Player player)) {
            Inventory topInventory = player.getOpenInventory().getTopInventory();
            if (topInventory.getHolder() instanceof InboxHolder) {
                event.setCancelled(true);
                ItemStack currentItem = event.getCurrentItem();
                if (currentItem == null || !NBT.get(currentItem,
                        (Function<ReadableItemNBT, Boolean>) nbt -> nbt.hasTag("clanInvite"))) {
                    return;
                }
                String clanName = NBT.get(currentItem, nbt ->
                        nbt.hasTag("clanName") ? nbt.getString("clanName") : null);
                if (clanName == null) {
                    return;
                }
                PlayerData playerData = playerdatamanager.getPlayerData(player.getUniqueId());
                switch (event.getClick()) {
                    case LEFT -> {
                        if (!clanManager.addClanMember(player.getUniqueId(), clanName)) {
                            player.sendMessage("Could not join that clan. It may be full, unavailable, or you may already be in a clan.");
                            return;
                        }
                        topInventory.remove(currentItem);
                        player.sendMessage("You joined clan " + clanName + ".");
                    }
                    case RIGHT -> {
                        topInventory.remove(currentItem);
                        if (playerData != null) {
                            playerData.removeIncomingClanInvite(clanName);
                            if (playerData.getInbox() != null) {
                                playerData.getInbox().removeContent(currentItem);
                            }
                            playerdatamanager.savePlayerData(player.getUniqueId());
                        }
                        player.sendMessage("Clan invitation declined.");
                    }
                    default -> { }
                }
            }
        }
    }

}
