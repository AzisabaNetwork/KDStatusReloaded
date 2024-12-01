package jp.azisaba.lgw.kdstatus.commands;

import com.google.common.base.Strings;
import jp.azisaba.lgw.kdstatus.sql.KDUserData;
import jp.azisaba.lgw.kdstatus.sql.KillDeathDataContainer;
import jp.azisaba.lgw.kdstatus.utils.Chat;
import jp.azisaba.lgw.kdstatus.utils.TimeUnit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyStatusCommand implements CommandExecutor {

    private final KillDeathDataContainer dataContainer;

    private final String arrow = "➣";

    public MyStatusCommand(KillDeathDataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Chat.f("&cこのコマンドはプレイヤーのみ有効です！"));
            return true;
        }

        Player p = (Player) sender;
        KDUserData data = dataContainer.getPlayerData(p, true);

        int kills = data.getKills(TimeUnit.LIFETIME);
        int deaths = data.getDeaths();
        double kdRaito;
        if (deaths > 0) {
            kdRaito = (double) kills / (double) deaths;
        } else {
            kdRaito = (double) kills;
        }

        int dailyKills = data.getKills(TimeUnit.DAILY);
        int monthlyKills = data.getKills(TimeUnit.MONTHLY);
        int yearlyKills = data.getKills(TimeUnit.YEARLY);

        StringBuilder builder = new StringBuilder();

        builder.append(Chat.f("&b&m&l{0}&c&l[{1}の戦績]&b&m&l{0}&r", Strings.repeat("━", 18), p.getName())).append("\n \n");
        builder.append(Chat.f("&9{0}&eキル数&a: &b{1}&r", arrow, kills)).append("\n");
        builder.append(Chat.f("&9{0}&eデス数&a: &b{1}&r", arrow, deaths)).append("\n \n");
        builder.append(Chat.f("&9{0}&eK/D&a: &b{1}&r", arrow, String.format("%.3f", kdRaito))).append("\n \n");
        builder.append(Chat.f("&9{0}&eDailyキル数&a: &b{1}&r", arrow, dailyKills)).append("\n");
        builder.append(Chat.f("&9{0}&eMonthlyキル数&a: &b{1}&r", arrow, monthlyKills)).append("\n");
        builder.append(Chat.f("&9{0}&eYearlyキル数&a: &b{1}&r", arrow, yearlyKills)).append("\n \n");
        builder.append(Chat.f("&b&m&l{0}", Strings.repeat("━", 52)));

        p.sendMessage(builder.toString());
        return true;
    }
}
