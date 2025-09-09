package net.azisaba.kdstatusreloaded.commands;

import net.azisaba.kdstatusreloaded.KDStatusReloaded;
import net.azisaba.kdstatusreloaded.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NullMarked
public class KDStatusCommand implements CommandExecutor, TabCompleter {
    private static final List<String> modes = Arrays.asList("check", "migration");

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

        if (args[0].equalsIgnoreCase("migration")) {
            plugin.getPlayerKd().migrate();
            sender.sendMessage("Migrated.");
            return true;
        }

        if(args[0].equalsIgnoreCase("check")) {
            var worldConfig = plugin.getPluginConfig().world;
            sender.sendMessage("- ShowCountCancelled: " + worldConfig.showCountCancelled);
            sender.sendMessage("->>> disables <<<-");
            sender.sendMessage("Kill: " + String.join(", ", worldConfig.disableKillWorldList));
            sender.sendMessage("Death: " + String.join(", ", worldConfig.disableDeathWorldList));
            sender.sendMessage("------------------");
        }

        sender.sendMessage(Chat.f("&cUsage: {0}", cmd.getUsage().replace("{LABEL}", label)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return modes;
        }
        return Collections.emptyList();
    }
}
