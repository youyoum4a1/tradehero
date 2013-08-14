/**
 * Config.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 21, 2013
 */
package com.tradehero.th.application;

import com.tradehero.th.R;

public class Config
{

    protected static String localValueForKey(int key)
    {
        return App.getContext().getResources().getString(key);
    }

    public static String getTrendingFeed()
    {
        return localValueForKey(R.string.trending_feed);
    }

    public static String getTrendingChartUrl()
    {
        return localValueForKey(R.string.trending_chart);
    }

    public static String getYahooQuotes()
    {
        return localValueForKey(R.string.yahoo_quotes);
    }

    public static String getTrendRssFeed()
    {
        return localValueForKey(R.string.trend_rss_feed);
    }

    public static String getBuyNewTrend()
    {
        return localValueForKey(R.string.trend_new_buy);
    }

    public static String getTrendNewBuyQuotes()
    {
        return localValueForKey(R.string.trend_new_buy_quotes);
    }

    public static String getTrendSearch()
    {
        return localValueForKey(R.string.trend_search);
    }
}
