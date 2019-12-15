package jp.azisaba.lgw.kdstatus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.RequiredArgsConstructor;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;
import jp.azisaba.lgw.kdstatus.utils.Chat;

@RequiredArgsConstructor
public class KDStatusCommand implements CommandExecutor {

    private final KDStatusReloaded plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ( args.length <= 0 ) {
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

        if ( args[0].equalsIgnoreCase("miguration") ) {
            plugin.getKdDataContainer().miguration((Player) sender);
            return true;
        }

        sender.sendMessage(Chat.f("&cUsage: {0}", cmd.getUsage().replace("{LABEL}", label)));
        return true;
    }
}
