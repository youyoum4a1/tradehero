package com.androidth.general.api.leaderboard.position;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.AbstractIntegerDTOKey;
import com.androidth.general.api.position.GetPositionsDTOKey;

public class LeaderboardMarkUserId extends AbstractIntegerDTOKey
    implements GetPositionsDTOKey
{
    private static final String BUNDLE_KEY = LeaderboardMarkUserId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserId(int key)
    {
        super(key);
    }

    public LeaderboardMarkUserId(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    public static boolean isLeaderboardMarkUserId(@NonNull Bundle args)
    {
        return args.containsKey(BUNDLE_KEY);
    }

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }

    public boolean isValid()
    {
        return true;
    }
}
