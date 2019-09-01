package jp.azisaba.lgw.kdstatus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import lombok.RequiredArgsConstructor;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;

@RequiredArgsConstructor
public class KDStatusCommand implements CommandExecutor {

    private final KDStatusReloaded plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ( args.length <= 0 ) {
            sender.sendMessage(ChatColor.RED + "Usage: " + cmd.getUsage().replace("{LABEL}", label));
            return true;
        }

        if ( args[0].equalsIgnoreCase("reload") ) {
            long start = System.currentTimeMillis();
            plugin.reloadPluginConfig();
            long finish = System.currentTimeMillis();

            sender.sendMessage(ChatColor.GREEN + "Configを再読み込みしました！ " + ChatColor.AQUA + "(" + (finish - start) + "ms)");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: " + cmd.getUsage().replace("{LABEL}", label));
        return true;
    }
}
