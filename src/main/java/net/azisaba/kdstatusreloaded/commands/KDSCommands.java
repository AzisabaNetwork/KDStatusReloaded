package net.azisaba.kdstatusreloaded.commands;

import net.azisaba.kdstatusreloaded.KDStatusReloaded;
import net.azisaba.kdstatusreloaded.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class KDSCommands {
    public static void init(KDStatusReloaded plugin) {
        registerCommand("mystatus", cmd -> {
            cmd.setExecutor(new MyStatusCommand(plugin.getPlayerKd()));
            cmd.setPermissionMessage(Chat.f("&c権限がありません。運営に報告してください。"));
        });

        registerCommand("kdstatus", cmd -> {
            cmd.setExecutor(new KDStatusCommand(plugin));
            cmd.setPermissionMessage(Chat.f("&cこのコマンドを実行する権限がありません！"));
        });
    }

    private static void registerCommand(String name, Consumer<PluginCommand> commandConsumer) {
        PluginCommand cmd = Bukkit.getPluginCommand(name);
        if(cmd == null) throw new RuntimeException("Failed to get command " + name);
        commandConsumer.accept(cmd);
    }
}
