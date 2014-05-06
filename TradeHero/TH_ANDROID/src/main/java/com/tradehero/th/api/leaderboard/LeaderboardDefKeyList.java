package com.tradehero.th.api.leaderboard;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import java.util.Collection;


public class LeaderboardDefKeyList extends DTOKeyIdList<LeaderboardDefKey>
{
    public static final String TAG = LeaderboardDefKeyList.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public LeaderboardDefKeyList()
    {
        super();
    }

    public LeaderboardDefKeyList(int capacity)
    {
        super(capacity);
    }

    public LeaderboardDefKeyList(Collection<? extends LeaderboardDefKey> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
