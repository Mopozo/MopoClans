package org.Mopo.mopoClans.Listeners;

import mopo.mopoLib.nbt.NBT;
import org.Mopo.mopoClans.ClanData.ClanManager;
import org.Mopo.mopoClans.Inbox.InboxManager;
import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.PlayerData.PlayerData;
import org.Mopo.mopoClans.PlayerData.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

                if (event.getCurrentItem() != null) {
                event.setCancelled(true);
                    Boolean clanInvite = NBT.get(event.getCurrentItem(), nbt -> {
                        return nbt.hasTag("clanInvite");
                    });
                    if (clanInvite) {
                    ItemStack currentItem = event.getCurrentItem();
                        switch (event.getClick()) {
                            case LEFT -> {
                                String clanName = NBT.get(currentItem, nbt -> {return nbt.getString("clanName");});
                                PlayerData playerData = playerdatamanager.getPlayerData(player.getUniqueId());
                                clanManager.addClanMember(player.getUniqueId(), clanName);
                                inbox.remove(currentItem);
                                playerData.removeIncomingClanInvite(clanName);
                            }
                            case RIGHT -> {
                                String clanName = NBT.get(currentItem, nbt -> {return nbt.getString("clanName");});
                                PlayerData playerData = playerdatamanager.getPlayerData(player.getUniqueId());
                                inbox.remove(currentItem);
                                playerData.removeIncomingClanInvite(clanName);
                            }
                        }
                    }
            }
            }
        }
    }

}
