package net.azisaba.kdstatusreloaded.api;

import java.util.UUID;

public class KDUserData {
    public final UUID uuid;
    public String name;
    public int totalKills, dailyKills, monthlyKills, yearlyKills;
    public int deaths;

    public KDUserData(UUID uuid, String name, int totalKills, int dailyKills, int monthlyKills, int yearlyKills, int deaths) {
        this.uuid = uuid;
        this.name = name;
        this.totalKills = totalKills;
        this.dailyKills = dailyKills;
        this.monthlyKills = monthlyKills;
        this.yearlyKills = yearlyKills;
        this.deaths = deaths;
    }
}
