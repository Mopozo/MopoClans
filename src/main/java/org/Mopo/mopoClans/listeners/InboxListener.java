package org.Mopo.mopoClans.listeners;

import mopo.mopoLib.nbt.NBT;
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

public class InboxListener implements Listener {

    ClanManager clanManager = MopoClans.getCLANMANAGER();
    PlayerDataManager playerdatamanager = MopoClans.getPLAYERDATAMANAGER(); //todo camel case playerDataManager

    boolean testing = true;

    @EventHandler
    public void playerClickEvent(InventoryClickEvent event) {
        if ((event.getWhoClicked() instanceof Player player)) {
            Inventory topInventory = player.getOpenInventory().getTopInventory();
            if (topInventory.getHolder() instanceof InboxHolder) {
                if (testing) {
                    player.sendMessage("Holders match");
                }

                if (event.getCurrentItem() != null) {
                    event.setCancelled(true);
                    Boolean clanInvite = NBT.get(event.getCurrentItem(), nbt -> {
                        return nbt.hasTag("clanInvite");
                    });
                    if (clanInvite) {
                        ItemStack currentItem = event.getCurrentItem();
                        switch (event.getClick()) {
                            case LEFT -> {
                                String clanName = NBT.get(currentItem, nbt -> {
                                    if (!nbt.hasTag("clanName")) {
                                        return null;
                                    }
                                    return nbt.getString("clanName");
                                });
                                if (clanName == null) {
                                    return;
                                }

                                PlayerData playerData = playerdatamanager.getPlayerData(player.getUniqueId());
                                clanManager.addClanMember(player.getUniqueId(), clanName);
                                topInventory.remove(currentItem);
                                if (playerData != null) {
                                    playerData.removeIncomingClanInvite(clanName);
                                    if (playerData.getInbox() != null) {
                                        playerData.getInbox().removeContent(currentItem);
                                    }
                                    playerdatamanager.savePlayerData(player.getUniqueId());
                                }
                            }
                            case RIGHT -> {
                                String clanName = NBT.get(currentItem, nbt -> {
                                    return nbt.getString("clanName");
                                });
                                PlayerData playerData = playerdatamanager.getPlayerData(player.getUniqueId());
                                topInventory.remove(currentItem);
                                if (playerData != null) {
                                    playerData.removeIncomingClanInvite(clanName);
                                    if (playerData.getInbox() != null) {
                                        playerData.getInbox().removeContent(currentItem);
                                    }
                                    playerdatamanager.savePlayerData(player.getUniqueId());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
