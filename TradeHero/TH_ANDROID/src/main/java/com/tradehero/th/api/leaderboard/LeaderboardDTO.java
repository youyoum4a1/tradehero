package com.tradehero.th.api.leaderboard;

import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 2:08 PM Copyright (c) TradeHero */
public class LeaderboardDTO
{
    public static final String LEADERBOARD_ID = "LEADERBOARD_ID";
    public static final String INCLUDE_FOF = "INCLUDE_FOF";

    public int id;
    public String name;
    public List<LeaderboardUserDTO> users;
    public int userIsAtPositionZeroBased;
    public Date markUtc;

    public LeaderboardDTO()
    {
        super();
    }
}