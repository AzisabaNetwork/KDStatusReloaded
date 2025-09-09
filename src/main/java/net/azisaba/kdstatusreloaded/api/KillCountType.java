package net.azisaba.kdstatusreloaded.api;

import java.util.Calendar;

public enum KillCountType {
    DAILY("daily_kills"),
    MONTHLY("monthly_kills"),
    YEARLY("yearly_kills"),
    TOTAL("kills");

    public String columnName;
    KillCountType(String columnName) {
        this.columnName = columnName;
    }

    public long getFirstMilliSecond() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        if (this == DAILY)
            return cal.getTimeInMillis();

        cal.set(Calendar.DATE, 1);

        if (this == MONTHLY)
            return cal.getTimeInMillis();

        cal.set(Calendar.MONTH, 0);

        if (this == YEARLY)
            return cal.getTimeInMillis();

        return -1;
    }
}
