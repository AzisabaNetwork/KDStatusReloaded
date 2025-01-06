package jp.azisaba.lgw.kdstatus.sql;

import java.util.UUID;

public class KillRankingData {

    private final UUID uuid;
    private final String name;
    private final int kills;

    public KillRankingData(UUID uuid, String name, int kills) {
        this.uuid = uuid;
        this.name = name;
        this.kills = kills;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getKills() {
        return kills;
    }
}
