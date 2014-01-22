package com.tradehero.th.api.leaderboard;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractLongDTOKey;

/**
 * Created by xavier on 1/22/14.
 */
public class LeaderboardUserId extends AbstractLongDTOKey
{
    public static final String TAG = LeaderboardUserId.class.getSimpleName();
    public static final String BUNDLE_KEY_KEY = LeaderboardUserId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public LeaderboardUserId(Long key)
    {
        super(key);
    }

    public LeaderboardUserId(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
