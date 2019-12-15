package jp.azisaba.lgw.kdstatus.sql;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class KillRankingData {

    private final UUID uuid;
    private final String name;
    private final int kills;

}
