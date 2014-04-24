package com.tradehero.th.api.leaderboard;

import java.util.Date;
import java.util.List;

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