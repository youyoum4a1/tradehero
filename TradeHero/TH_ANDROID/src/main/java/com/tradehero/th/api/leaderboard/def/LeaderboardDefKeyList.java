package com.tradehero.th.api.leaderboard.def;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import java.util.Collection;

public class LeaderboardDefKeyList extends DTOKeyIdList<LeaderboardDefKey>
{
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
