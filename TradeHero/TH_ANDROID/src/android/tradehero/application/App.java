/**
 * App.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 21, 2013
 */
package android.tradehero.application;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.app.Application;
import android.content.Context;
import android.tradehero.models.ProfileDTO;
import android.tradehero.models.Token;
import android.tradehero.models.TradeofWeek;
import android.tradehero.models.Trend;

public class App extends Application {

	private static App instance;

	private Trend trend;
	private Token token;
	private ArrayList<TradeofWeek> tradeofweek;

	public ArrayList<TradeofWeek> getTradeofweek() {
		return tradeofweek;
	}

	public void setTradeofweek(TradeofWeek tradeofweek) {
		this.tradeofweek.add(tradeofweek);
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}
	private ProfileDTO profileDTO;

	public ProfileDTO getProfileDTO() {
		return profileDTO;
	}

	public void setProfileDTO(ProfileDTO profileDTO) {
		this.profileDTO = profileDTO;
	}
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
