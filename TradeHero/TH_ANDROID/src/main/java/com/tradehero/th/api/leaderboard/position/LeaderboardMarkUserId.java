package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.th.api.position.GetPositionsDTOKey;

public class LeaderboardMarkUserId extends AbstractIntegerDTOKey
    implements GetPositionsDTOKey
{
    public static final String BUNDLE_KEY = LeaderboardMarkUserId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserId(Integer key)
    {
        super(key);
    }

    public LeaderboardMarkUserId(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    public static boolean isLeaderboardMarkUserId(Bundle args)
    {
        return args != null &&
                args.containsKey(BUNDLE_KEY);
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
