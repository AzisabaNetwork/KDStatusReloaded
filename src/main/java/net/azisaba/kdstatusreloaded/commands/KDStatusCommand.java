package net.azisaba.kdstatusreloaded.commands;

import net.azisaba.kdstatusreloaded.KDStatusReloaded;
import net.azisaba.kdstatusreloaded.api.KillCountType;
import net.azisaba.kdstatusreloaded.playerkd.model.KDUserData;
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
    private static final List<String> modes = Arrays.asList("check", "migrate", "flush-all", "show-ranking");

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

        String mode = args[0].toLowerCase();
        switch (mode) {
            case "check" -> {
                var worldConfig = plugin.getPluginConfig().world;
                sender.sendMessage("- ShowCountCancelled: " + worldConfig.showCountCancelled);
                sender.sendMessage("->>> disables <<<-");
                sender.sendMessage("Kill: " + String.join(", ", worldConfig.disableKillWorldList));
                sender.sendMessage("Death: " + String.join(", ", worldConfig.disableDeathWorldList));
                sender.sendMessage("------------------");
                return true;
            }
            case "flush-all" -> {
                plugin.getPlayerKd().flushAll();
            }
            case "migrate" -> {
                plugin.getPlayerKd().migrate();
                sender.sendMessage("Migrated.");
                return true;
            }
            case "show-ranking" -> {
                if(args.length != 2) {
                    sender.sendMessage(String.format("/%s show-ranking <type>", label));
                    return true;
                }
                KillCountType killCountType = KillCountType.valueOf(args[1].toUpperCase());
                List<KDUserData> topList = plugin.getPlayerKd().getTops(killCountType, 7);
                sender.sendMessage("======= Ranking =======");
                for (int i = 0; i < 7; i++) {
                    if(topList.size() <= i) {
                        sender.sendMessage(String.format("%d: なし", i+1));
                    } else {
                        KDUserData kdUserData = topList.get(i);
                        sender.sendMessage(String.format("%d: %s", i + 1, kdUserData.name));
                    }
                }
                sender.sendMessage("=======================");
            }
        }

        sender.sendMessage(Chat.f("&cUsage: {0}", cmd.getUsage().replace("{LABEL}", label)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return modes;
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("show-ranking")) {
                return Arrays.stream(KillCountType.values()).map(t -> t.name().toLowerCase()).toList();
            }
        }
        return Collections.emptyList();
    }
}
