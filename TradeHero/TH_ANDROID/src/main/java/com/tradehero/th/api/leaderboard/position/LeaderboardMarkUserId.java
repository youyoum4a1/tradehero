package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import org.jetbrains.annotations.Nullable;

public class LeaderboardMarkUserId extends AbstractIntegerDTOKey
    implements GetPositionsDTOKey
{
    private static final String BUNDLE_KEY = LeaderboardMarkUserId.class.getName() + ".key";

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

    public static boolean isLeaderboardMarkUserId(@Nullable Bundle args)
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
