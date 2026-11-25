package org.Mopo.mopoClans.inventories;

import mopo.mopoLib.items.CreateItem;
import mopo.mopoLib.nbt.NBT;
import mopo.mopoLib.nbt.iface.ReadWriteItemNBT;
import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.clandata.Clan;
import org.Mopo.mopoClans.clandata.ClanManager;
import org.Mopo.mopoClans.playerdata.PlayerData;
import org.Mopo.mopoClans.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ClanGUI {

    public Inventory getClanGui(UUID playerUUID) {
        PlayerData playerData = playerDataManager().getPlayerData(playerUUID);
        Inventory inventory = Bukkit.createInventory(new ClanGUIHolder(),
                27, configString("gui.main.title", "Clan Menu"));
        if (playerData == null) {
            return inventory;
        }

        if (playerData.getClanName() == null) {
            setConfiguredAction(inventory, 11, Material.EMERALD, "gui.main.create",
                    "<green>Create a Clan", "create");
            setConfiguredAction(inventory, 15, Material.BOOK, "gui.main.invitations",
                    "<aqua>Clan Invitations", "inbox");
        } else {
            Clan clan = clanManager().getClan(playerData.getClanName());
            if (clan != null) {
                String tag = clan.getClanTag() == null ? "" : clan.getClanTag();
                String clanInfoName = configString("gui.main.clan_info.name", "<gold>{clan} <gray>[{tag}]")
                        .replace("{clan}", clan.getClanName())
                        .replace("{tag}", tag);
                setAction(inventory, 4,
                        configuredMaterial("gui.main.clan_info.material", Material.WHITE_BANNER),
                        clanInfoName, "info");
            }
            setConfiguredAction(inventory, 10, Material.PLAYER_HEAD, "gui.main.members",
                    "<aqua>Members", "members");
            setConfiguredAction(inventory, 12, Material.NAME_TAG, "gui.main.invite",
                    "<yellow>Invite a Player", "invite");
            setConfiguredAction(inventory, 14, Material.ANVIL, "gui.main.rename",
                    "<yellow>Rename Clan", "rename");
            setConfiguredAction(inventory, 16, Material.TNT, "gui.main.disband",
                    "<red>Disband Clan", "confirm_disband");
            setConfiguredAction(inventory, 22, Material.BARRIER, "gui.main.leave",
                    "<red>Leave Clan", "confirm_leave");
            setConfiguredAction(inventory, 18, Material.BOOK, "gui.main.invitations",
                    "<aqua>Clan Invitations", "inbox");
        }

        if (playerData.isAdmin()) {
            setConfiguredAction(inventory, 26, Material.COMPASS, "gui.main.admin_clans",
                    "<light_purple>Manage All Clans", "admin_clans");
        }
        setConfiguredAction(inventory, 0, Material.BARRIER, "gui.main.close", "<gray>Close", "close");
        return inventory;
    }

    public Inventory getInvitationsGui(UUID playerUUID) {
        return getInvitationsGui(playerUUID, 0);
    }

    public Inventory getInvitationsGui(UUID playerUUID, int page) {
        PlayerData playerData = playerDataManager().getPlayerData(playerUUID);
        Inventory inventory = Bukkit.createInventory(new ClanGUIHolder(),
                54, configString("gui.invitations.title", "Clan Invitations"));
        if (playerData == null) {
            setConfiguredAction(inventory, 49, Material.ARROW, "gui.invitations.back",
                    "<gray>Back", "main_back");
            return inventory;
        }

        List<String> invitations = playerData.getIncomingClanInvites();
        if (invitations.isEmpty()) {
            inventory.setItem(22, CreateItem.createItem(
                    configuredMaterial("gui.invitations.empty.material", Material.PAPER),
                    configString("gui.invitations.empty.name", "<gray>You have no clan invitations")));
        }
        int pageCount = Math.max(1, (invitations.size() + 44) / 45);
        int currentPage = Math.max(0, Math.min(page, pageCount - 1));
        int firstIndex = currentPage * 45;
        for (int index = firstIndex; index < Math.min(firstIndex + 45, invitations.size()); index++) {
            String clanName = invitations.get(index);
            Clan clan = clanManager().getClan(clanName);
            String clanLabel = clan == null || clan.getClanTag() == null
                    ? clanName : clanName + " [" + clan.getClanTag() + "]";
            ItemStack item = CreateItem.createItem(
                    configuredMaterial("gui.invitations.item.material", Material.WRITABLE_BOOK),
                    configString("gui.invitations.item.name", "<blue>Invitation from <gold>{clan}")
                            .replace("{clan}", clanLabel),
                    List.of(configString("gui.invitations.item.lore", "<gray>Left-click to accept, right-click to decline.")
                            .replace("{clan}", clanLabel)));
            NBT.modify(item, nbt -> {
                nbt.setString("action", "clan_invite");
                nbt.setString("clan", clanName);
            });
            inventory.setItem(index - firstIndex, item);
        }
        setConfiguredAction(inventory, 49, Material.ARROW, "gui.invitations.back",
                "<gray>Back", "main_back");
        if (currentPage > 0) {
            setPageAction(inventory, 45, "<yellow>Previous Page", currentPage - 1, "invite_page");
        }
        if (currentPage + 1 < pageCount) {
            setPageAction(inventory, 53, "<yellow>Next Page", currentPage + 1, "invite_page");
        }
        return inventory;
    }

    public Inventory getMembersGui(String clanName) {
        Inventory inventory = Bukkit.createInventory(new ClanGUIHolder(),
                54, configString("gui.members.title", "Clan Members"));
        Clan clan = clanManager().getClan(clanName);
        if (clan == null) {
            return inventory;
        }

        int slot = 0;
        for (UUID memberUUID : clan.getClanMembersUUID()) {
            if (slot >= 45) {
                break;
            }
            OfflinePlayer member = Bukkit.getOfflinePlayer(memberUUID);
            String name = member.getName() == null ? memberUUID.toString().substring(0, 8) : member.getName();
            String rank = memberUUID.equals(clan.getClanLeaderUUID()) ? "Owner"
                    : clan.getClanAdminsUUID().contains(memberUUID) ? "Admin" : "Member";
            ItemStack item = CreateItem.createItem(
                    configuredMaterial("gui.members.item.material", Material.PLAYER_HEAD),
                    configString("gui.members.item.name", "<aqua>{player}").replace("{player}", name),
                    List.of(configString("gui.members.item.lore", "<gray>Rank: {rank}").replace("{rank}",
                            configString("gui.members.rank." + rank.toLowerCase(), rank))));
            if (item.getItemMeta() instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(member);
                item.setItemMeta(skullMeta);
            }
            NBT.modify(item, nbt -> {
                nbt.setString("action", "member_options");
                nbt.setString("clan", clanName);
                nbt.setString("member", memberUUID.toString());
            });
            inventory.setItem(slot++, item);
        }
        setAction(inventory, 49, Material.ARROW, "<gray>Back", "main_back");
        return inventory;
    }

    public Inventory getMemberActionsGui(String clanName, UUID memberUUID, UUID viewerUUID) {
        Inventory inventory = Bukkit.createInventory(new ClanGUIHolder(),
                27, configString("gui.member_actions.title", "Member Actions"));
        Clan clan = clanManager().getClan(clanName);
        PlayerData viewerData = playerDataManager().getPlayerData(viewerUUID);
        if (clan == null || viewerData == null) {
            return inventory;
        }
        boolean isLeader = viewerUUID.equals(clan.getClanLeaderUUID());
        boolean isAdmin = viewerData.isAdmin() || "admin".equals(viewerData.getClanRank()) || isLeader;
        if (isLeader && !memberUUID.equals(clan.getClanLeaderUUID())) {
            setTargetAction(inventory, 11, configuredMaterial("gui.member_actions.rank.material", Material.NAME_TAG),
                    configString("gui.member_actions.rank.name", "<yellow>Set Member Rank"),
                    "manage_rank", clanName, memberUUID);
        } else if (isAdmin && !memberUUID.equals(clan.getClanLeaderUUID())
                && !clan.getClanAdminsUUID().contains(memberUUID)) {
            setTargetAction(inventory, 11, Material.GOLD_INGOT, "<yellow>Promote to Admin",
                    "promote", clanName, memberUUID);
        }
        if (isLeader && !memberUUID.equals(viewerUUID)) {
            setTargetAction(inventory, 15, Material.GOLDEN_HELMET, "<gold>Transfer Leadership",
                    "confirm_transfer", clanName, memberUUID);
        }
        setTargetAction(inventory, 22, Material.ARROW, "<gray>Back to Members",
                "members_back", clanName, null);
        return inventory;
    }

    public Inventory getMemberRankGui(String clanName, UUID memberUUID) {
        Inventory inventory = Bukkit.createInventory(new ClanGUIHolder(),
                27, configString("gui.ranks.title", "Set Member Rank"));
        Clan clan = clanManager().getClan(clanName);
        if (clan == null || memberUUID.equals(clan.getClanLeaderUUID())
                || !clan.getClanMembersUUID().contains(memberUUID)) {
            setTargetAction(inventory, 22, Material.ARROW, "<gray>Back to Members",
                    "members_back", clanName, null);
            return inventory;
        }

        boolean isAdmin = clan.getClanAdminsUUID().contains(memberUUID);
        setTargetAction(inventory, 11, configuredMaterial("gui.ranks.admin.material", Material.GOLD_INGOT),
                configString("gui.ranks.admin.name", "<yellow>Admin"),
                "set_rank", clanName, memberUUID);
        setRankValue(inventory.getItem(11), "admin");
        setTargetAction(inventory, 15, configuredMaterial("gui.ranks.member.material", Material.IRON_INGOT),
                configString("gui.ranks.member.name", "<green>Member"),
                "set_rank", clanName, memberUUID);
        setRankValue(inventory.getItem(15), "default");
        setAction(inventory, 13, isAdmin ? Material.GOLD_INGOT : Material.IRON_INGOT,
                isAdmin ? "<gray>Current rank: Admin" : "<gray>Current rank: Member", "rank_info");
        setTargetAction(inventory, 22, Material.ARROW, "<gray>Back to Members",
                "members_back", clanName, null);
        return inventory;
    }

    public Inventory getConfirmationGui(String action, String clanName, UUID targetUUID) {
        Inventory inventory = Bukkit.createInventory(new ClanGUIHolder(),
                9, configString("gui.confirmation.title", "Confirm Clan Action"));
        ItemStack confirmItem = CreateItem.createItem(Material.LIME_CONCRETE, "<green>Confirm");
        NBT.modify(confirmItem, nbt -> {
            nbt.setString("action", action);
            nbt.setString("clan", clanName);
            if (targetUUID != null) {
                nbt.setString("member", targetUUID.toString());
            }
        });
        inventory.setItem(3, confirmItem);
        setAction(inventory, 5, Material.RED_CONCRETE, "<red>Cancel", "main_back");
        return inventory;
    }

    public Inventory getAdminClansGui(int page) {
        Inventory inventory = Bukkit.createInventory(new ClanGUIHolder(), 54, "Manage Clans");
        List<Clan> clans = clanManager().getClans();
        int pageCount = Math.max(1, (clans.size() + 44) / 45);
        int currentPage = Math.max(0, Math.min(page, pageCount - 1));
        int firstIndex = currentPage * 45;
        for (int index = firstIndex; index < Math.min(firstIndex + 45, clans.size()); index++) {
            Clan clan = clans.get(index);
            ItemStack item = CreateItem.createItem(Material.WHITE_BANNER,
                    "<gold>" + clan.getClanName() + " <gray>[" + clan.getClanTag() + "]");
            NBT.modify(item, nbt -> {
                nbt.setString("action", "admin_clan");
                nbt.setString("clan", clan.getClanName());
            });
            inventory.setItem(index - firstIndex, item);
        }
        setAction(inventory, 49, Material.ARROW, "<gray>Back", "admin_clans_back");
        if (currentPage > 0) {
            setPageAction(inventory, 45, "<yellow>Previous Page", currentPage - 1, "admin_page");
        }
        if (currentPage + 1 < pageCount) {
            setPageAction(inventory, 53, "<yellow>Next Page", currentPage + 1, "admin_page");
        }
        return inventory;
    }

    public Inventory getAdminClanActionsGui(String clanName) {
        Inventory inventory = Bukkit.createInventory(new ClanGUIHolder(), 27, "Manage Clan");
        setTargetAction(inventory, 11, Material.ANVIL, "<yellow>Rename Clan",
                "admin_rename", clanName, null);
        setTargetAction(inventory, 15, Material.TNT, "<red>Disband Clan",
                "confirm_admin_disband", clanName, null);
        setAction(inventory, 22, Material.ARROW, "<gray>Back to Clans", "admin_clans_back");
        return inventory;
    }

    private void setAction(Inventory inventory, int slot, Material material, String name, String action) {
        ItemStack item = CreateItem.createItem(material, name);
        NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setString("action", action));
        inventory.setItem(slot, item);
    }

    private void setTargetAction(Inventory inventory, int slot, Material material, String name,
                                 String action, String clanName, UUID memberUUID) {
        ItemStack item = CreateItem.createItem(material, name);
        NBT.modify(item, nbt -> {
            nbt.setString("action", action);
            nbt.setString("clan", clanName);
            if (memberUUID != null) {
                nbt.setString("member", memberUUID.toString());
            }
        });
        inventory.setItem(slot, item);
    }

    private void setPageAction(Inventory inventory, int slot, String name, int page, String action) {
        ItemStack item = CreateItem.createItem(Material.ARROW, name);
        NBT.modify(item, nbt -> {
            nbt.setString("action", action);
            nbt.setString("page", Integer.toString(page));
        });
        inventory.setItem(slot, item);
    }

    private void setRankValue(ItemStack item, String rank) {
        NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setString("rank", rank));
    }

    private void setConfiguredAction(Inventory inventory, int slot, Material fallbackMaterial,
                                     String path, String fallbackName, String action) {
        setAction(inventory, slot,
                configuredMaterial(path + ".material", fallbackMaterial),
                configString(path + ".name", fallbackName), action);
    }

    private Material configuredMaterial(String path, Material fallback) {
        String value = configString(path, fallback.name());
        Material material = Material.matchMaterial(value);
        if (material == null) {
            MopoClans.getINSTANCE().getLogger().warning(
                    "Invalid material in config at " + path + ": " + value + "; using " + fallback);
            return fallback;
        }
        return material;
    }

    private String configString(String path, String fallback) {
        return MopoClans.getConfigYML().getString(path, fallback);
    }

    private ClanManager clanManager() {
        return MopoClans.getCLANMANAGER();
    }

    private PlayerDataManager playerDataManager() {
        return MopoClans.getPLAYERDATAMANAGER();
    }
}
