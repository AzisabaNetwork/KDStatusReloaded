package jp.azisaba.lgw.kdstatus.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Strings;

import net.md_5.bungee.api.ChatColor;

import lombok.RequiredArgsConstructor;

import jp.azisaba.lgw.kdstatus.KDUserData;
import jp.azisaba.lgw.kdstatus.KillDeathDataContainer;
import jp.azisaba.lgw.kdstatus.KillDeathDataContainer.TimeUnit;

@RequiredArgsConstructor
public class MyStatusCommand implements CommandExecutor {

    private final KillDeathDataContainer dataContainer;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if ( sender instanceof Player ) {
            Player p = (Player) sender;
            KDUserData data = dataContainer.getPlayerData(p, true);

            StringBuilder builder = new StringBuilder();

            builder.append(ChatColor.GREEN + Strings.repeat("=", 7) + ChatColor.RED + "[" + ChatColor.YELLOW
                    + "Status" + ChatColor.RED + "]" + ChatColor.GREEN + Strings.repeat("=", 7) + "\n");

            builder.append(
                    ChatColor.RED + "Kills" + ChatColor.GREEN + ": " + ChatColor.YELLOW + data.getKills() + "\n");
            builder.append(
                    ChatColor.RED + "Deaths" + ChatColor.GREEN + ": " + ChatColor.YELLOW + data.getDeaths() + "\n");
            builder.append(ChatColor.RED + "Daily Kills" + ChatColor.GREEN + ": " + ChatColor.YELLOW
                    + data.getKills(TimeUnit.DAILY) + "\n");

            double kd = (double) data.getKills() / (double) data.getDeaths();

            builder.append(ChatColor.RED + "K/D" + ChatColor.GREEN + ": " + ChatColor.YELLOW
                    + String.format("%.2f", kd) + "\n");

            builder.append(ChatColor.GREEN + StringUtils.repeat("=", 21));

            p.sendMessage(builder.toString());
        }
        return true;
    }
}
