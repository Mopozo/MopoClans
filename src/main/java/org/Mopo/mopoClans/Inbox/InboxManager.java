package org.Mopo.mopoClans.Inbox;

import mopo.mopoLib.inventories.BufferManager;
import org.Mopo.mopoClans.Inventories.InboxHolder;
import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.PlayerData.PlayerData;
import org.Mopo.mopoClans.PlayerData.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class InboxManager {

    public void addContent(UUID targetUUID, ItemStack item) {
        PlayerDataManager playerdatamanager = MopoClans.getPLAYERDATAMANAGER();
        PlayerData playerData = playerdatamanager.getPlayerData(targetUUID);
        if (playerData != null) {
            List<ItemStack> contents = playerData.getInbox().getContents();
            contents.add(item);
            Inventory inbox = MopoClans.getINBOXMANAGER().getInbox(targetUUID);
            Inventory targetBuffer = MopoClans.getBUFFERMANAGER().loadInventoryFromBuffer(targetUUID.toString() + "InboxBuffer", inbox);
            MopoClans.getBUFFERMANAGER().addItemToBuffer(targetUUID.toString() + "InboxBuffer", targetBuffer.firstEmpty(), item);
        }

    }

    public Inventory getInbox(UUID playerUUID) {
        BufferManager buffermanager = MopoClans.getBUFFERMANAGER();
        Inventory bufferinv = Bukkit.createInventory(new InboxHolder(), 54, "Inbox");
        Inventory inventory = buffermanager.loadInventoryFromBuffer(playerUUID.toString() + "InboxBuffer", bufferinv);
        if (inventory != null) {
            return inventory;
        }

        return null;
    }

    public Inbox createInbox(UUID playerUUID) {
        return new Inbox(playerUUID);
    }
}
