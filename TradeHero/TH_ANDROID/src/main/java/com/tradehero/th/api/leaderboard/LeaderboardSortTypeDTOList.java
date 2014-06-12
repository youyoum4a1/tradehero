package com.tradehero.th.api.leaderboard;

import java.util.ArrayList;
import java.util.Collection;

public class LeaderboardSortTypeDTOList extends ArrayList<LeaderboardSortTypeDTO>
{
    //<editor-fold desc="Constructors">
    public LeaderboardSortTypeDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public LeaderboardSortTypeDTOList()
    {
        super();
    }

    public LeaderboardSortTypeDTOList(Collection<? extends LeaderboardSortTypeDTO> c)
    {
        super(c);
    }
    //</editor-fold>
}
