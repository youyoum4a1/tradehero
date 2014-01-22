package com.tradehero.th.api.leaderboard;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/**
 * Created by xavier on 1/22/14.
 */
public class LeaderboardKey extends AbstractIntegerDTOKey
{
    public static final String TAG = LeaderboardKey.class.getSimpleName();
    public static final String BUNDLE_KEY_KEY = LeaderboardKey.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public LeaderboardKey(Integer key)
    {
        super(key);
    }

    public LeaderboardKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
