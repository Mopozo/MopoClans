package org.Mopo.mopoClans.inbox;

import mopo.mopoLib.items.CreateItem;
import mopo.mopoLib.nbt.NBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ClanInviteItem {

    private ClanInviteItem() {
    }

    public static ItemStack create(String clanName) {
        List<String> lore = new ArrayList<>();
        lore.add("<green>Left click to ACCEPT!");
        lore.add("<red>Right click to DECLINE!");
        ItemStack item = CreateItem.createItem(Material.WRITABLE_BOOK, "<blue>Clan Invite",
                "<grey>Clan " + clanName + " invited you to join them!", lore);
        NBT.modify(item, nbt -> {
            nbt.setBoolean("clanInvite", true);
            nbt.setString("clanName", clanName);
        });
        return item;
    }

}
