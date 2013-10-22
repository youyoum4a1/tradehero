package com.tradehero.th.api.leaderboard;

import com.tradehero.th.api.DTOKeyIdList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderListKey;
import java.util.Collection;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 7:41 PM To change this template use File | Settings | File Templates. */
public class LeaderboardDefKeyList extends DTOKeyIdList<LeaderboardDefListKey, LeaderboardDefKey>
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
