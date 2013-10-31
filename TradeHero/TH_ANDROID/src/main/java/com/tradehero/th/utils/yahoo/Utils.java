package com.tradehero.th.utils.yahoo;

/**
 * Created by julien on 9/10/13
 */


public class Utils
{
    public static String getChartURL(String yahooSymbol, ChartSize size, TimeSpan timeSpan)
    {
        return String.format("http://chart.finance.yahoo.com/z?s=%s&t=%s&q=l&z=%s", yahooSymbol, timeSpan.code, size.code);
    }
}
