package org.Mopo.mopoClans.listeners;

import mopo.mopoLib.nbt.NBT;
import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.clandata.Clan;
import org.Mopo.mopoClans.clandata.ClanManager;
import org.Mopo.mopoClans.inventories.ClanGUI;
import org.Mopo.mopoClans.inventories.ClanGUIHolder;
import org.Mopo.mopoClans.playerdata.PlayerData;
import org.Mopo.mopoClans.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClanListener implements Listener {

    private final ClanGUI clanGUI = new ClanGUI();
    private final Map<UUID, PendingInput> pendingInputs = new ConcurrentHashMap<>();

    @EventHandler
    public void playerClickEvent(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        if (!(topInventory.getHolder() instanceof ClanGUIHolder)) {
            return;
        }

        event.setCancelled(true);
        if (event.getClickedInventory() != topInventory) {
            return;
        }
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        String action = NBT.get(item, nbt -> nbt.hasTag("action") ? nbt.getString("action") : null);
        if (action == null || action.isBlank()) {
            return;
        }
        String clanName = NBT.get(item, nbt -> nbt.hasTag("clan") ? nbt.getString("clan") : null);
        String memberValue = NBT.get(item, nbt -> nbt.hasTag("member") ? nbt.getString("member") : null);
        UUID memberUUID = parseUuid(memberValue);

        ClanManager clanManager = MopoClans.getCLANMANAGER();
        PlayerDataManager playerDataManager = MopoClans.getPLAYERDATAMANAGER();
        PlayerData playerData = playerDataManager.getPlayerData(player.getUniqueId());
        switch (action) {
            case "close" -> player.closeInventory();
            case "main_back" -> player.openInventory(clanGUI.getClanGui(player.getUniqueId()));
            case "members_back" -> {
                if (clanName != null) {
                    player.openInventory(clanGUI.getMembersGui(clanName));
                }
            }
            case "invite_page" -> {
                String page = NBT.get(item, nbt -> nbt.hasTag("page") ? nbt.getString("page") : "0");
                player.openInventory(clanGUI.getInvitationsGui(player.getUniqueId(), parsePage(page)));
            }
            case "create" -> prompt(player, "create", null, "Enter a clan name and tag, separated by a space.");
            case "rename" -> {
                if (playerData != null && playerData.getClanName() != null
                        && ("leader".equals(playerData.getClanRank()) || playerData.isAdmin())) {
                    prompt(player, "rename", playerData.getClanName(), "Enter the new clan name.");
                } else {
                    player.sendMessage("Only the clan leader can rename the clan.");
                }
            }
            case "invite" -> {
                if (playerData != null && ("leader".equals(playerData.getClanRank())
                        || "admin".equals(playerData.getClanRank()))) {
                    prompt(player, "invite", playerData.getClanName(), "Enter the online player's name to invite.");
                } else {
                    player.sendMessage("Only clan leaders and admins can invite players.");
                }
            }
            case "inbox" -> player.openInventory(clanGUI.getInvitationsGui(player.getUniqueId()));
            case "members" -> {
                if (playerData != null && playerData.getClanName() != null) {
                    player.openInventory(clanGUI.getMembersGui(playerData.getClanName()));
                }
            }
            case "member_options" -> {
                if (clanName != null && memberUUID != null) {
                    player.openInventory(clanGUI.getMemberActionsGui(clanName, memberUUID, player.getUniqueId()));
                }
            }
            case "manage_rank" -> {
                if (clanName != null && memberUUID != null && isClanOwner(player.getUniqueId(), clanName)) {
                    player.openInventory(clanGUI.getMemberRankGui(clanName, memberUUID));
                }
            }
            case "set_rank" -> {
                if (clanName != null && memberUUID != null && isClanOwner(player.getUniqueId(), clanName)) {
                    String rank = NBT.get(item, nbt -> nbt.hasTag("rank") ? nbt.getString("rank") : null);
                    if (rank != null && clanManager.setClanMemberRank(memberUUID, clanName, rank)) {
                        player.sendMessage("Member rank set to " + rank + ".");
                        player.openInventory(clanGUI.getMemberRankGui(clanName, memberUUID));
                    } else {
                        player.sendMessage("Could not update that member's rank.");
                    }
                } else {
                    player.sendMessage("Only the clan owner can change member ranks.");
                }
            }
            case "clan_invite" -> {
                if (clanName != null) {
                    if (event.isLeftClick()) {
                        if (clanManager.addClanMember(player.getUniqueId(), clanName)) {
                            player.sendMessage("You joined clan " + clanName + ".");
                        } else {
                            player.sendMessage("Could not join that clan. It may be full, unavailable, or you may already be in a clan.");
                        }
                    } else if (event.isRightClick()) {
                        clanManager.removeClanInvite(player.getUniqueId(), clanName);
                        player.sendMessage("Clan invitation declined.");
                    } else {
                        return;
                    }
                    player.openInventory(clanGUI.getInvitationsGui(player.getUniqueId()));
                }
            }
            case "promote" -> {
                if (clanName != null && memberUUID != null && canManageMembers(playerData, clanName)) {
                    clanManager.promoteClanMember(memberUUID, clanName);
                    player.sendMessage("Member promoted to clan admin.");
                    player.openInventory(clanGUI.getMembersGui(clanName));
                }
            }
            case "confirm_disband" -> {
                if (playerData != null && playerData.getClanName() != null) {
                    player.openInventory(clanGUI.getConfirmationGui("disband", playerData.getClanName(), null));
                }
            }
            case "confirm_leave" -> {
                if (playerData != null && playerData.getClanName() != null) {
                    player.openInventory(clanGUI.getConfirmationGui("leave", playerData.getClanName(), null));
                }
            }
            case "confirm_transfer" -> {
                if (clanName != null && memberUUID != null) {
                    player.openInventory(clanGUI.getConfirmationGui("transfer", clanName, memberUUID));
                }
            }
            case "disband" -> {
                if (clanName != null && canDisband(playerData, clanName)) {
                    clanManager.deleteClan(clanName);
                    player.sendMessage("Clan successfully disbanded.");
                    player.openInventory(clanGUI.getClanGui(player.getUniqueId()));
                }
            }
            case "leave" -> {
                if (playerData != null && clanName != null) {
                    Clan clan = clanManager.getClan(clanName);
                    if (clan != null && player.getUniqueId().equals(clan.getClanLeaderUUID())) {
                        player.sendMessage("Transfer leadership before leaving your clan.");
                    } else {
                        clanManager.removeClanMember(player.getUniqueId());
                        player.sendMessage("You left your clan.");
                    }
                    player.openInventory(clanGUI.getClanGui(player.getUniqueId()));
                }
            }
            case "transfer" -> {
                if (clanName != null && memberUUID != null) {
                    Clan clan = clanManager.getClan(clanName);
                    if (clan != null && player.getUniqueId().equals(clan.getClanLeaderUUID())) {
                        clanManager.setClanOwner(memberUUID, clanName);
                        player.sendMessage("Clan leadership transferred.");
                    }
                    player.openInventory(clanGUI.getMembersGui(clanName));
                }
            }
            case "admin_clans", "admin_clans_back" -> {
                if (isServerAdmin(playerData)) {
                    player.openInventory(clanGUI.getAdminClansGui(0));
                }
            }
            case "admin_page" -> {
                if (isServerAdmin(playerData)) {
                    String page = NBT.get(item, nbt -> nbt.hasTag("page") ? nbt.getString("page") : "0");
                    player.openInventory(clanGUI.getAdminClansGui(parsePage(page)));
                }
            }
            case "admin_clan" -> {
                if (isServerAdmin(playerData) && clanName != null) {
                    player.openInventory(clanGUI.getAdminClanActionsGui(clanName));
                }
            }
            case "admin_rename" -> {
                if (isServerAdmin(playerData) && clanName != null) {
                    prompt(player, "admin_rename", clanName, "Enter the new clan name.");
                }
            }
            case "confirm_admin_disband" -> {
                if (isServerAdmin(playerData) && clanName != null) {
                    player.openInventory(clanGUI.getConfirmationGui("admin_disband", clanName, null));
                }
            }
            case "admin_disband" -> {
                if (isServerAdmin(playerData) && clanName != null) {
                    clanManager.deleteClan(clanName);
                    player.sendMessage("Clan successfully disbanded.");
                    player.openInventory(clanGUI.getAdminClansGui(0));
                }
            }
            default -> { }
        }
    }

    @EventHandler
    public void onChatInput(AsyncPlayerChatEvent event) {
        PendingInput pending = pendingInputs.remove(event.getPlayer().getUniqueId());
        if (pending == null) {
            return;
        }
        event.setCancelled(true);
        String input = event.getMessage().trim();
        Bukkit.getScheduler().runTask(MopoClans.getINSTANCE(),
                () -> handleInput(event.getPlayer(), pending, input));
    }

    private void handleInput(Player player, PendingInput pending, String input) {
        if (input.equalsIgnoreCase("cancel")) {
            player.sendMessage("Clan action cancelled.");
            return;
        }
        ClanManager clanManager = MopoClans.getCLANMANAGER();
        try {
            switch (pending.action()) {
                case "create" -> {
                    String[] values = input.split("\\s+", 2);
                    if (values.length != 2) {
                        player.sendMessage("Enter both a clan name and tag, or type cancel.");
                        prompt(player, "create", null, "Enter a clan name and tag, separated by a space.");
                        return;
                    }
                    clanManager.createClan(values[0], values[1], player.getUniqueId());
                    player.sendMessage("Clan created.");
                }
                case "rename" -> {
                    PlayerData data = MopoClans.getPLAYERDATAMANAGER().getPlayerData(player.getUniqueId());
                    Clan clan = clanManager.getClan(pending.clanName());
                    if (data != null && clan != null && (data.isAdmin()
                            || player.getUniqueId().equals(clan.getClanLeaderUUID()))) {
                        clanManager.renameClan(pending.clanName(), input);
                        player.sendMessage("Your clan was renamed.");
                    } else {
                        player.sendMessage("You are not allowed to rename that clan.");
                    }
                }
                case "admin_rename" -> {
                    PlayerData data = MopoClans.getPLAYERDATAMANAGER().getPlayerData(player.getUniqueId());
                    if (isServerAdmin(data)) {
                        clanManager.renameClan(pending.clanName(), input);
                        player.sendMessage("Clan renamed.");
                    }
                }
                case "invite" -> {
                    PlayerData data = MopoClans.getPLAYERDATAMANAGER().getPlayerData(player.getUniqueId());
                    if (data != null && data.getClanName() != null
                            && ("leader".equals(data.getClanRank()) || "admin".equals(data.getClanRank()))) {
                        Player target = Bukkit.getPlayerExact(input);
                        if (target == null) {
                            player.sendMessage("That player is not online.");
                        } else {
                            clanManager.sendClanInvite(target.getName(), data.getClanName());
                        }
                    }
                }
                default -> { }
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            player.sendMessage(exception.getMessage());
        }
    }

    private void prompt(Player player, String action, String clanName, String message) {
        pendingInputs.put(player.getUniqueId(), new PendingInput(action, clanName));
        player.closeInventory();
        player.sendMessage(message + " Type cancel to stop.");
    }

    private boolean isServerAdmin(PlayerData playerData) {
        return playerData != null && playerData.isAdmin();
    }

    private boolean isClanOwner(UUID playerUUID, String clanName) {
        Clan clan = MopoClans.getCLANMANAGER().getClan(clanName);
        return clan != null && playerUUID.equals(clan.getClanLeaderUUID());
    }

    private boolean canManageMembers(PlayerData playerData, String clanName) {
        Clan clan = MopoClans.getCLANMANAGER().getClan(clanName);
        return playerData != null && clan != null
                && (playerData.isAdmin() || "admin".equals(playerData.getClanRank())
                || playerData.getUuid().equals(clan.getClanLeaderUUID()));
    }

    private boolean canDisband(PlayerData playerData, String clanName) {
        Clan clan = MopoClans.getCLANMANAGER().getClan(clanName);
        return playerData != null && clan != null
                && (playerData.isAdmin() || playerData.getUuid().equals(clan.getClanLeaderUUID()));
    }

    private UUID parseUuid(String value) {
        if (value == null) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private int parsePage(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private record PendingInput(String action, String clanName) { }
}
