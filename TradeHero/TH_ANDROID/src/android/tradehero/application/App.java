/**
 * App.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 21, 2013
 */
package android.tradehero.application;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.tradehero.models.Trend;

public class App extends Application {
	
	private static App instance;
	
	private Trend trend;
	private LinkedHashMap<String, String> yahooQuotesMap;
	
	public static Context getContext() {
		return instance;
	}
	
	public App() {
		super();
		instance = this;
	}
	
	public LinkedHashMap<String, String> getYahooQuotesMap() {
		return yahooQuotesMap;
	}

	public void setYahooQuotesMap(LinkedHashMap<String, String> yahooQuotesMap) {
		this.yahooQuotesMap = yahooQuotesMap;
	}

	public Trend getTrend() {
		return trend;
	}

	public void setTrend(Trend trend) {
		this.trend = trend;
	}

	

	
	
	
	

}
