package org.Mopo.mopoClans.Commands;

import mopo.mopoLib.tabComplete.TabComplete;
import org.Mopo.mopoClans.ClanData.ClanManager;
import org.Mopo.mopoClans.MopoClans;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClanCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        ClanManager clanmanager = MopoClans.getCLANMANAGER();

        if ((sender instanceof Player player)) {
            if (args.length >= 1) {
                String arg1 = args[0];
                switch (arg1) {
                    case "create" -> {}
                }
            }
        }


        return false;
    }
}
