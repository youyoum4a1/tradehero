package com.tradehero.th.api.leaderboard.position;

import com.tradehero.common.persistence.DTOKey;

public class LeaderboardFriendsKey implements DTOKey
{
    public LeaderboardFriendsKey()
    {
        super();
    }

    @Override public boolean equals(Object o)
    {
        return o instanceof LeaderboardFriendsKey;
    }

    @Override public int hashCode()
    {
        return 0;
    }
}
