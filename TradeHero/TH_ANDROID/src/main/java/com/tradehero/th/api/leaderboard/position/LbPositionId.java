package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.th.api.leaderboard.LeaderboardKey;

/**
 * Created by julien on 1/11/13
 */
public class LbPositionId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = LbPositionId.class.getName() + ".key";

    public LbPositionId(Integer key)
    {
        super(key);
    }

    public LbPositionId(Bundle args)
    {
        super(args);
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof LbPositionId) && equals((LbPositionId) other);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
