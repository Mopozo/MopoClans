package org.Mopo.mopoClans.inbox;

import mopo.mopoLib.items.CreateItem;
import mopo.mopoLib.nbt.NBT;
import org.Mopo.mopoClans.MopoClans;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ClanInviteItem {

    private ClanInviteItem() {
    }

    public static ItemStack create(String clanName) {
        String itemName = MopoClans.getConfigYML().getString(
                "gui.invitations.item.name", "<blue>Invitation from <gold>{clan}").replace("{clan}", clanName);
        String loreLine = MopoClans.getConfigYML().getString(
                "gui.invitations.item.lore", "<gray>Left-click to accept, right-click to decline.")
                .replace("{clan}", clanName);
        List<String> lore = new ArrayList<>();
        lore.add(loreLine);
        ItemStack item = CreateItem.createItem(Material.WRITABLE_BOOK, itemName, lore);
        NBT.modify(item, nbt -> {
            nbt.setBoolean("clanInvite", true);
            nbt.setString("clanName", clanName);
        });
        return item;
    }

}
