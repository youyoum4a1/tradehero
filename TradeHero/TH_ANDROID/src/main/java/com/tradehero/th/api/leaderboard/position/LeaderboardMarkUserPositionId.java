package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/**
 * Created by julien on 1/11/13
 */
public class LeaderboardMarkUserPositionId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = LeaderboardMarkUserPositionId.class.getName() + ".key";

    public LeaderboardMarkUserPositionId(Integer key)
    {
        super(key);
    }

    public LeaderboardMarkUserPositionId(Bundle args)
    {
        super(args);
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof LeaderboardMarkUserPositionId) && equals((LeaderboardMarkUserPositionId) other);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
