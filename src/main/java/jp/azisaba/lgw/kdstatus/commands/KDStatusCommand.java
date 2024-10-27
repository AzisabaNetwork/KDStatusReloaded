package jp.azisaba.lgw.kdstatus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;
import jp.azisaba.lgw.kdstatus.utils.Chat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KDStatusCommand implements CommandExecutor, TabCompleter {
    private static final List<String> modes = Arrays.asList("reload", "migration", "reconnect-db");

    private final KDStatusReloaded plugin;

    public KDStatusCommand(KDStatusReloaded plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Chat.f("&cUsage: {0}", cmd.getUsage().replace("{LABEL}", label)));
            return true;
        }

        if ( args[0].equalsIgnoreCase("reload") ) {
            long start = System.currentTimeMillis();
            plugin.reloadPluginConfig();
            long finish = System.currentTimeMillis();

            sender.sendMessage(Chat.f("&aConfigを再読み込みしました！ &b({0}ms)", finish - start));
            return true;
        }

        if ( args[0].equalsIgnoreCase("migration") ) {
            plugin.getKdDataContainer().migrationToMySQL((Player) sender);
            return true;
        }

        if ( args[0].equalsIgnoreCase("reconnect-db") ) {
            sender.sendMessage("Reconnecting database...");
            plugin.sql.reconnect();
            sender.sendMessage("Reconnect was finished.");
            sender.sendMessage("Auto-connection test is running...");
            boolean alive = plugin.sql.isConnectionAlive();
            sender.sendMessage("Result: Connection is " + (alive ? ChatColor.GREEN + "alive" : ChatColor.RED + "dead"));
            return true;
        }

        sender.sendMessage(Chat.f("&cUsage: {0}", cmd.getUsage().replace("{LABEL}", label)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if(args.length == 1) {
            return modes;
        }
        return Collections.emptyList();
    }
}
