package com.tradehero.th.models.chart.yahoo;

import com.tradehero.th.models.chart.ChartDTO;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xavier on 1/30/14.
 */
public class YahooChartDTO implements ChartDTO
{
    public static final String TAG = YahooChartDTO.class.getSimpleName();

    public String yahooSymbol;
    public YahooChartSize size;
    public YahooTimeSpan timeSpan;
    public List<YahooMovingAverageInterval> movingAverageIntervals;

    public YahooChartDTO(String yahooSymbol, YahooChartSize size, YahooTimeSpan timeSpan,
            List<YahooMovingAverageInterval> movingAverageIntervals)
    {
        this.yahooSymbol = yahooSymbol;
        this.size = size;
        this.timeSpan = timeSpan;
        this.movingAverageIntervals = movingAverageIntervals;
    }

    public YahooChartDTO(String yahooSymbol, YahooChartSize size, YahooTimeSpan timeSpan)
    {
        this.yahooSymbol = yahooSymbol;
        this.size = size;
        this.timeSpan = timeSpan;
        this.movingAverageIntervals = defaultMovingAverageIntervals();
    }

    public static List<YahooMovingAverageInterval> defaultMovingAverageIntervals()
    {
        ArrayList<YahooMovingAverageInterval> created = new ArrayList<>();
        created.add(YahooMovingAverageInterval.m50);
        created.add(YahooMovingAverageInterval.m200);
        return created;
    }

    public String getChartUrl()
    {
        return String.format(
                "http://chart.finance.yahoo.com/z?s=%s&t=%s&q=l&z=%s&p=%s",
                yahooSymbol,
                timeSpan.code,
                size.code,
                YahooMovingAverageInterval.concat(movingAverageIntervals));
    }
}
