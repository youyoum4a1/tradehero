package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import org.jetbrains.annotations.NotNull;

public class LeaderboardMarkUserId extends AbstractIntegerDTOKey
    implements GetPositionsDTOKey
{
    private static final String BUNDLE_KEY = LeaderboardMarkUserId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserId(int key)
    {
        super(key);
    }

    public LeaderboardMarkUserId(@NotNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    public static boolean isLeaderboardMarkUserId(@NotNull Bundle args)
    {
        return args.containsKey(BUNDLE_KEY);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }

    public boolean isValid()
    {
        return true;
    }
}
