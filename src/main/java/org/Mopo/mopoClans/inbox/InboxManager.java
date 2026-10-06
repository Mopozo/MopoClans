package org.Mopo.mopoClans.inbox;

import org.Mopo.mopoClans.inventories.InboxHolder;
import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.playerdata.PlayerData;
import org.Mopo.mopoClans.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class InboxManager {

    public void addContent(UUID targetUUID, ItemStack item) {
        PlayerDataManager playerdatamanager = MopoClans.getPLAYERDATAMANAGER();
        PlayerData playerData = playerdatamanager.getPlayerData(targetUUID);
        if (playerData != null && playerData.getInbox() != null) {
            playerData.getInbox().addContent(item);
        }

    }

    public Inventory getInbox(UUID playerUUID) {
        PlayerData playerData = MopoClans.getPLAYERDATAMANAGER().getPlayerData(playerUUID);
        if (playerData == null || playerData.getInbox() == null) {
            return null;
        }
        Inventory inventory = Bukkit.createInventory(new InboxHolder(), 54, "Inbox");
        List<ItemStack> contents = playerData.getInbox().getContents();
        for (ItemStack item : contents) {
            if (item != null) {
                inventory.addItem(item.clone());
            }
        }
        return inventory;
    }

    public Inbox createInbox(UUID playerUUID) {
        return new Inbox(playerUUID);
    }
}
