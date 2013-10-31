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

    public static ChartSize getPreferredSize(int pixelWidth, int pixelHeight)
    {
        // TODO refine
        if (pixelWidth >= ChartSize.large.yahooPixelWidth && pixelHeight >= ChartSize.large.yahooPixelHeight)
        {
            return ChartSize.large;
        }
        if (pixelWidth >= ChartSize.medium.yahooPixelWidth && pixelHeight >= ChartSize.medium.yahooPixelHeight)
        {
            return ChartSize.medium;
        }
        return ChartSize.small;
    }
}
