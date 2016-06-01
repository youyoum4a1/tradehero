package com.ayondo.academy.api.leaderboard.position;

import android.support.annotation.NonNull;
import com.tradehero.common.api.PagedDTOKey;

public class LeaderboardFriendsKey implements PagedDTOKey
{
    @NonNull public final Integer page;

    //<editor-fold desc="Constructors">
    public LeaderboardFriendsKey()
    {
        this(1);
    }

    public LeaderboardFriendsKey(@NonNull Integer page)
    {
        this.page = page;
    }
    //</editor-fold>

    @Override public boolean equals(Object o)
    {
        return o instanceof LeaderboardFriendsKey;
    }

    @Override public int hashCode()
    {
        if (page == null)
        {
            return "null".hashCode();
        }
        else
        {
            return page.hashCode();
        }
    }

    @NonNull @Override public Integer getPage()
    {
        return page;
    }
}
