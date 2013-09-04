/**
 * App.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 21, 2013
 */
package com.tradehero.th.application;

import android.content.Context;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.THUser;
import com.tradehero.th.models.ProfileDTO;
import com.tradehero.th.models.Token;
import com.tradehero.th.models.TradeOfWeek;
import com.tradehero.th.models.Trend;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class App extends Application
{
    private Trend trend;
    private Token token;

    private ArrayList<TradeOfWeek> tradeOfWeek;
    private LinkedHashMap<String, String> yahooQuotesMap;

    public ArrayList<TradeOfWeek> getTradeOfWeek()
    {
        return tradeOfWeek;
    }

    public void setTradeOfWeek(TradeOfWeek tradeOfWeek)
    {
        this.tradeOfWeek.add(tradeOfWeek);
    }

    public UserProfileDTO getProfileDTO()
    {
        return THUser.getCurrentUser();
    }

    public void setYahooQuotesMap(LinkedHashMap<String, String> yahooQuotesMap)
    {
        this.yahooQuotesMap = yahooQuotesMap;
    }

     public Trend getTrend()
    {
        return trend;
    }

    public void setTrend(Trend trend)
    {
        this.trend = trend;
    }
}
