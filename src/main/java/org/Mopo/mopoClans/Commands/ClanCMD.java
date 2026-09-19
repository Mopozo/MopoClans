package org.Mopo.mopoClans.Commands;

import mopo.mopoLib.tabComplete.TabComplete;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.Mopo.mopoClans.ClanData.ClanManager;
import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.PlayerData.PlayerData;
import org.Mopo.mopoClans.PlayerData.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClanCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        ClanManager clanManager = MopoClans.getCLANMANAGER();
        PlayerDataManager playerdatamanager = MopoClans.getPLAYERDATAMANAGER();

        if ((sender instanceof Player player)) {
            PlayerData playerData = playerdatamanager.getPlayerData(player.getUniqueId());
            if (args.length >= 1) {
                String arg1 = args[0];
                switch (arg1) {
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
                            }
                        } else if (args.length == 1) {
                            String clanName = playerData.getClanName();
                            clanManager.
                            if () {}
                        }
                    }
                    case "rename"  -> {
                        if (args.length == 2) {

                        }
                    }
                }
            }
        }


        return false;
    }
}
