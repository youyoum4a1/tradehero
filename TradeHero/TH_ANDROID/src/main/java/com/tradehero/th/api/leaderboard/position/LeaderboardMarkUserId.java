package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.th.api.leaderboard.LeaderboardKey;

/**
 * Created by julien on 1/11/13
 */
public class LeaderboardMarkUserId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = LeaderboardMarkUserId.class.getName() + ".key";

    public LeaderboardMarkUserId(Integer key)
    {
        super(key);
    }

    public LeaderboardMarkUserId(Bundle args)
    {
        super(args);
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof LeaderboardKey) && equals((LeaderboardKey) other);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }

    public boolean isValid()
    {
        return key != null;
    }
}
