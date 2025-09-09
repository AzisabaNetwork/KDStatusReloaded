package net.azisaba.kdstatusreloaded.playerkd.model;

import org.jspecify.annotations.NullMarked;

import java.beans.ConstructorProperties;
import java.util.UUID;

@NullMarked
public class KDUserData implements Cloneable {
    public final UUID uuid;
    public String name;
    public int totalKills, dailyKills, monthlyKills, yearlyKills;
    public int deaths;
    public long lastUpdated;

    public KDUserData(UUID uuid, String name) {
        this(uuid, name, 0, 0, 0, 0, 0, -1);
    }

    @ConstructorProperties({"uuid", "name", "kills", "daily_kills", "monthly_kills", "yearly_kills", "deaths", "last_updated"})
    public KDUserData(UUID uuid, String name, int totalKills, int dailyKills, int monthlyKills, int yearlyKills, int deaths, long lastUpdated) {
        this.uuid = uuid;
        this.name = name;
        this.totalKills = totalKills;
        this.dailyKills = dailyKills;
        this.monthlyKills = monthlyKills;
        this.yearlyKills = yearlyKills;
        this.deaths = deaths;
        this.lastUpdated = lastUpdated;
    }

    @Override
    public KDUserData clone() {
        try {
            KDUserData clone = (KDUserData) super.clone();
            clone.name = name;
            clone.totalKills = totalKills;
            clone.dailyKills = dailyKills;
            clone.monthlyKills = monthlyKills;
            clone.yearlyKills = yearlyKills;
            clone.deaths = deaths;
            clone.lastUpdated = lastUpdated;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
