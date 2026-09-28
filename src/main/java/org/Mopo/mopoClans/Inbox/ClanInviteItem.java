package org.Mopo.mopoClans.Inbox;

import mopo.mopoLib.items.CreateItem;
import mopo.mopoLib.nbt.NBT;
import org.Mopo.mopoClans.ClanData.ClanManager;
import org.Mopo.mopoClans.MopoClans;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ClanInviteItem {

    ClanManager clanManager = MopoClans.getCLANMANAGER();
    InboxManager inboxManager = MopoClans.getINBOXMANAGER();

    private ItemStack item;
    private List<String> acceptDeclineLore;



    ClanInviteItem(String clanName, Player player) {
        this.acceptDeclineLore.add("<green>Left click to ACCEPT!");
        this.acceptDeclineLore.add("<red>Right click to DECLINE!");
        if (clanManager.getClan(clanName) != null) {
            this.item = CreateItem.createItem(Material.WRITABLE_BOOK, "<blue>Clan Invite", "<grey>Clan " + clanName + " invited you to join them!", acceptDeclineLore);
            NBT.modify(this.item, nbt -> {
                nbt.setBoolean("clanInvite", true);
                nbt.setString("clanName", clanName);
            } );
            inboxManager.getInbox(player.getUniqueId()).addItem(this.item);
        }
    }

}
