/**
 * Config.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 21, 2013
 */
package android.tradehero.application;

import android.tradehero.activities.R;


public class Config {
	
	
	protected static String localValueForKey(int key) {
		return App.getContext().getResources().getString(key);
	}
	
	public static String getTrendingFeed() {
		return localValueForKey(R.string.trending_feed);
	}
	
	public static String getTrendingChartUrl() {
		return localValueForKey(R.string.trending_chart);
	}
	
	public static String getYahooQuotes() {
		return localValueForKey(R.string.yahoo_quotes);
	}
	
	public static String getTrendRssFeed() {
		return localValueForKey(R.string.trend_rss_feed);
	}
	
	public static String getBuyTrend() {
		return localValueForKey(R.string.trend_buy);
	}
	
	public static String getTradeofWeekfeeds() {
		return localValueForKey(R.string.trend_buy);
	}
	
}
