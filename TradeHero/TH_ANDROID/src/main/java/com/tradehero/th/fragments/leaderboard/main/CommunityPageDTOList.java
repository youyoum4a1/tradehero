package com.tradehero.th.fragments.leaderboard.main;

import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

class CommunityPageDTOList extends ArrayList<CommunityPageDTO>
{
    //<editor-fold desc="Constructors">
    public CommunityPageDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public CommunityPageDTOList()
    {
        super();
    }

    public CommunityPageDTOList(Collection<? extends CommunityPageDTO> c)
    {
        super(c);
    }
    //</editor-fold>

    public boolean add(@NotNull LeaderboardDefDTO leaderboardDefDTO)
    {
        return add(new LeaderboardDefCommunityPageDTO(leaderboardDefDTO));
    }

    public boolean addAllLeaderboardDefDTO(@NotNull Collection<? extends LeaderboardDefDTO> leaderboardDefDTOs)
    {
        boolean changed = false;
        for (LeaderboardDefDTO leaderboardDefDTO : leaderboardDefDTOs)
        {
            changed |= add(leaderboardDefDTO);
        }
        return changed;
    }
}
