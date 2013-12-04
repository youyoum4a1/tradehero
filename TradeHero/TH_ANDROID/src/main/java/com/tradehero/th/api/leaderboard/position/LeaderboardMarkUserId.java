package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/**
 * Created by julien on 1/11/13
 */
public class LeaderboardMarkUserId extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY = LeaderboardMarkUserId.class.getName() + ".key";

    public LeaderboardMarkUserId(Integer key)
    {
        super(key);
    }

    public LeaderboardMarkUserId(Bundle args)
    {
        super(args);
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
