package com.tradehero.th.api.leaderboard;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 2:08 PM Copyright (c) TradeHero */
public class LeaderboardDTO implements DTO
{
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

    public LeaderboardDTO(int id, String name, List<LeaderboardUserDTO> users, int userIsAtPositionZeroBased, Date markUtc)
    {
        this.id = id;
        this.name = name;
        this.users = users;
        this.userIsAtPositionZeroBased = userIsAtPositionZeroBased;
        this.markUtc = markUtc;
    }

    public LeaderboardKey getLeaderboardKey()
    {
        return new LeaderboardKey(id);
    }
}