package com.tradehero.th.api.leaderboard.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import java.util.Collection;

public class LeaderboardUserIdList extends BaseArrayList<LeaderboardUserId>
    implements DTO
{
    //<editor-fold desc="Constructors">
    public LeaderboardUserIdList()
    {
        super();
    }

    public LeaderboardUserIdList(
            @NonNull Collection<? extends LeaderboardUserId> c,
            @Nullable LeaderboardUserId typeQualifier)
    {
        super(c);
    }

    public LeaderboardUserIdList(
            @NonNull Collection<? extends LeaderboardUserDTO> c,
            @Nullable LeaderboardUserDTO typeQualifier)
    {
        super();
        for (LeaderboardUserDTO stocksLeaderboardUserDTO : c)
        {
            add(stocksLeaderboardUserDTO.getLeaderboardUserId());
        }
    }
    //</editor-fold>
}
