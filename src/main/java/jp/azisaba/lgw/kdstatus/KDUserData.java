package jp.azisaba.lgw.kdstatus;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KDUserData {

	private final Player p;

	private YamlConfiguration conf;
	private File file;

	public static final String KILLS_KEY = "Kills";
	public static final String DEATHS_KEY = "Deaths";
	public static final String LAST_KILL_KEY = "LastKill";
	public static final String LAST_NAME_KEY = "LastName";
	public static final String DAILY_KILLS_KEY = "DailyKill";
	public static final String MONTHLY_KILLS_KEY = "MonthlyKill";

	public KDUserData(Player p) {
		this.p = p;
		loadFile();
	}

	public Player getPlayer() {
		return p;
	}

	public void addKill(int num) {

		if (conf.get(KILLS_KEY) == null) {
			conf.set(KILLS_KEY, num);
			return;
		}

		conf.set(KILLS_KEY, conf.getInt(KILLS_KEY) + num);

		addDailyKill(num);
		addMonthlyKill(num);

		updateLastKillLong();

		ItemStack emerald = new ItemStack(Material.EMERALD, KDManager.getPlugin().config.emeraldAmount);

		if (KDManager.getPlugin().config.emeraldAmount > 0) {
			p.getInventory().addItem(emerald);
			KDManager.getPlugin().getLogger().info("Add " + emerald.getAmount() + " emerald(s) for " + p.getName());
		}
	}

	public void addDeath(int num) {
		if (conf.get(DEATHS_KEY) == null) {
			conf.set(DEATHS_KEY, num);
			return;
		}

		conf.set(DEATHS_KEY, conf.getInt(DEATHS_KEY) + num);
	}

	public long getLastKillLong() {
		return conf.getLong(LAST_KILL_KEY, -1);
	}

	public void updateLastKillLong() {
		conf.set(LAST_KILL_KEY, System.currentTimeMillis());
	}

	public void addDailyKill(int num) {
		Calendar cal = Calendar.getInstance();
		Calendar now = Calendar.getInstance();

		Date date = new Date(getLastKillLong());

		cal.setTime(date);

		if (cal.get(Calendar.DATE) != now.get(Calendar.DATE) || cal.get(Calendar.MONTH) != now.get(Calendar.MONTH)
				|| cal.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
			conf.set(DAILY_KILLS_KEY, num);
		} else {
			conf.set(DAILY_KILLS_KEY, conf.getInt(DAILY_KILLS_KEY, 0) + num);
		}
	}

	public void addMonthlyKill(int num) {
		Calendar cal = Calendar.getInstance();
		Calendar now = Calendar.getInstance();

		Date date = new Date(getLastKillLong());

		cal.setTime(date);

		if (cal.get(Calendar.MONTH) != now.get(Calendar.MONTH) || cal.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
			conf.set(MONTHLY_KILLS_KEY, num);
		} else {
			conf.set(MONTHLY_KILLS_KEY, conf.getInt(MONTHLY_KILLS_KEY, 0) + num);
		}
	}

	public void saveData(boolean async) {

		long start = System.currentTimeMillis();

		if (async) {
			new Thread(() -> {

				boolean success = save();

				if (success) {
					long end = System.currentTimeMillis();
					KDManager.getPlugin().getLogger()
							.info("Saved " + p.getName() + "'s player data (" + (end - start) + " ms) (Async="
									+ async
									+ ")");
				} else {
					if (KDManager.getPlugin().config.showLogInConsole) {
						KDManager.getPlugin().getLogger()
								.info("Failed to save " + p.getName() + "'s player data (Async=" + async + ")");
					}
				}
			}).start();
		} else {

			boolean success = save();

			if (success) {
				long end = System.currentTimeMillis();
				KDManager.getPlugin().getLogger()
						.info("Saved " + p.getName() + "'s player data (" + (end - start) + " ms) (Async=" + async
								+ ")");
			} else {
				if (KDManager.getPlugin().config.showLogInConsole) {
					KDManager.getPlugin().getLogger()
							.info("Failed to save " + p.getName() + "'s player data (Async=" + async + ")");
				}
			}
		}
	}

	private boolean save() {
		try {
			conf.save(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public int getKills() {
		return conf.getInt(KILLS_KEY, 0);
	}

	public int getDeaths() {
		return conf.getInt(DEATHS_KEY, 0);
	}

	public int getDailyKills() {
		Calendar cal = Calendar.getInstance();
		Calendar now = Calendar.getInstance();

		Date date = new Date(getLastKillLong());

		cal.setTime(date);

		if (cal.get(Calendar.DATE) != now.get(Calendar.DATE) || cal.get(Calendar.MONTH) != now.get(Calendar.MONTH)
				|| cal.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
			return 0;
		}

		return conf.getInt(DAILY_KILLS_KEY, 0);
	}

	public int getMonthlyKills() {
		Calendar cal = Calendar.getInstance();
		Calendar now = Calendar.getInstance();

		Date date = new Date(getLastKillLong());

		cal.setTime(date);

		if (cal.get(Calendar.MONTH) != now.get(Calendar.MONTH) || cal.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
			return 0;
		}

		return conf.getInt(MONTHLY_KILLS_KEY, 0);
	}

	private void loadFile() {

		long start = System.currentTimeMillis();

		File folder = new File(KDManager.getPlugin().getDataFolder(), "PlayerData");

		if (!folder.exists()) {
			folder.mkdirs();
		}

		file = new File(folder, p.getUniqueId().toString() + ".yml");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		conf = YamlConfiguration.loadConfiguration(file);

		conf.set(LAST_NAME_KEY, p.getName());
		saveData(true);

		long end = System.currentTimeMillis();

		KDManager.getPlugin().getLogger().info("Loaded " + p.getName() + "'s data (" + (end - start) + "ms)");
	}
}
