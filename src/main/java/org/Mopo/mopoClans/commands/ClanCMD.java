package org.Mopo.mopoClans.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.Mopo.mopoClans.clandata.Clan;
import org.Mopo.mopoClans.clandata.ClanManager;
import org.Mopo.mopoClans.inventories.ClanGUI;
import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.playerdata.PlayerData;
import org.Mopo.mopoClans.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ClanCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        ClanManager clanManager = MopoClans.getCLANMANAGER();
        PlayerDataManager playerdatamanager = MopoClans.getPLAYERDATAMANAGER();
        ClanGUI clanGUI = new ClanGUI();

        if ((sender instanceof Player player)) {
            PlayerData playerData = playerdatamanager.getPlayerData(player.getUniqueId());

            if (args.length == 0) {
                player.openInventory(clanGUI.getClanGui(player.getUniqueId()));
                return true;
            }

            if (args.length >= 1) {
                String arg1 = args[0];
                switch (arg1) { //todo lowercase
                    case "create" -> {
                            if (args.length == 3) {
                                String clanName = args[1];
                                String clanTag = args[2];
                                clanManager.createClan(clanName,clanTag,player.getUniqueId());
                            } else {
                                player.sendMessage(MiniMessage.miniMessage().deserialize("<red> Correct usage: /clan create <clanName> <clanTag>"));
                            }
                    }
                    case "disband" -> {
                        if (args.length == 2) {
                            if (playerData.isAdmin()) {
                                String clanName = args[1];
                                clanManager.deleteClan(clanName);
                                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Clan successfully disbanded!"));
                            }
                        } else if (args.length == 1) {
                            String clanName = playerData.getClanName();
                            if (clanManager.getClan(clanName).getClanLeaderUUID().equals(player.getUniqueId())) {
                                clanManager.deleteClan(clanName);
                                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Clan successfully disbanded!"));
                            }
                        }
                    }
                    case "rename"  -> {
                        if (args.length >= 2) {
                            if (args.length == 3) {
                                if (playerData.isAdmin()) {
                                    String targetClanName = args[1];
                                    String newClanName = args[2];
                                    clanManager.renameClan(targetClanName, newClanName);
                                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Clan " + targetClanName + " is now clan " + newClanName ));
                                }
                            } else if (args.length == 2) {
                                String clanName = playerData.getClanName();
                                if (clanManager.getClan(clanName).getClanLeaderUUID().equals(player.getUniqueId())) {
                                    String targetClanName = playerData.getClanName();
                                    String newClanName = args[1];
                                    clanManager.renameClan(targetClanName, newClanName);
                                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>You are now clan " + newClanName));
                                }
                            }
                        }
                    }
                    case "inbox" -> {
                        player.openInventory(clanGUI.getInvitationsGui(player.getUniqueId()));
                    }
                    case "invite" -> {
                        if (args.length >= 2) {
                            String targetUser = args[1];
                            Player targetPlayer = Bukkit.getPlayer(targetUser);
                            if (targetPlayer != null) {
                                if (playerData.getClanRank().equals("admin") || playerData.getClanRank().equals("leader")) {
                                    clanManager.sendClanInvite(targetUser, playerData.getClanName());
                                }
                            }
                        }
                    }
                    case "accept" -> {
                        if (args.length >= 2) {
                            String targetClan = args[1];
                            if (playerData.getIncomingClanInvites().contains(targetClan)) {
                                clanManager.addClanMember(player.getUniqueId(), targetClan );
                            } else {
                                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No incoming invites from clan:" + targetClan));
                            }
                        }

                    }
                    case "deny" -> {
                        if (args.length >= 2) {
                            String targetClan = args[1];
                            if (playerData.getIncomingClanInvites().contains(targetClan)) {
                                clanManager.removeClanInvite(player.getName(), targetClan);
                            } else {
                                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No incoming invites from clan:" + targetClan));
                            }
                        }
                    }
                    case "promote" -> {
                        if (args.length >= 2) {
                            String targetplayer = args[1];
                            String targetClan = playerData.getClanName();
                            Clan clan = clanManager.getClan(targetClan);
                            if (targetClan != null) {
                                if (playerData.getClanRank().equals("admin") || clan.getClanLeaderUUID().equals(player.getUniqueId())) {
                                    clanManager.promoteClanMember(targetplayer, targetClan);
                                }
                            }
                        }
                    }
                    case "transfer" -> {
                        if (args.length >= 2) {
                            String targetPlayer = args[1];
                            Player player1 = Bukkit.getPlayer(targetPlayer);
                            if (player1 != null) {
                                UUID targetPlayerUUID = player1.getUniqueId();
                                PlayerData targetPlayerData = playerdatamanager.getPlayerData(targetPlayerUUID);
                                String clanName = targetPlayerData.getClanName();
                                if (playerData.getClanName().equals(clanName) && playerData.getClanRank().equals("leader")) {
                                    clanManager.setClanOwner(targetPlayer, clanName);
                                }
                            }
                        }
                    }
                }
            }
        }


        return sender instanceof Player;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player) || args.length == 0) {
            return List.of();
        }

        PlayerData playerData = MopoClans.getPLAYERDATAMANAGER().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            return List.of();
        }

        List<String> candidates = new ArrayList<>();
        if (args.length == 1) {
            if (playerData.getClanName() == null) {
                candidates.add("create");
                if (!playerData.getIncomingClanInvites().isEmpty()) {
                    candidates.add("accept");
                }
            }
            candidates.add("inbox");
            if (!playerData.getIncomingClanInvites().isEmpty()) {
                candidates.add("deny");
            }
            if (playerData.getClanName() != null) {
                Clan clan = MopoClans.getCLANMANAGER().getClan(playerData.getClanName());
                if (clan != null) {
                    boolean isLeader = player.getUniqueId().equals(clan.getClanLeaderUUID());
                    boolean canManage = isLeader || "admin".equals(playerData.getClanRank());
                    if (canManage) {
                        candidates.add("invite");
                        candidates.add("promote");
                    }
                    if (isLeader) {
                        candidates.add("rename");
                        candidates.add("disband");
                        candidates.add("transfer");
                    }
                }
            }
            if (playerData.isAdmin()) {
                candidates.add("disband");
                candidates.add("rename");
            }
            return matching(args[0], candidates);
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "invite" -> {
                if (args.length == 2 && canManageClan(player, playerData)) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (!onlinePlayer.equals(player)) {
                            PlayerData targetData = MopoClans.getPLAYERDATAMANAGER()
                                    .getPlayerData(onlinePlayer.getUniqueId());
                            if (targetData != null && targetData.getClanName() == null) {
                                candidates.add(onlinePlayer.getName());
                            }
                        }
                    }
                }
            }
            case "accept", "deny" -> {
                if (args.length == 2) {
                    for (String clanName : playerData.getIncomingClanInvites()) {
                        if (MopoClans.getCLANMANAGER().getClan(clanName) != null
                                && ("deny".equalsIgnoreCase(args[0]) || playerData.getClanName() == null)) {
                            candidates.add(clanName);
                        }
                    }
                }
            }
            case "disband" -> {
                if (args.length == 2 && playerData.isAdmin()) {
                    for (Clan clan : MopoClans.getCLANMANAGER().getClans()) {
                        candidates.add(clan.getClanName());
                    }
                }
            }
            case "promote", "transfer" -> {
                if (args.length == 2 && playerData.getClanName() != null) {
                    Clan clan = MopoClans.getCLANMANAGER().getClan(playerData.getClanName());
                    boolean isTransfer = "transfer".equalsIgnoreCase(args[0]);
                    boolean authorized = clan != null && (isTransfer
                            ? player.getUniqueId().equals(clan.getClanLeaderUUID())
                            : canManageClan(player, playerData));
                    if (authorized) {
                        for (UUID memberUUID : clan.getClanMembersUUID()) {
                            Player member = Bukkit.getPlayer(memberUUID);
                            if (member == null || member.equals(player)) {
                                continue;
                            }
                            if (isTransfer
                                    || (!memberUUID.equals(clan.getClanLeaderUUID())
                                    && !clan.getClanAdminsUUID().contains(memberUUID))) {
                                candidates.add(member.getName());
                            }
                        }
                    }
                }
            }
            default -> { }
        }
        return matching(args[args.length - 1], candidates);
    }

    private boolean canManageClan(Player player, PlayerData playerData) {
        Clan clan = MopoClans.getCLANMANAGER().getClan(playerData.getClanName());
        return clan != null && (player.getUniqueId().equals(clan.getClanLeaderUUID())
                || "admin".equals(playerData.getClanRank()));
    }

    private List<String> matching(String input, List<String> candidates) {
        List<String> matches = new ArrayList<>();
        StringUtil.copyPartialMatches(input, candidates, matches);
        matches.sort(Comparator.comparing(value -> value.toLowerCase(Locale.ROOT)));
        return matches;
    }
}
