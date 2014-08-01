package com.tradehero.th.api.leaderboard.def;

import com.android.internal.util.Predicate;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import org.jetbrains.annotations.NotNull;

public class LeaderboardDefDTOList extends BaseArrayList<LeaderboardDefDTO>
    implements DTO
{
    //<editor-fold desc="Contructors">
    public LeaderboardDefDTOList()
    {
        super();
    }
    //</editor-fold>

    @NotNull
    public LeaderboardDefDTOList keepForCountryCode(@NotNull String countryCode)
    {
        Predicate<LeaderboardDefDTO> leaderboardDefDTOPredicate = new LeaderboardDefDTOCountryCodeContainPredicate(countryCode);
        LeaderboardDefDTOList kept = new LeaderboardDefDTOList();
        for (LeaderboardDefDTO leaderboardDefDTO : this)
        {
            if (leaderboardDefDTOPredicate.apply(leaderboardDefDTO))
            {
                kept.add(leaderboardDefDTO);
            }
        }
        return kept;
    }

    @NotNull public LeaderboardDefKeyList createKeys()
    {
        LeaderboardDefKeyList list = new LeaderboardDefKeyList();
        for (@NotNull LeaderboardDefDTO leaderboardDefDTO : this)
        {
            list.add(leaderboardDefDTO.getLeaderboardDefKey());
        }
        return list;
    }
}
