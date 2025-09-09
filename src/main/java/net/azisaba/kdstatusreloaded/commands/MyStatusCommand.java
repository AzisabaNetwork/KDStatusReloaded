package net.azisaba.kdstatusreloaded.commands;

import com.google.common.base.Strings;
import net.azisaba.kdstatusreloaded.api.KDUserData;
import net.azisaba.kdstatusreloaded.playerkd.PlayerKD;
import net.azisaba.kdstatusreloaded.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.StringJoiner;

public class MyStatusCommand implements CommandExecutor {
    private static final String arrow = "➣";

    private final PlayerKD playerKd;

    public MyStatusCommand(PlayerKD playerKd) {
        this.playerKd = playerKd;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Chat.f("&cこのコマンドはプレイヤーのみ有効です！"));
            return true;
        }

        Player p = (Player) sender;
        KDUserData data = playerKd.getPlayerData(p.getUniqueId());

        int kills = data.totalKills;
        int deaths = data.deaths;
        double kdRaito;
        if (deaths > 0) {
            kdRaito = (double) kills / (double) deaths;
        } else {
            kdRaito = kills;
        }

        int dailyKills = data.dailyKills;
        int monthlyKills = data.monthlyKills;
        int yearlyKills = data.yearlyKills;

        StringJoiner builder = new StringJoiner("\n");

        builder.add(Chat.f("&b&m&l{0}&c&l[{1}の戦績]&b&m&l{0}&r", Strings.repeat("━", 18), p.getName())).add(" ");
        builder.add(Chat.f("&9{0}&eキル数&a: &b{1}&r", arrow, kills));
        builder.add(Chat.f("&9{0}&eデス数&a: &b{1}&r", arrow, deaths)).add(" ");
        builder.add(Chat.f("&9{0}&eK/D&a: &b{1}&r", arrow, String.format("%.3f", kdRaito))).add(" ");
        builder.add(Chat.f("&9{0}&eDailyキル数&a: &b{1}&r", arrow, dailyKills));
        builder.add(Chat.f("&9{0}&eMonthlyキル数&a: &b{1}&r", arrow, monthlyKills));
        builder.add(Chat.f("&9{0}&eYearlyキル数&a: &b{1}&r", arrow, yearlyKills)).add(" ");
        builder.add(Chat.f("&b&m&l{0}", Strings.repeat("━", 52)));

        p.sendMessage(builder.toString());
        return true;
    }
}
