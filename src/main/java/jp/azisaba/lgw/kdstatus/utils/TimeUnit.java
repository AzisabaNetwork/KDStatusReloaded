package jp.azisaba.lgw.kdstatus.utils;

import java.util.Calendar;

public enum TimeUnit {
    LIFETIME("kills"),
    DAILY("daily_kills"),
    MONTHLY("monthly_kills"),
    YEARLY("yearly_kills");

    private final String sqlColumnName;

    TimeUnit(String sqlColumnName){
        this.sqlColumnName = sqlColumnName;
    }

    public String getSqlColumnName() {
        return sqlColumnName;
    }

    public static long getFirstMilliSecond(TimeUnit unit) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        if ( unit == TimeUnit.DAILY )
            return cal.getTimeInMillis();

        cal.set(Calendar.DATE, 1);

        if ( unit == TimeUnit.MONTHLY )
            return cal.getTimeInMillis();

        cal.set(Calendar.MONTH, 0);

        if ( unit == TimeUnit.YEARLY )
            return cal.getTimeInMillis();

        return -1;
    }
}