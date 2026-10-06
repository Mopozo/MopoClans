package org.Mopo.mopoClans.inventories;


import mopo.mopoLib.items.CreateItem;
import mopo.mopoLib.nbt.NBT;
import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.playerdata.PlayerData;
import org.Mopo.mopoClans.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ClanGUI {

    public Inventory getClanGui(UUID playerUUID) {
        PlayerDataManager playerdatamanager = MopoClans.getPLAYERDATAMANAGER();
        PlayerData playerData = playerdatamanager.getPlayerData(playerUUID);

        ItemStack closeItem = CreateItem.createItem(Material.BARRIER, "<red>Close");
        NBT.modify(closeItem, nbt -> {
            nbt.setBoolean("closeitem", true);
            nbt.setString("action", "close");
        });

        ItemStack profileItem = CreateItem.createItem(Material.PLAYER_HEAD, "<profile>");
        NBT.modify(profileItem, nbt -> {
            nbt.setString("profile", playerUUID.toString());
            nbt.setString("action", "profile");
        });

        ItemStack membersItem = CreateItem.createItem(Material.DIAMOND_NAUTILUS_ARMOR, "<aqua>Members");
        NBT.modify(membersItem, nbt -> {
            nbt.setString("clan", playerData.getClanName());
            nbt.setString("action", "members");
        });

        ItemStack inboxItem = CreateItem.createItem(Material.BOOK, "<lime>Inbox");
        NBT.modify(inboxItem, nbt -> {
            nbt.setBoolean("inbox", true);
            nbt.setString("action", "inbox");
        });

        Inventory inventory = Bukkit.createInventory(new ClanGUIHolder(), 18);
        inventory.setItem(13,closeItem );
        inventory.setItem(5, profileItem);
        inventory.setItem(3, membersItem);
        inventory.setItem(7, inboxItem);

        return inventory;
    }


}
