package com.tradehero.th.models.chart.yahoo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

public enum YahooMovingAverageInterval
{
    m5("m5", 5),
    m10("m10", 10),
    m20("m20", 20),
    m50("m50", 50),
    m100("m100", 100),
    m200("m200", 200);

    public static final String CONCAT_SEPARATOR = ",";

    @NonNull public final String code;
    public final int days;

    //<editor-fold desc="Constructors">
    YahooMovingAverageInterval(@NonNull String code, int days)
    {
        this.code = code;
        this.days = days;
    }
    //</editor-fold>

    @NonNull public static String concat(@Nullable List<YahooMovingAverageInterval> movingAverageIntervals)
    {
        if (movingAverageIntervals == null)
        {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        String separator = "";
        for (YahooMovingAverageInterval movingAverageInterval : movingAverageIntervals)
        {
            builder.append(separator).append(movingAverageInterval.code);
            separator = CONCAT_SEPARATOR;
        }
        return builder.toString();
    }
}
