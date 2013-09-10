/**
 * App.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 21, 2013
 */
package com.tradehero.th.application;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.THUser;
import com.tradehero.th.models.Token;
import com.tradehero.th.models.TradeOfWeek;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class App extends Application
{
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
}
