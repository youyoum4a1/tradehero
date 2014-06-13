package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractStringDTOKey;

public class LeaderboardDefListKey extends AbstractStringDTOKey
{
    static final String BUNDLE_KEY_KEY = LeaderboardDefKey.class.getName() + ".key";

    private static final String ALL = "all";

    //<editor-fold desc="Constructors">
    public LeaderboardDefListKey()
    {
        super(ALL);
    }

    public LeaderboardDefListKey(String key)
    {
        super(key);
    }

    public LeaderboardDefListKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
