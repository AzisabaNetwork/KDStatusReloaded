package jp.azisaba.lgw.kdstatus.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public enum TimeUnit {
    LIFETIME("kills"),
    DAILY("daily_kills"),
    MONTHLY("monthly_kills"),
    YEARLY("yearly_kills");

    @Getter
    private final String sqlColumnName;

    TimeUnit(String sqlColumnName){
        this.sqlColumnName = sqlColumnName;
    }

    public String getSqlColumnName() {
        return sqlColumnName;
    }
}