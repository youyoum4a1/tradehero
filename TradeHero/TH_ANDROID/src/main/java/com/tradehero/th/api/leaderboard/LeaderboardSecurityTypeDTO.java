package com.tradehero.th.api.leaderboard;

import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 2:09 PM Copyright (c) TradeHero */
public class LeaderboardSecurityTypeDTO
{
    public List<String> restrictByExchanges;
    public List<Integer> restrictedBySectorIds;
    public Date fromUtc;
    public Date toUtc;

    public LeaderboardSecurityTypeDTO()
    {
        super();
    }
}