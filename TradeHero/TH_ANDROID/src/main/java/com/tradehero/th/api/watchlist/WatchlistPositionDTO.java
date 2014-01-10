package com.tradehero.th.api.watchlist;

import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;

/** Created with IntelliJ IDEA. User: tho Date: 12/3/13 Time: 5:50 PM Copyright (c) TradeHero */
public class WatchlistPositionDTO extends PositionDTO
{
    public Double getWatchlistPrice()
    {
        return (Double) get("watchlistPrice");
    }
    public SecurityCompactDTO securityDTO;

}
