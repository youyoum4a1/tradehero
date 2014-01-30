package com.tradehero.th.models.chart.yahoo;

import java.util.List;

/**
 * Created by xavier on 1/30/14.
 */
public enum YahooMovingAverageInterval
{
    m5("m5", 5),
    m10("m10", 10),
    m20("m20", 20),
    m50("m50", 50),
    m100("m100", 100),
    m200("m200", 200);

    public static final String CONCAT_SEPARATOR = ",";

    public final String code;
    public final int days;

    private YahooMovingAverageInterval(String code, int days)
    {
        this.code = code;
        this.days = days;
    }

    public static String concat(List<YahooMovingAverageInterval> movingAverageIntervals)
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
